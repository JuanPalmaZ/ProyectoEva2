package cl.paris.marketplace.ms_proveedores.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- Importación agregada
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
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // ==========================================
    // ENDPOINTS: PROVEEDORES
    // ==========================================

    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(
            @Valid @RequestBody ProveedorRequest request,
            Authentication authentication // <-- Interceptamos token
    ) {
        UUID usuarioId = UUID.fromString(authentication.getCredentials().toString());
        ProveedorResponse response = proveedorService.crearProveedor(request, usuarioId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVEEDOR') and #usuarioId.toString() == authentication.credentials)")
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProveedorResponse>> listarProveedoresPorUsuario(@PathVariable UUID usuarioId) {
        List<ProveedorResponse> response = proveedorService.listarProveedoresPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
    
    // Ruta para que ms-productos la consulte vía Feign
    @GetMapping("/interno/usuario/{usuarioId}/id")
    public ResponseEntity<UUID> obtenerIdProveedorInterno(@PathVariable UUID usuarioId) {
        UUID proveedorId = proveedorService.obtenerIdProveedorPorUsuarioId(usuarioId);
        return ResponseEntity.ok(proveedorId);
    }

    // ==========================================
    // ENDPOINTS: DOCUMENTOS
    // ==========================================

    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PostMapping("/documentos")
    public ResponseEntity<DocumentoResponse> agregarDocumento(
            @Valid @RequestBody DocumentoRequest request,
            Authentication authentication // <-- Interceptamos token
    ) {
        UUID usuarioId = UUID.fromString(authentication.getCredentials().toString());
        DocumentoResponse response = proveedorService.agregarDocumento(request, usuarioId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==========================================
    // ENDPOINTS: VISTA CONSOLIDADA
    // ==========================================

    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @GetMapping("/{id}/completo")
    public ResponseEntity<ProveedorCompletoResponse> obtenerProveedorCompleto(@PathVariable UUID id) {
        ProveedorCompletoResponse response = proveedorService.obtenerProveedorCompleto(id);
        return ResponseEntity.ok(response);
    }
}