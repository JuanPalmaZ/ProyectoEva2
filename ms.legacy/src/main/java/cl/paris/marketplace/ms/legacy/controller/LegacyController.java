package cl.paris.marketplace.ms.legacy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cl.paris.marketplace.ms.legacy.dto.LegacySyncRequest;
import cl.paris.marketplace.ms.legacy.dto.LegacyRecordResponse;
import cl.paris.marketplace.ms.legacy.service.LegacyService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/legacy")
@PreAuthorize("hasRole('ADMIN')") //Solo personal ADMIN puede forzar la conexión legacy
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