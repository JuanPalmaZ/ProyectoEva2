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

@Service // ¡Esta anotación es vital para que Spring Boot lo reconozca!
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final DocumentoRepository documentoRepository;
    private final ProveedorMapper proveedorMapper;

    // Inyección de dependencias por constructor
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
    public ProveedorResponse crearProveedor(ProveedorRequest request) {
        // 1. Regla de negocio: Validar que el RUT no esté duplicado
        if (proveedorRepository.existsByRut(request.rut())) {
            throw new RuntimeException("Error: El RUT ingresado ya se encuentra registrado en el sistema.");
        }

        // 2. Transformar DTO a Entidad
        Proveedor proveedor = proveedorMapper.toProveedorEntity(request);
        
        // 3. Guardar en Base de Datos
        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);
        
        // 4. Devolver respuesta segura
        return proveedorMapper.toProveedorResponse(proveedorGuardado);
    }

    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarProveedoresPorUsuario(UUID usuarioId) {
        // Busca todos los proveedores que un usuario en específico haya registrado
        return proveedorRepository.findByUsuarioId(usuarioId).stream()
                .map(proveedorMapper::toProveedorResponse)
                .toList();
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: DOCUMENTOS
    // ==========================================
    
    @Transactional
    public DocumentoResponse agregarDocumento(DocumentoRequest request) {
        // 1. Validar que el proveedor al que le intentan subir el documento realmente exista
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new RuntimeException("Error: Proveedor no encontrado."));

        // 2. Regla de negocio: Evitar subir el mismo tipo de documento dos veces
        if (documentoRepository.existsByProveedorIdAndTipoDocumento(proveedor.getId(), request.tipoDocumento())) {
            throw new RuntimeException("El proveedor ya cuenta con un documento de tipo: " + request.tipoDocumento());
        }

        // 3. Transformar, asociar al proveedor y guardar
        Documento documento = proveedorMapper.toDocumentoEntity(request, proveedor);
        Documento documentoGuardado = documentoRepository.save(documento);
        
        return proveedorMapper.toDocumentoResponse(documentoGuardado);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: VISTA CONSOLIDADA
    // ==========================================
    
    @Transactional(readOnly = true)
    public ProveedorCompletoResponse obtenerProveedorCompleto(UUID proveedorId) {
        // 1. Obtener la información básica del proveedor
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado."));

        // 2. Obtener todos los documentos asociados a este proveedor específico
        List<Documento> documentosEntity = documentoRepository.findByProveedorId(proveedorId);
        
        // 3. Convertir la lista de Entidades a una lista de DTOs (Responses)
        List<DocumentoResponse> documentosResponse = documentosEntity.stream()
                .map(proveedorMapper::toDocumentoResponse)
                .toList();

        // 4. Ensamblar y devolver la vista gigante con el proveedor y sus archivos
        return proveedorMapper.toProveedorCompletoResponse(proveedor, documentosResponse);
    }
}