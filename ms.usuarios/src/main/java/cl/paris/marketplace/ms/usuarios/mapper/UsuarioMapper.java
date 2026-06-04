package cl.paris.marketplace.ms.usuarios.mapper;

import org.springframework.stereotype.Component;

import cl.paris.marketplace.ms.usuarios.dto.PerfilRequest;
import cl.paris.marketplace.ms.usuarios.dto.PerfilResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioRequest;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioResponse;
import cl.paris.marketplace.ms.usuarios.model.Perfil;
import cl.paris.marketplace.ms.usuarios.model.Rol;
import cl.paris.marketplace.ms.usuarios.model.Usuario;

@Component
public class UsuarioMapper {

    // ==========================================
    // MAPEO DE USUARIOS
    // ==========================================
    public Usuario toUsuarioEntity(UsuarioRequest request, Rol rol) {
        Usuario usuario = new Usuario();
        usuario.setEmail(request.email());
        usuario.setRol(rol);
        usuario.setBaneado(false); 
        return usuario;
    }

    public UsuarioResponse toUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRol().getNombreRol(),
                usuario.getBaneado(), 
                usuario.getFechaIngreso() // Devolvemos la fecha original al JSON
        );
    }

    // ==========================================
    // MAPEO DE PERFILES
    // ==========================================
    public Perfil toPerfilEntity(PerfilRequest request, Usuario usuario) {
        Perfil perfil = new Perfil();
        perfil.setUsuario(usuario);
        perfil.setPrimerNombre(request.primerNombre());
        perfil.setSegundoNombre(request.segundoNombre());
        perfil.setPrimerApellido(request.primerApellido());
        perfil.setSegundoApellido(request.segundoApellido());
        perfil.setTelefono(request.telefono());
        return perfil;
    }

    public PerfilResponse toPerfilResponse(Perfil perfil) {
        return new PerfilResponse(
                perfil.getId(),
                perfil.getPrimerNombre(),
                perfil.getSegundoNombre(),
                perfil.getPrimerApellido(),
                perfil.getSegundoApellido(),
                perfil.getTelefono()
        );
    }
}