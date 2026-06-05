package cl.paris.marketplace.ms.administracion.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EstadoUsuarioRequest(
    @NotNull(message = "El ID del usuario a modificar es obligatorio") UUID usuarioId, 
    @NotNull(message = "El estado de baneo es requerido (true/false)") Boolean baneo,
    @NotBlank(message = "La razón del cambio de estado es requerida") String razon
) {}