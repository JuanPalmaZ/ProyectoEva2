package cl.paris.marketplace.ms.administracion.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminAccionRequest(
    @NotNull(message = "El ID del usuario administrador es obligatorio") UUID usuarioId,
    @NotBlank(message = "La acción no puede estar vacía") String accion,
    @NotBlank(message = "El detalle de la auditoría es requerido") String detalle
) {}