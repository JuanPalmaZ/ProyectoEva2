package cl.paris.marketplace.ms.administracion.controller;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;
 
import cl.paris.marketplace.ms.administracion.dto.AdminAccionRequest;
import cl.paris.marketplace.ms.administracion.dto.AdminAccionResponse;
import cl.paris.marketplace.ms.administracion.dto.ModerarProductoRequest;
import cl.paris.marketplace.ms.administracion.dto.EstadoUsuarioRequest;
import cl.paris.marketplace.ms.administracion.service.AdminService;
import jakarta.validation.Valid;
 
import java.util.List;
import java.util.UUID;
 
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") 
public class AdminController {
 
    private final AdminService adminService;
 
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
 
    @PostMapping("/auditoria")
    public ResponseEntity<AdminAccionResponse> registrarAccionManual(
            @Valid @RequestBody AdminAccionRequest request,
            Authentication authentication) {
        
        UUID adminId = UUID.fromString(authentication.getCredentials().toString());
        AdminAccionResponse response = adminService.registrarAccionManual(request, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
 
    @GetMapping("/auditoria")
    public ResponseEntity<List<AdminAccionResponse>> listarHistorial() {
        return ResponseEntity.ok(adminService.listarHistorial());
    }
 
    @GetMapping("/auditoria/usuario/{usuarioId}")
    public ResponseEntity<List<AdminAccionResponse>> listarPorUsuarioAdmin(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(adminService.listarPorUsuarioAdmin(usuarioId));
    }
 
    @PostMapping("/productos/moderar")
    public ResponseEntity<AdminAccionResponse> moderarProducto(
            @Valid @RequestBody ModerarProductoRequest request,
            Authentication authentication) {
        
        UUID adminId = UUID.fromString(authentication.getCredentials().toString());
        AdminAccionResponse response = adminService.moderarProducto(request, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
 
    @PostMapping("/usuarios/estado")
    public ResponseEntity<AdminAccionResponse> cambiarEstadoUsuario(
            @Valid @RequestBody EstadoUsuarioRequest request,
            Authentication authentication) {
        
        UUID adminId = UUID.fromString(authentication.getCredentials().toString());
        AdminAccionResponse response = adminService.cambiarEstadoUsuario(request, adminId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}