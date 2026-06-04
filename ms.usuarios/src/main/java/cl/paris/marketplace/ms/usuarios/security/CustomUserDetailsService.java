package cl.paris.marketplace.ms.usuarios.security;

import java.util.Collections;

import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cl.paris.marketplace.ms.usuarios.model.Usuario;
import cl.paris.marketplace.ms.usuarios.repository.UsuarioRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el email: " + email));

        if (Boolean.TRUE.equals(usuario.getBaneado())) {
            throw new LockedException("Acceso denegado: Esta cuenta ha sido suspendida por la administración de la plataforma.");
        }

        String nombreRol = usuario.getRol().getNombreRol();
        if (nombreRol != null && !nombreRol.startsWith("ROLE_")) {
            nombreRol = "ROLE_" + nombreRol;
        }
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(nombreRol);

        return new User(
                usuario.getEmail(),
                usuario.getPasswordHash(), 
                Collections.singletonList(authority) 
        );
    }
}