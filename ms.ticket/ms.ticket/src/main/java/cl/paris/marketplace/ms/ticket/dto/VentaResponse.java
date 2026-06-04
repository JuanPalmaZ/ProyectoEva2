package cl.paris.marketplace.ms.ticket.dto;

import java.util.List;
import java.util.UUID;

// DTO enriquecido: Ahora leemos los detalles de la boleta para sacar el ID del vendedor
public record VentaResponse(
        UUID idVenta,
        List<DetalleVentaDTO> detalles
) {
    public record DetalleVentaDTO(
            UUID proveedorId
    ) {}
}