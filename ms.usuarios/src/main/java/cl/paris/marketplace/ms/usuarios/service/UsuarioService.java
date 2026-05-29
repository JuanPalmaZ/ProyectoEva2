package cl.paris.marketplace.ms.usuarios.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.usuarios.client.MetodoPagoClient;
import cl.paris.marketplace.ms.usuarios.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.usuarios.dto.PerfilRequest;
import cl.paris.marketplace.ms.usuarios.dto.PerfilResponse;
import cl.paris.marketplace.ms.usuarios.dto.RolRequest;
import cl.paris.marketplace.ms.usuarios.dto.RolResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioCompletoResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioRequest;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioResponse;
import cl.paris.marketplace.ms.usuarios.mapper.UsuarioMapper;
import cl.paris.marketplace.ms.usuarios.model.Perfil;
import cl.paris.marketplace.ms.usuarios.model.Rol;
import cl.paris.marketplace.ms.usuarios.model.Usuario;
import cl.paris.marketplace.ms.usuarios.repository.PerfilRepository;
import cl.paris.marketplace.ms.usuarios.repository.RolRepository;
import cl.paris.marketplace.ms.usuarios.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioMapper usuarioMapper;
    
    // El cliente Feign para comunicarse con ms-clientes
    private final MetodoPagoClient metodoPagoClient;

    // Inyección por constructor
    public UsuarioService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PerfilRepository perfilRepository,
            UsuarioMapper usuarioMapper,
            MetodoPagoClient metodoPagoClient) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.perfilRepository = perfilRepository;
        this.usuarioMapper = usuarioMapper;
        this.metodoPagoClient = metodoPagoClient;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: USUARIOS
    // ==========================================
    
    @Transactional
    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El correo electrónico ya se encuentra registrado.");
        }

        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new RuntimeException("El Rol especificado no existe."));

        Usuario usuario = usuarioMapper.toUsuarioEntity(request, rol);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        return usuarioMapper.toUsuarioResponse(usuarioGuardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return usuarioMapper.toUsuarioResponse(usuario);
    }

    // Método agregado para soportar el buscador del controlador
    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarUsuarios(String email) {
        List<Usuario> usuarios;

        if (email == null || email.trim().isEmpty()) {
            usuarios = usuarioRepository.findAll(); 
        } else {
            usuarios = usuarioRepository.findByEmailContainingIgnoreCase(email); 
        }

        return usuarios.stream()
                .map(usuarioMapper::toUsuarioResponse)
                .toList();
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: PERFIL
    // ==========================================
    
    @Transactional
    public PerfilResponse crearPerfil(PerfilRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("No se puede crear el perfil: Usuario no encontrado."));

        if (perfilRepository.findByUsuarioId(request.usuarioId()).isPresent()) {
            throw new RuntimeException("El usuario ya cuenta con un perfil asociado.");
        }

        Perfil perfil = usuarioMapper.toPerfilEntity(request, usuario);
        Perfil perfilGuardado = perfilRepository.save(perfil);
        return usuarioMapper.toPerfilResponse(perfilGuardado);
    }

    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfilPorUsuarioId(UUID usuarioId) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para el usuario especificado."));
        return usuarioMapper.toPerfilResponse(perfil);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: ROLES
    // ==========================================

    @Transactional
    public RolResponse crearRol(RolRequest request) {
        if (rolRepository.findByNombreRol(request.nombreRol()).isPresent()) {
            throw new RuntimeException("El rol ya existe en el sistema.");
        }

        Rol rol = new Rol();
        rol.setNombreRol(request.nombreRol());
        Rol rolGuardado = rolRepository.save(rol);

        return new RolResponse(rolGuardado.getId(), rolGuardado.getNombreRol());
    }

    @Transactional(readOnly = true)
    public List<RolResponse> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> new RolResponse(rol.getId(), rol.getNombreRol()))
                .toList(); 
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: VISTA CONSOLIDADA (CON FEIGN)
    // ==========================================
    
    @Transactional(readOnly = true)
    public UsuarioCompletoResponse obtenerUsuarioCompleto(UUID usuarioId) {
        // 1. Obtener el Usuario
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 2. Mapear el Rol
        RolResponse rolResponse = new RolResponse(usuario.getRol().getId(), usuario.getRol().getNombreRol());

        // 3. Buscar el Perfil de forma segura
        PerfilResponse perfilResponse = perfilRepository.findByUsuarioId(usuarioId)
                .map(usuarioMapper::toPerfilResponse)
                .orElse(null); 

        // 4. Buscar las tarjetas usando OpenFeign hacia ms-clientes
        List<MetodoPagoResponse> metodosPagoResponse;
        try {
            metodosPagoResponse = metodoPagoClient.obtenerMetodosPagoUsuario(usuarioId);
        } catch (Exception e) {
            // Si el microservicio de clientes está caído, devolvemos una lista vacía
            // en lugar de tumbar todo el sistema de usuarios.
            metodosPagoResponse = java.util.Collections.emptyList();
        }

        // 5. Ensamblar todo el paquete
        return new UsuarioCompletoResponse(
                usuario.getId(),
                usuario.getEmail(),
                rolResponse,
                perfilResponse,
                metodosPagoResponse
        );
    }
}