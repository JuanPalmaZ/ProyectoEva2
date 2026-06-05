package cl.paris.marketplace.ms.legacy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.legacy.dto.LegacyRecordResponse;
import cl.paris.marketplace.ms.legacy.dto.LegacySyncRequest;
import cl.paris.marketplace.ms.legacy.service.LegacyService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/legacy")
// QUITAMOS el @PreAuthorize de aquí arriba para poder controlar ruta por ruta
public class LegacyController {

    private final LegacyService legacyService;

    public LegacyController(LegacyService legacyService) {
        this.legacyService = legacyService;
    }

    // Esta ruta queda abierta al público en SecurityConfig, pero protegida por un Header secreto
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarDatoAntiguo(
            @RequestHeader(value = "X-Internal-Secret", required = false) String secret,
            @Valid @RequestBody LegacySyncRequest request) {
        
        // Verificamos que la petición venga de ms-usuarios y no de un atacante externo
        if (!"paris-legacy-secret-2026".equals(secret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Acceso denegado: Llave de sistema inválida o ausente.");
        }

        LegacyRecordResponse response = legacyService.sincronizarDatoAntiguo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); 
    }

    // Protegemos individualmente las rutas de lectura para que solo entren administradores logeados
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/historial")
    public ResponseEntity<List<LegacyRecordResponse>> listarHistorialSincronizaciones() {
        return ResponseEntity.ok(legacyService.listarHistorialSincronizaciones());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/entidad/{tipoEntidad}")
    public ResponseEntity<List<LegacyRecordResponse>> listarPorTipoEntidad(@PathVariable String tipoEntidad) {
        return ResponseEntity.ok(legacyService.listarPorTipoEntidad(tipoEntidad));
    }
}