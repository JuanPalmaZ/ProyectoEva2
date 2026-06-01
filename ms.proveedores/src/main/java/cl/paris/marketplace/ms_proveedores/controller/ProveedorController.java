package cl.paris.marketplace.ms_proveedores.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importación obligatoria para los candados
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms_proveedores.dto.DocumentoRequest;
import cl.paris.marketplace.ms_proveedores.dto.DocumentoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorCompletoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorRequest;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorResponse;
import cl.paris.marketplace.ms_proveedores.service.ProveedorService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proveedores") // Ruta base para este microservicio
public class ProveedorController {

    private final ProveedorService proveedorService;

    // Inyección de dependencias por constructor
    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // ==========================================
    // ENDPOINTS: PROVEEDORES
    // ==========================================

    // Un proveedor puede registrar su empresa, o un admin darlo de alta
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.crearProveedor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // =========================================================
    // ¡LA CERRADURA ANTI-IDOR APLICADA AQUÍ!
    // Solo deja pasar al ADMIN, o al PROVEEDOR si el ID de la URL coincide con su token
    // =========================================================
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVEEDOR') and #usuarioId.toString() == authentication.credentials)")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProveedorResponse>> listarProveedoresPorUsuario(@PathVariable UUID usuarioId) {
        List<ProveedorResponse> response = proveedorService.listarProveedoresPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: DOCUMENTOS
    // ==========================================

    // Solo el proveedor puede subir sus documentos legales de validación, o el admin en su defecto
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PostMapping("/documentos")
    public ResponseEntity<DocumentoResponse> agregarDocumento(@Valid @RequestBody DocumentoRequest request) {
        DocumentoResponse response = proveedorService.agregarDocumento(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==========================================
    // ENDPOINTS: VISTA CONSOLIDADA
    // ==========================================

    // Trae el perfil del proveedor con todos sus documentos tributarios/legales
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @GetMapping("/{id}/completo")
    public ResponseEntity<ProveedorCompletoResponse> obtenerProveedorCompleto(@PathVariable UUID id) {
        ProveedorCompletoResponse response = proveedorService.obtenerProveedorCompleto(id);
        return ResponseEntity.ok(response);
    }
}