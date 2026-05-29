package cl.paris.marketplace.ms.clientes.dto;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MetodoPagoRequest(
    @NotNull UUID usuarioId,
    @NotBlank String tokenTarjeta,
    @NotBlank String tipo
) {}