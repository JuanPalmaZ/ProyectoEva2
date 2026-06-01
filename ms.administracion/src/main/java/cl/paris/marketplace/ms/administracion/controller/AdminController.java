package cl.paris.marketplace.ms.administracion.controller;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importación para el candado de seguridad
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
@PreAuthorize("hasRole('ADMIN')") // REGLA DE ORO: Solo los usuarios con rol ADMIN pueden tocar este controlador completo
public class AdminController {
 
    private final AdminService adminService;
 
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
 
    // ==========================================
    // ENDPOINTS: AUDITORÍA MANUAL
    // ==========================================
    
    @PostMapping("/auditoria")
    public ResponseEntity<AdminAccionResponse> registrarAccionManual(@Valid @RequestBody AdminAccionRequest request) {
        AdminAccionResponse response = adminService.registrarAccionManual(request);
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
 
    // ==========================================
    // ENDPOINTS: MODERACIÓN DE NEGOCIO (Usando @RequestBody)
    // ==========================================
 
    @PostMapping("/productos/moderar")
    public ResponseEntity<AdminAccionResponse> moderarProducto(@Valid @RequestBody ModerarProductoRequest request) {
        AdminAccionResponse response = adminService.moderarProducto(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
 
    @PostMapping("/usuarios/estado")
    public ResponseEntity<AdminAccionResponse> cambiarEstadoUsuario(@Valid @RequestBody EstadoUsuarioRequest request) {
        AdminAccionResponse response = adminService.cambiarEstadoUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}