package cl.paris.marketplace.ms.usuarios.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.AuthRequest;
import cl.paris.marketplace.ms.usuarios.dto.AuthResponse;
import cl.paris.marketplace.ms.usuarios.security.JwtService;

@RestController
@RequestMapping("/api/auth") // Esta es la ruta pública que abrimos en SecurityConfig
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    // Constructor manual para inyectar los servicios de seguridad
    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        
        // 1. EL GUARDIA REVISA LAS CREDENCIALES
        // Esto desencadena automáticamente la búsqueda en base de datos y la comparación con BCrypt
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        // 2. SI TODO ES CORRECTO, EXTRAEMOS AL USUARIO
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 3. LA FÁBRICA CREA EL TOKEN
        String token = jwtService.generarToken(userDetails);

        // 4. ENTREGAMOS EL TOKEN EN FORMATO JSON
        return ResponseEntity.ok(new AuthResponse(token));
    }
}