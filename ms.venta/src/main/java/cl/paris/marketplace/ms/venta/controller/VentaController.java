package cl.paris.marketplace.ms.venta.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- Importación obligatoria
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.venta.dto.VentaRequest;
import cl.paris.marketplace.ms.venta.dto.VentaResponse;
import cl.paris.marketplace.ms.venta.service.VentaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    // ==========================================
    // REGISTRAR VENTA
    // ==========================================

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<?> registrarVenta(
            @Valid @RequestBody VentaRequest request,
            Authentication authentication 
    ) {
        try {
            // 1. Escudo Anti-Null: Revisamos si Spring borró la credencial
            if (authentication.getCredentials() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Crítico: El usuarioId llegó como 'null'. Revisa si el Token realmente trae el claim 'usuarioId'.");
            }

            String credencialesStr = authentication.getCredentials().toString();
            UUID clienteId;
            
            // 2. Escudo Anti-Formato: Revisamos si realmente es un UUID
            try {
                clienteId = UUID.fromString(credencialesStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de Formato: El Token fue leído, pero el ID adentro no es un UUID válido. Valor recibido: '" + credencialesStr + "'");
            }
            
            // 3. Ejecución normal si todo está perfecto
            VentaResponse response = ventaService.registrarVenta(request, clienteId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // 4. Si explota por falta de stock o problemas de Feign
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error procesando la venta en el Service: " + e.getMessage());
        }
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================

    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
    @GetMapping("/{ventaId}")
    public ResponseEntity<VentaResponse> buscarPorId(
            @PathVariable UUID ventaId) {

        return ResponseEntity.ok(
                ventaService.buscarPorId(ventaId));
    }

    // ==========================================
    // HISTORIAL CLIENTE
    // ==========================================

    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #clienteId.toString() == authentication.credentials)")
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<VentaResponse>> buscarPorCliente(
            @PathVariable UUID clienteId) {

        return ResponseEntity.ok(
                ventaService.buscarPorCliente(clienteId));
    }

    // ==========================================
    // REPORTE POR FECHAS
    // ==========================================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rango")
    public ResponseEntity<List<VentaResponse>> buscarPorRangoFechas(

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        return ResponseEntity.ok(
                ventaService.buscarPorRangoFechas(
                        fechaInicio,
                        fechaFin));
    }
}