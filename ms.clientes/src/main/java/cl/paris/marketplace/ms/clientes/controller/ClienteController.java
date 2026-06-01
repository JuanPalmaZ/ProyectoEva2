package cl.paris.marketplace.ms.clientes.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ==========================================
    // ENDPOINTS: MÉTODOS DE PAGO
    // ==========================================
    
    // ¡NUEVO! Solo deja pasar si el token dice CLIENTE o ADMINISTRADOR
    @PreAuthorize("hasRole('CLIENTE')") 
    @PostMapping("/metodos-pago")
    public ResponseEntity<MetodoPagoResponse> agregarMetodoPago(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoResponse response = clienteService.agregarMetodoPago(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ¡NUEVO! Aquí dejamos que lo vea el CLIENTE dueño, o un ADMINISTRADOR
    @PreAuthorize("hasRole('CLIENTE') ") 
    @GetMapping("/usuario/{usuarioId}/metodos-pago")
    public ResponseEntity<List<MetodoPagoResponse>> listarMetodosPagoUsuario(@PathVariable UUID usuarioId) {
        List<MetodoPagoResponse> response = clienteService.listarMetodosPagoUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
}