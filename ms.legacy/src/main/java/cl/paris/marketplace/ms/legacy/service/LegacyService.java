package cl.paris.marketplace.ms.legacy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.legacy.dto.LegacySyncRequest;
import cl.paris.marketplace.ms.legacy.dto.LegacyRecordResponse;
import cl.paris.marketplace.ms.legacy.mapper.LegacyRecordMapper;
import cl.paris.marketplace.ms.legacy.model.LegacyRecord;
import cl.paris.marketplace.ms.legacy.repository.LegacyRecordRepository;

@Service
public class LegacyService {

    private final LegacyRecordRepository legacyRecordRepository;
    private final LegacyRecordMapper legacyRecordMapper;

    // Inyección por constructor limpia
    public LegacyService(LegacyRecordRepository legacyRecordRepository, LegacyRecordMapper legacyRecordMapper) {
        this.legacyRecordRepository = legacyRecordRepository;
        this.legacyRecordMapper = legacyRecordMapper;
    }

    @Transactional
    public LegacyRecordResponse sincronizarDatoAntiguo(LegacySyncRequest request) {
        String tipo = request.tipoEntidad().toUpperCase();
        
        // Regla de negocio simulada: Solo procesamos entidades core antiguas válidas
        if (!tipo.equals("PRODUCTO") && !tipo.equals("CLIENTE") && !tipo.equals("STOCK")) {
            throw new RuntimeException("Entidad legacy no soportada. Use: PRODUCTO, CLIENTE o STOCK.");
        }

        LegacyRecord record = legacyRecordMapper.toEntity(request);
        LegacyRecord guardado = legacyRecordRepository.save(record);
        return legacyRecordMapper.toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<LegacyRecordResponse> listarHistorialSincronizaciones() {
        return legacyRecordRepository.findAll().stream()
                .map(legacyRecordMapper::toResponse)
                .toList(); 
    }

    @Transactional(readOnly = true)
    public List<LegacyRecordResponse> listarPorTipoEntidad(String tipoEntidad) {
        List<LegacyRecord> records = legacyRecordRepository.findByTipoEntidadOrderByFechaSincronizacionDesc(tipoEntidad.toUpperCase());
        if (records.isEmpty()) {
            throw new RuntimeException("No se registran sincronizaciones de datos para la entidad especificada.");
        }
        return records.stream()
                .map(legacyRecordMapper::toResponse)
                .toList();
    }
}