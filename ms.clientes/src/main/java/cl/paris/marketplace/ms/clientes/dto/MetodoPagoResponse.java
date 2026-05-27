package cl.paris.marketplace.ms.clientes.dto;
import java.util.UUID;
public record MetodoPagoResponse(
    UUID id,
    String tipo
) {}