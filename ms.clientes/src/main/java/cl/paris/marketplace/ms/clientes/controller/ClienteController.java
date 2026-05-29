package cl.paris.marketplace.ms.clientes.controller;

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

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección de dependencias por constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ==========================================
    // ENDPOINTS: MÉTODOS DE PAGO
    // ==========================================
    
    // Ruta: POST http://localhost:8080/api/clientes/metodos-pago
    @PostMapping("/metodos-pago")
    public ResponseEntity<MetodoPagoResponse> agregarMetodoPago(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoResponse response = clienteService.agregarMetodoPago(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Ruta: GET http://localhost:8080/api/clientes/usuario/{usuarioId}/metodos-pago
    @GetMapping("/usuario/{usuarioId}/metodos-pago")
    public ResponseEntity<List<MetodoPagoResponse>> listarMetodosPagoUsuario(@PathVariable UUID usuarioId) {
        List<MetodoPagoResponse> response = clienteService.listarMetodosPagoUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
    
}