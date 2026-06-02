package cl.paris.marketplace.ms.ticket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import cl.paris.marketplace.ms.ticket.dto.TicketRequest;
import cl.paris.marketplace.ms.ticket.dto.TicketResponse;
import cl.paris.marketplace.ms.ticket.dto.ActualizarEstadoTicketRequest;
import cl.paris.marketplace.ms.ticket.service.TicketService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    // Inyección por constructor bajo el estándar estricto de tu equipo
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // ==========================================
    // ENDPOINT: APERTURA DE DISPUTA POR EL CLIENTE
    // ==========================================
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") // Solo los clientes de Paris.cl inician reclamos
    public ResponseEntity<?> abrirTicket(
            @Valid @RequestBody TicketRequest request,
            Authentication authentication
    ) {
        try {
            // Escudo Anti-Null: Revisamos si Spring borró la credencial
            if (authentication.getCredentials() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Crítico: El usuarioId no se encontró en el token.");
            }

            String credencialesStr = authentication.getCredentials().toString();
            UUID clienteId;
            
            // Escudo Anti-Formato: Revisamos si realmente es un UUID
            try {
                clienteId = UUID.fromString(credencialesStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error de Formato: El Token fue leído, pero el ID adentro no es un UUID válido.");
            }
            
            TicketResponse response = ticketService.abrirTicket(request, clienteId);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // Retorna 201 Created

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error procesando el ticket: " + e.getMessage());
        }
    }

    // ==========================================
    // ENDPOINT: RESOLUCIÓN DE LA DISPUTA
    // ==========================================
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROVEEDOR')") // El administrador o la tienda socia resuelven el caso
    public ResponseEntity<TicketResponse> cambiarEstado(
            @PathVariable UUID id, 
            @Valid @RequestBody ActualizarEstadoTicketRequest request) {
        TicketResponse response = ticketService.cambiarEstado(id, request);
        return ResponseEntity.ok(response); // Retorna 200 OK
    }

    // ==========================================
    // ENDPOINTS: CONSULTA Y BANDEJA DE MENSAJERÍA
    // ==========================================

    // Historial global para paneles de administración interna
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarTodos() {
        return ResponseEntity.ok(ticketService.listarTodosLosTickets());
    }

    // Bandeja personal para que el Cliente revise sus disputas activas (Candado Anti-IDOR)
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #clienteId.toString() == authentication.credentials)")
    public ResponseEntity<List<TicketResponse>> listarPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(ticketService.obtenerTicketsPorCliente(clienteId));
    }

    // Bandeja de entrada para que el Vendedor gestione los reclamos que le abrieron (Candado Anti-IDOR)
    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVEEDOR') and #vendedorId.toString() == authentication.credentials)")
    public ResponseEntity<List<TicketResponse>> listarPorVendedor(@PathVariable UUID vendedorId) {
        return ResponseEntity.ok(ticketService.obtenerTicketsPorVendedor(vendedorId));
    }
}