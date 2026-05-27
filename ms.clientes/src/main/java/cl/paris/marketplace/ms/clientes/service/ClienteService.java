package cl.paris.marketplace.ms.clientes.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.dto.PerfilRequest;
import cl.paris.marketplace.ms.clientes.dto.PerfilResponse;
import cl.paris.marketplace.ms.clientes.dto.RolRequest;
import cl.paris.marketplace.ms.clientes.dto.RolResponse;
import cl.paris.marketplace.ms.clientes.dto.UsuarioCompletoResponse;
import cl.paris.marketplace.ms.clientes.dto.UsuarioRequest;
import cl.paris.marketplace.ms.clientes.dto.UsuarioResponse;
import cl.paris.marketplace.ms.clientes.mapper.ClienteMapper;
import cl.paris.marketplace.ms.clientes.model.MetodoPago;
import cl.paris.marketplace.ms.clientes.model.Perfil;
import cl.paris.marketplace.ms.clientes.model.Rol;
import cl.paris.marketplace.ms.clientes.model.Usuario;
import cl.paris.marketplace.ms.clientes.repository.MetodoPagoRepository;
import cl.paris.marketplace.ms.clientes.repository.PerfilRepository;
import cl.paris.marketplace.ms.clientes.repository.RolRepository;
import cl.paris.marketplace.ms.clientes.repository.UsuarioRepository;

@Service
public class ClienteService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PerfilRepository perfilRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final ClienteMapper clienteMapper;

    // Inyección por constructor (Garantiza que Spring cargue todos los componentes)
    public ClienteService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PerfilRepository perfilRepository,
            MetodoPagoRepository metodoPagoRepository,
            ClienteMapper clienteMapper) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.perfilRepository = perfilRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.clienteMapper = clienteMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: USUARIOS
    // ==========================================
    @Transactional
    public UsuarioResponse registrarUsuario(UsuarioRequest request) {
        // 1. Validación de negocio: Evitar correos duplicados
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new RuntimeException("El correo electrónico ya se encuentra registrado.");
        }

        // 2. Buscar el Rol en la base de datos
        Rol rol = rolRepository.findById(request.rolId())
                .orElseThrow(() -> new RuntimeException("El Rol especificado no existe."));

        // 3. Transformar DTO a Entidad usando el Mapper
        Usuario usuario = clienteMapper.toUsuarioEntity(request, rol);

        // NOTA ACADÉMICA: Cuando implementes Spring Security en la Fase 3, 
        // aquí debes encriptar la contraseña antes del save:
        // usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        // 4. Guardar en PostgreSQL y responder con el DTO seguro
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return clienteMapper.toUsuarioResponse(usuarioGuardado);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        return clienteMapper.toUsuarioResponse(usuario);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: PERFIL
    // ==========================================
    @Transactional
    public PerfilResponse crearPerfil(PerfilRequest request) {
        // 1. Validar que el usuario exista para amarrar el perfil
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("No se puede crear el perfil: Usuario no encontrado."));

        // 2. Validar relación 1 a 1: Que no tenga ya un perfil creado
        if (perfilRepository.findByUsuarioId(request.usuarioId()).isPresent()) {
            throw new RuntimeException("El usuario ya cuenta con un perfil asociado.");
        }

        // 3. Mapear y guardar
        Perfil perfil = clienteMapper.toPerfilEntity(request, usuario);
        Perfil perfilGuardado = perfilRepository.save(perfil);
        return clienteMapper.toPerfilResponse(perfilGuardado);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: MÉTODOS DE PAGO
    // ==========================================
    @Transactional
    public MetodoPagoResponse agregarMetodoPago(MetodoPagoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        MetodoPago metodoPago = clienteMapper.toMetodoPagoEntity(request, usuario);
        MetodoPago metodoPagoGuardado = metodoPagoRepository.save(metodoPago);
        return clienteMapper.toMetodoPagoResponse(metodoPagoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MetodoPagoResponse> listarMetodosPagoUsuario(UUID usuarioId) {
        return metodoPagoRepository.findByUsuarioId(usuarioId).stream()
                .map(clienteMapper::toMetodoPagoResponse)
                .collect(Collectors.toList());
    }
// ==========================================
    // LÓGICA DE NEGOCIO: ROLES
    // ==========================================

    @Transactional
    public RolResponse crearRol(RolRequest request) {
        // Validar si el rol ya existe para no tener duplicados
        if (rolRepository.findByNombreRol(request.nombreRol()).isPresent()) {
            throw new RuntimeException("El rol ya existe en el sistema.");
        }

        Rol rol = new Rol();
        rol.setNombreRol(request.nombreRol());

        Rol rolGuardado = rolRepository.save(rol);

        return new RolResponse(rolGuardado.getId(), rolGuardado.getNombreRol());
    }
    // ==========================================
    // LÓGICA DE NEGOCIO: ROLES (Agrega esto debajo del método crearRol)
    // ==========================================

    @Transactional(readOnly = true)
    public java.util.List<RolResponse> listarRoles() {
        return rolRepository.findAll().stream()
                .map(rol -> new RolResponse(rol.getId(), rol.getNombreRol()))
                .toList(); // .toList() es nativo y más limpio en Java moderno
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: PERFIL (Agrega esto debajo del método crearPerfil)
    // ==========================================
    @Transactional(readOnly = true)
    public PerfilResponse obtenerPerfilPorUsuarioId(UUID usuarioId) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para el usuario especificado."));
        return clienteMapper.toPerfilResponse(perfil);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: VISTA CONSOLIDADA
    // ==========================================
    @Transactional(readOnly = true)
    public UsuarioCompletoResponse obtenerUsuarioCompleto(UUID usuarioId) {
        // 1. Obtener el Usuario (Si no existe, aquí sí debe detenerse)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));

        // 2. Mapear el Rol
        RolResponse rolResponse = new RolResponse(usuario.getRol().getId(), usuario.getRol().getNombreRol());

        // 3. Buscar el Perfil de forma segura
        PerfilResponse perfilResponse = perfilRepository.findByUsuarioId(usuarioId)
                .map(clienteMapper::toPerfilResponse)
                .orElse(null); // Si aún no ha llenado su perfil, simplemente será null

        // 4. Buscar las tarjetas (Aprovechamos el método que ya tenías hecho)
        List<MetodoPagoResponse> metodosPagoResponse = listarMetodosPagoUsuario(usuarioId);

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
