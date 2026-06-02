package cl.paris.marketplace.ms_proveedores.dto;

import jakarta.validation.constraints.NotBlank;

public record ProveedorRequest(
        @NotBlank(message = "La razón social es obligatoria") 
        String razonSocial,
        
        @NotBlank(message = "El RUT es obligatorio") 
        String rut
) {}