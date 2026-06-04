package cl.paris.marketplace.ms.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilRequest(
    @NotBlank(message = "El primer nombre es obligatorio")
    @Size(max = 30, message = "El primer nombre no puede exceder los 30 caracteres")
    String primerNombre,

    @Size(max = 30, message = "El segundo nombre no puede exceder los 30 caracteres")
    String segundoNombre,

    @NotBlank(message = "El primer apellido es obligatorio")
    @Size(max = 30, message = "El primer apellido no puede exceder los 30 caracteres")
    String primerApellido,

    @Size(max = 30, message = "El segundo apellido no puede exceder los 30 caracteres")
    String segundoApellido,

    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    String telefono
) {}