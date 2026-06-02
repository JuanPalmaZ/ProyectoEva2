package cl.paris.marketplace.ms.ticket.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<TicketResponse> abrirTicket(@Valid @RequestBody TicketRequest request) {
        TicketResponse response = ticketService.abrirTicket(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Retorna 201 Created
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
        // CORRECCIÓN: Llama al método exacto del Service para evitar el error 'undefined'
        return ResponseEntity.ok(ticketService.listarTodosLosTickets());
    }

    // Bandeja personal para que el Cliente revise sus disputas activas
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarPorCliente(@PathVariable UUID clienteId) {
        // CORRECCIÓN: Enlaza directo con obtenerTicketsPorCliente del Service
        return ResponseEntity.ok(ticketService.obtenerTicketsPorCliente(clienteId));
    }

    // Bandeja de entrada para que el Vendedor gestione los reclamos que le abrieron
    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMIN')")
    public ResponseEntity<List<TicketResponse>> listarPorVendedor(@PathVariable UUID vendedorId) {
        // CORRECCIÓN: Enlaza directo con obtenerTicketsPorVendedor del Service
        return ResponseEntity.ok(ticketService.obtenerTicketsPorVendedor(vendedorId));
    }
}