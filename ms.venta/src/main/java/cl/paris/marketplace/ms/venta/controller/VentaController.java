package cl.paris.marketplace.ms.venta.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<VentaResponse> registrarVenta(
            @Valid @RequestBody VentaRequest request) {

        VentaResponse response =
                ventaService.registrarVenta(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
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

    @PreAuthorize("hasAnyRole('ADMIN','CLIENTE')")
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