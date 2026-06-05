package cl.paris.marketplace.ms_proveedores.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms_proveedores.dto.DocumentoRequest;
import cl.paris.marketplace.ms_proveedores.dto.DocumentoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorCompletoResponse;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorRequest;
import cl.paris.marketplace.ms_proveedores.dto.ProveedorResponse;
import cl.paris.marketplace.ms_proveedores.mapper.ProveedorMapper;
import cl.paris.marketplace.ms_proveedores.model.Documento;
import cl.paris.marketplace.ms_proveedores.model.Proveedor;
import cl.paris.marketplace.ms_proveedores.repository.DocumentoRepository;
import cl.paris.marketplace.ms_proveedores.repository.ProveedorRepository;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final DocumentoRepository documentoRepository;
    private final ProveedorMapper proveedorMapper;

    public ProveedorService(ProveedorRepository proveedorRepository,
                            DocumentoRepository documentoRepository,
                            ProveedorMapper proveedorMapper) {
        this.proveedorRepository = proveedorRepository;
        this.documentoRepository = documentoRepository;
        this.proveedorMapper = proveedorMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: PROVEEDORES
    // ==========================================
    
    @Transactional
    public ProveedorResponse crearProveedor(ProveedorRequest request, UUID usuarioId) {
        if (proveedorRepository.existsByRut(request.rut())) {
            throw new RuntimeException("Error: El RUT ingresado ya se encuentra registrado en el sistema.");
        }

        Proveedor proveedor = proveedorMapper.toProveedorEntity(request);
        proveedor.setUsuarioId(usuarioId); 
        
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        
        return proveedorMapper.toProveedorResponse(proveedorGuardado);
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarProveedoresPorUsuario(UUID usuarioId) {
        return proveedorRepository.findByUsuarioId(usuarioId).stream()
                .map(proveedorMapper::toProveedorResponse)
                .toList();
    }
    
    // Puente interno para ms-productos
    @Transactional(readOnly = true)
    public UUID obtenerIdProveedorPorUsuarioId(UUID usuarioId) {
        Proveedor proveedor = proveedorRepository.findFirstByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado para este usuario"));
        return proveedor.getId();
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: DOCUMENTOS
    // ==========================================
    
    @Transactional
    public DocumentoResponse agregarDocumento(DocumentoRequest request, UUID usuarioId) {
        // Buscamos al proveedor usando su token de usuario
        Proveedor proveedor = proveedorRepository.findFirstByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Error: No tienes un perfil de proveedor asociado."));

        if (documentoRepository.existsByProveedorIdAndTipoDocumento(proveedor.getId(), request.tipoDocumento())) {
            throw new RuntimeException("El proveedor ya cuenta con un documento de tipo: " + request.tipoDocumento());
        }

        Documento documento = proveedorMapper.toDocumentoEntity(request, proveedor);
        Documento documentoGuardado = documentoRepository.save(documento);
        
        return proveedorMapper.toDocumentoResponse(documentoGuardado);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: VISTA CONSOLIDADA
    // ==========================================
    
    @Transactional(readOnly = true)
    public ProveedorCompletoResponse obtenerProveedorCompleto(UUID proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado."));

        List<Documento> documentosEntity = documentoRepository.findByProveedorId(proveedorId);
        
        List<DocumentoResponse> documentosResponse = documentosEntity.stream()
                .map(proveedorMapper::toDocumentoResponse)
                .toList();

        return proveedorMapper.toProveedorCompletoResponse(proveedor, documentosResponse);
    }
}