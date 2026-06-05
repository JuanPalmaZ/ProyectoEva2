package cl.paris.marketplace.ms.legacy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.legacy.dto.LegacyRecordResponse;
import cl.paris.marketplace.ms.legacy.dto.LegacySyncRequest;
import cl.paris.marketplace.ms.legacy.service.LegacyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/legacy")
@PreAuthorize("isAuthenticated()")
public class LegacyController {

    private final LegacyService legacyService;

    public LegacyController(LegacyService legacyService) {
        this.legacyService = legacyService;
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<LegacyRecordResponse> sincronizarDatoAntiguo(@Valid @RequestBody LegacySyncRequest request) {
        LegacyRecordResponse response = legacyService.sincronizarDatoAntiguo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Retorna 201 Created
    }

    @GetMapping("/historial")
    public ResponseEntity<List<LegacyRecordResponse>> listarHistorialSincronizaciones() {
        return ResponseEntity.ok(legacyService.listarHistorialSincronizaciones());
    }

    @GetMapping("/entidad/{tipoEntidad}")
    public ResponseEntity<List<LegacyRecordResponse>> listarPorTipoEntidad(@PathVariable String tipoEntidad) {
        return ResponseEntity.ok(legacyService.listarPorTipoEntidad(tipoEntidad));
    }
}