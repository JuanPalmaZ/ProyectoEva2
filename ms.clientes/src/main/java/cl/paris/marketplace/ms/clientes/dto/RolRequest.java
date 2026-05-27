package cl.paris.marketplace.ms.clientes.dto;

import jakarta.validation.constraints.NotBlank;
public record RolRequest
(@NotBlank(message = "El nombre del rol no puede estar vacío")
    String nombreRol
) {}