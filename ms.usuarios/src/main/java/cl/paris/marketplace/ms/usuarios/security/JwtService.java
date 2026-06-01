package cl.paris.marketplace.ms.usuarios.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

// Importa tu entidad y tu repositorio
import cl.paris.marketplace.ms.usuarios.model.Usuario;
import cl.paris.marketplace.ms.usuarios.repository.UsuarioRepository; 

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // Inyectamos el repositorio para ir a buscar el UUID de forma segura
    private final UsuarioRepository usuarioRepository;

    public JwtService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // 1. GENERAR TOKEN
    public String generarToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        // Inyectamos los roles al token
        extraClaims.put("roles", userDetails.getAuthorities());
        
        // =========================================================
        // LA SOLUCIÓN AL CLASS CAST EXCEPTION
        // Buscamos al usuario en la BD usando el email que nos da Spring Security.
        // Si no lo encuentra, lanza error, pero si lo encuentra, sacamos el UUID seguro.
        // =========================================================
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD al generar token"));
                
        extraClaims.put("usuarioId", usuario.getId().toString());
        
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername()) 
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey()) 
                .compact();
    }

    // 2. EXTRAER USUARIO
    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

    // 3. VALIDAR TOKEN
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extraerExpiration(token).before(new Date());
    }

    private Date extraerExpiration(String token) {
        return extraerClaim(token, Claims::getExpiration);
    }

    private <T> T extraerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extraerAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extraerAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}