package cl.paris.marketplace.ms_proveedores.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.crearProveedor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Usamos la ruta /usuario/{usuarioId} para listar los proveedores que creó alguien en específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ProveedorResponse>> listarProveedoresPorUsuario(@PathVariable UUID usuarioId) {
        List<ProveedorResponse> response = proveedorService.listarProveedoresPorUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: DOCUMENTOS
    // ==========================================

    @PostMapping("/documentos")
    public ResponseEntity<DocumentoResponse> agregarDocumento(@Valid @RequestBody DocumentoRequest request) {
        DocumentoResponse response = proveedorService.agregarDocumento(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==========================================
    // ENDPOINTS: VISTA CONSOLIDADA
    // ==========================================

    @GetMapping("/{id}/completo")
    public ResponseEntity<ProveedorCompletoResponse> obtenerProveedorCompleto(@PathVariable UUID id) {
        ProveedorCompletoResponse response = proveedorService.obtenerProveedorCompleto(id);
        return ResponseEntity.ok(response);
    }
}