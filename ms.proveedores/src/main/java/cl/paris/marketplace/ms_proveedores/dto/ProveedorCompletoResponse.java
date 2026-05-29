package cl.paris.marketplace.ms_proveedores.dto;

import java.util.List;
import java.util.UUID;

public record ProveedorCompletoResponse(
        UUID id,
        UUID usuarioId,
        String razonSocial,
        String rut,
        String estadoApro,
        
        // ¡Aquí anidamos la lista de documentos!
        List<DocumentoResponse> documentos
) {}