package cl.paris.marketplace.ms_proveedores.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentoRequest(
        @NotBlank(message = "El tipo de documento es obligatorio") 
        String tipoDocumento
) {}