package cl.paris.marketplace.ms.usuarios.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.PerfilRequest;
import cl.paris.marketplace.ms.usuarios.dto.PerfilResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioCompletoResponse;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioRequest;
import cl.paris.marketplace.ms.usuarios.dto.UsuarioResponse;
import cl.paris.marketplace.ms.usuarios.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios") 
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ==========================================
    // ENDPOINTS: USUARIOS
    // ==========================================
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrarUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.registrarUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(@RequestParam(required = false) String email) {
        return ResponseEntity.ok(usuarioService.buscarUsuarios(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable UUID id) {
        UsuarioResponse response = usuarioService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINT ADMINISTRATIVO (Llamado vía Feign)
    // ==========================================
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/estado-baneo")
    public ResponseEntity<Void> actualizarEstadoBaneo(
            @PathVariable UUID id, 
            @RequestParam Boolean baneo) {
        
        usuarioService.actualizarEstadoBaneo(id, baneo);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // ENDPOINTS: PERFILES
    // ==========================================
    @PostMapping("/perfiles")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerfilResponse> crearPerfil(
            @Valid @RequestBody PerfilRequest request,
            Authentication authentication) {
        
        String emailDelToken = authentication.getName();
        PerfilResponse response = usuarioService.crearPerfil(request, emailDelToken);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{usuarioId}/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(@PathVariable UUID usuarioId) {
        PerfilResponse response = usuarioService.obtenerPerfilPorUsuarioId(usuarioId);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: VISTA CONSOLIDADA (Usa Feign)
    // ==========================================
    @GetMapping("/{id}/completo")
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.obtenerUsuarioCompleto(id));
    }
}