package cl.paris.marketplace.ms.usuarios.dto;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MetodoPagoRequest(
    @NotNull(message = "El ID del usuario es obligatorio")
    UUID usuarioId,

    @NotBlank(message = "El token de la tarjeta es obligatorio")
    @Size(max = 255, message = "El token excede la longitud permitida")
    String tokenTarjeta,

    @NotBlank(message = "Debe especificar el tipo de método de pago (ej. CREDITO, DEBITO)")
    @Size(max = 50)
    String tipo
) {}
