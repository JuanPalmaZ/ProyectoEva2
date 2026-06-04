package cl.paris.marketplace.ms.usuarios.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import cl.paris.marketplace.ms.usuarios.model.Usuario;
import cl.paris.marketplace.ms.usuarios.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys; 

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final UsuarioRepository usuarioRepository;

    public JwtService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public String generarToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        
        extraClaims.put("roles", userDetails.getAuthorities());
        
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

    public String extraerUsername(String token) {
        return extraerClaim(token, Claims::getSubject);
    }

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

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extraerUsuarioId(String token) {
        return extraerClaim(token, claims -> claims.get("usuarioId", String.class));
    }

    public List<String> extraerRoles(String token) {
        return extraerClaim(token, claims -> {
            List<Map<String, String>> authorities = claims.get("roles", List.class);
            if (authorities != null) {
                return authorities.stream()
                        .map(auth -> auth.get("authority"))
                        .toList();
            }
            return java.util.Collections.emptyList();
        });
    }
}