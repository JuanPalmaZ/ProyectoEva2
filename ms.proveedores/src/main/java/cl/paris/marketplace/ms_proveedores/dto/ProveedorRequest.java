package cl.paris.marketplace.ms_proveedores.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProveedorRequest(
        @NotNull(message = "El ID del usuario es obligatorio") 
        UUID usuarioId,
        
        @NotBlank(message = "La razón social es obligatoria") 
        String razonSocial,
        
        @NotBlank(message = "El RUT es obligatorio") 
        String rut
) {}