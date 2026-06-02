package cl.paris.marketplace.ms.ticket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record TicketRequest(
    @NotNull(message = "El ID del pedido es obligatorio") UUID pedidoId,
    @NotNull(message = "El ID del vendedor es obligatorio") UUID vendedorId,
    
    @NotBlank(message = "El asunto de la disputa no puede estar vacío")
    @Size(max = 150, message = "El asunto no puede superar los 150 caracteres")
    String asunto,
    
    @NotBlank(message = "El mensaje explicativo inicial es obligatorio")
    @Size(max = 2000, message = "El mensaje no puede superar los 2000 caracteres")
    String mensajeInicial
) {}