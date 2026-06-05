package cl.paris.marketplace.ms_proveedores.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import cl.paris.marketplace.ms_proveedores.dto.DocumentoRequest;
import cl.paris.marketplace.ms_proveedores.dto.DocumentoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorCompletoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorRequest;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorResponse;
import cl.paris.marketplace.ms_proveedores.model.Documento;
import cl.paris.marketplace.ms_proveedores.model.Proveedor;

@Component
public class ProveedorMapper {

    // ==========================================
    // MAPPERS PARA PROVEEDOR
    // ==========================================

    public Proveedor toProveedorEntity(ProveedorRequest request) {
        Proveedor proveedor = new Proveedor();
        proveedor.setRazonSocial(request.razonSocial());
        proveedor.setRut(request.rut());
        // Regla de negocio: Todo proveedor nuevo entra en estado PENDIENTE
        proveedor.setEstadoApro("PENDIENTE"); 
        return proveedor;
    }

    public ProveedorResponse toProveedorResponse(Proveedor proveedor) {
        return new ProveedorResponse(
                proveedor.getId(),
                proveedor.getUsuarioId(),
                proveedor.getRazonSocial(),
                proveedor.getRut(),
                proveedor.getEstadoApro()
        );
    }

    // ==========================================
    // MAPPERS PARA DOCUMENTO
    // ==========================================

    public Documento toDocumentoEntity(DocumentoRequest request, Proveedor proveedor) {
        Documento documento = new Documento();
        documento.setProveedor(proveedor); // Amarra el documento a su dueño
        documento.setTipoDocumento(request.tipoDocumento());
        // Regla de negocio: Se registra la hora exacta del servidor
        documento.setFechaSubida(LocalDateTime.now()); 
        return documento;
    }

    public DocumentoResponse toDocumentoResponse(Documento documento) {
        return new DocumentoResponse(
                documento.getId(),
                documento.getTipoDocumento(),
                documento.getFechaSubida()
        );
    }

    // ==========================================
    // MAPPER PARA VISTA COMPLETA
    // ==========================================

    public ProveedorCompletoResponse toProveedorCompletoResponse(Proveedor proveedor, List<DocumentoResponse> documentosResponse) {
        return new ProveedorCompletoResponse(
                proveedor.getId(),
                proveedor.getUsuarioId(),
                proveedor.getRazonSocial(),
                proveedor.getRut(),
                proveedor.getEstadoApro(),
                documentosResponse 
        );
    }
}