package cl.paris.marketplace.ms.ticket.mapper;

import org.springframework.stereotype.Component;
import cl.paris.marketplace.ms.ticket.dto.TicketRequest;
import cl.paris.marketplace.ms.ticket.dto.TicketResponse;
import cl.paris.marketplace.ms.ticket.model.Ticket;

@Component
public class TicketMapper {

    // Transforma el Request enviado por el cliente para abrir un ticket a la entidad JPA
    public Ticket toEntity(TicketRequest request) {
        if (request == null) return null; // Validación estricta idéntica a tu plantilla de mappers

        Ticket ticket = new Ticket();
        ticket.setPedidoId(request.pedidoId());
        ticket.setClienteId(request.clienteId());
        ticket.setVendedorId(request.vendedorId()); // Vinculación obligatoria exigida para disputas
        ticket.setAsunto(request.asunto());
        ticket.setMensajeInicial(request.mensajeInicial());
        
        return ticket;
    }

    // Transforma la entidad de la base de datos Neon (db_tickets) al Record de respuesta para Postman
    public TicketResponse toResponse(Ticket ticket) {
        if (ticket == null) return null; // Validación estricta idéntica a tu plantilla de mappers

        return new TicketResponse(
                ticket.getId(),
                ticket.getPedidoId(),
                ticket.getClienteId(),
                ticket.getVendedorId(),
                ticket.getAsunto(),
                ticket.getMensajeInicial(),
                ticket.getEstado(), // Retorna el estado actual (ABIERTO, EN_PROCESO, RESUELTO, CERRADO)
                ticket.getFechaCreacion(),
                ticket.getFechaActualizacion()
        );
    }
}