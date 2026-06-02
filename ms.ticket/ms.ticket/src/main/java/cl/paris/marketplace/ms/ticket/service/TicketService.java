package cl.paris.marketplace.ms.ticket.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.ticket.dto.TicketRequest;
import cl.paris.marketplace.ms.ticket.dto.TicketResponse;
import cl.paris.marketplace.ms.ticket.dto.ActualizarEstadoTicketRequest;
import cl.paris.marketplace.ms.ticket.mapper.TicketMapper;
import cl.paris.marketplace.ms.ticket.model.Ticket;
import cl.paris.marketplace.ms.ticket.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    // Inyección por constructor idéntica al estándar estricto de tu equipo
    public TicketService(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: APERTURA DE DISPUTAS
    // ==========================================
    
    @Transactional
    public TicketResponse abrirTicket(TicketRequest request) {
        // Validación previa antes de guardar (Garantiza los IDs mínimos requeridos para la disputa)
        if (request.pedidoId() == null || request.clienteId() == null || request.vendedorId() == null) {
            throw new RuntimeException("Debe asociar obligatoriamente un pedido, un cliente y un vendedor para abrir una disputa.");
        }

        // Validación complementaria del contenido
        if (request.asunto() == null || request.asunto().trim().isEmpty()) {
            throw new RuntimeException("El asunto de la disputa no puede estar vacío.");
        }

        Ticket ticket = ticketMapper.toEntity(request);
        Ticket ticketGuardado = ticketRepository.save(ticket);
        
        return ticketMapper.toResponse(ticketGuardado);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: RESOLUCIÓN DE DISPUTAS
    // ==========================================
    
    @Transactional
    public TicketResponse cambiarEstado(UUID ticketId, ActualizarEstadoTicketRequest request) {
        // Busca el ticket en Neon o gatilla error inmediato
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("El ticket con el ID provisto no existe en la base de datos."));

        String nuevoEstado = request.estado().toUpperCase();
        // Control estricto de estados válidos del negocio de mensajería
        if (!nuevoEstado.equals("ABIERTO") && !nuevoEstado.equals("EN_PROCESO") && 
            !nuevoEstado.equals("RESUELTO") && !nuevoEstado.equals("CERRADO")) {
            throw new RuntimeException("Estado inválido. Los estados válidos son: ABIERTO, EN_PROCESO, RESUELTO o CERRADO.");
        }

        ticket.setEstado(nuevoEstado);
        Ticket ticketActualizado = ticketRepository.save(ticket);
        
        return ticketMapper.toResponse(ticketActualizado);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> listarTodosLosTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toResponse)
                .toList(); // Uso de .toList() nativo igual que tu compañero
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: EXPOSICIÓN Y CONSULTAS (SISTEMA DE MENSAJERÍA)
    // ==========================================

    @Transactional(readOnly = true)
    public List<TicketResponse> obtenerTicketsPorPedido(UUID pedidoId) {
        List<Ticket> tickets = ticketRepository.findByPedidoIdOrderByFechaCreacionDesc(pedidoId);
        
        // Estilo espejo de error si no hay registros en la base de datos Neon (db_tickets)
        if (tickets.isEmpty()) {
            throw new RuntimeException("No se encontraron tickets de disputa asociados al pedido especificado.");
        }

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> obtenerTicketsPorVendedor(UUID vendedorId) {
        List<Ticket> tickets = ticketRepository.findByVendedorIdOrderByFechaCreacionDesc(vendedorId);
        
        // Estilo espejo de error para asegurar la consistencia del negocio con el vendedor/proveedor
        if (tickets.isEmpty()) {
            throw new RuntimeException("No se encontraron reclamos ni disputas registradas para el vendedor especificado.");
        }

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> obtenerTicketsPorCliente(UUID clienteId) {
        List<Ticket> tickets = ticketRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
        
        // Estilo espejo de error por si la bandeja de entrada/reclamos del cliente está vacía
        if (tickets.isEmpty()) {
            throw new RuntimeException("El cliente especificado no registra tickets de soporte ni disputas creadas.");
        }

        return tickets.stream()
                .map(ticketMapper::toResponse)
                .toList();
    }
}