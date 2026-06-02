package cl.paris.marketplace.ms.venta.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificacionRequest(
    @NotBlank String destinatario,
    @NotBlank String asunto,
    @NotBlank String mensaje
) {}