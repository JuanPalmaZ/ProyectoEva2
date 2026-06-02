package cl.paris.marketplace.ms.ticket.dto;

import java.util.UUID;

// DTO minimalista: Si llega a leer el ID, significa que la venta existe en ms.ventas
public record VentaResponse(
    UUID id
) {}