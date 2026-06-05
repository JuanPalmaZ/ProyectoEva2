package cl.paris.marketplace.ms.administracion.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModerarProductoRequest(
    @NotNull(message = "El ID del producto es obligatorio") UUID productoId,
    @NotBlank(message = "El estado de moderación es obligatorio (APROBADO/RECHAZADO)") String estado,
    @NotBlank(message = "El motivo de la moderación es requerido") String motivo
) {}