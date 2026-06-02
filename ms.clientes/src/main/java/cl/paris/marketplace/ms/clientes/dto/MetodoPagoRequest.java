package cl.paris.marketplace.ms.clientes.dto;

import jakarta.validation.constraints.NotBlank;

public record MetodoPagoRequest(
    @NotBlank String tokenTarjeta,
    @NotBlank String tipo
) {}