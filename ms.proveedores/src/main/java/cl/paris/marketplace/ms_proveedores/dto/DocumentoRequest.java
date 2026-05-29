package cl.paris.marketplace.ms_proveedores.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DocumentoRequest(
        @NotNull(message = "El ID del proveedor es obligatorio") 
        UUID proveedorId,
        
        @NotBlank(message = "El tipo de documento es obligatorio") 
        String tipoDocumento
) {}