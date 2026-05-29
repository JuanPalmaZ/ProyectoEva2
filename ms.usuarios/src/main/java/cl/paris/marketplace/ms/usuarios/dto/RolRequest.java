package cl.paris.marketplace.ms.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
public record RolRequest
(@NotBlank(message = "El nombre del rol no puede estar vacío")
    String nombreRol
) {}