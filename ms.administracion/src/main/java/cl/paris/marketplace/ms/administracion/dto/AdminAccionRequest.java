package cl.paris.marketplace.ms.administracion.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminAccionRequest(
    @NotBlank(message = "La acción no puede estar vacía") String accion,
    @NotBlank(message = "El detalle de la auditoría es requerido") String detalle
) {}