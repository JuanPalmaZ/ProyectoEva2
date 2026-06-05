package cl.paris.marketplace.ms.usuarios.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

   @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("\n--- INICIANDO FILTRO JWT ---");
        System.out.println("1. Ruta solicitada: " + request.getRequestURI());
        
        final String authHeader = request.getHeader("Authorization");
        System.out.println("2. Header Authorization: " + authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("3. BLOQUEADO: No hay header o no empieza con 'Bearer '");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        System.out.println("4. JWT extraído (debería empezar con ey): " + jwt.substring(0, Math.min(jwt.length(), 10)) + "...");
        
        try {
            final String userEmail = jwtService.extraerUsername(jwt);
            System.out.println("5. Email extraído del token: " + userEmail);

            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("6. Contexto vacío, procediendo a validar token...");
                
                if (jwtService.isTokenValid(jwt)) {
                    System.out.println("7. El token es VÁLIDO (no ha expirado)");
                    
                    List<String> roles = jwtService.extraerRoles(jwt);
                    System.out.println("8. Roles extraídos: " + roles);
                    
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    String usuarioId = jwtService.extraerUsuarioId(jwt);
                    System.out.println("9. Usuario ID extraído: " + usuarioId);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userEmail, usuarioId, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    System.out.println("10. ¡AUTENTICACIÓN EXITOSA! Dejando pasar la petición...");
                } else {
                    System.out.println("7. ERROR: jwtService.isTokenValid() devolvió FALSE");
                }
            }
        } catch (Exception e) {
            System.err.println("ERROR CRÍTICO DENTRO DEL FILTRO");
            e.printStackTrace(); 
        }
        
        filterChain.doFilter(request, response);
    }
}