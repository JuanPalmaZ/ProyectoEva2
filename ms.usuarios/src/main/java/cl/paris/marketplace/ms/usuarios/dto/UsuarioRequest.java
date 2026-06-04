package cl.paris.marketplace.ms.usuarios.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
    
    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe proporcionar un formato de correo electrónico válido")
    String email,

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 50, message = "La contraseña debe tener entre 8 y 50 caracteres")
    String password,

    @NotNull(message = "Debe asignar un rol al usuario")
    Long rolId
) {}