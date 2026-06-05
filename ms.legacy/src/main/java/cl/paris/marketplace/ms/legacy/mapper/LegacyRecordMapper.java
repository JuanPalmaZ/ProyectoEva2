package cl.paris.marketplace.ms.legacy.mapper;

import org.springframework.stereotype.Component;
import cl.paris.marketplace.ms.legacy.dto.LegacySyncRequest;
import cl.paris.marketplace.ms.legacy.dto.LegacyRecordResponse;
import cl.paris.marketplace.ms.legacy.model.LegacyRecord;

@Component
public class LegacyRecordMapper {

    public LegacyRecord toEntity(LegacySyncRequest request) {
        if (request == null) return null; 

        LegacyRecord record = new LegacyRecord();
        record.setCodigoAntiguo(request.codigoAntiguo());
        record.setTipoEntidad(request.tipoEntidad().toUpperCase());
        record.setDatosMigrados(request.datosMigrados());
        return record;
    }

    public LegacyRecordResponse toResponse(LegacyRecord record) {
        if (record == null) return null; 

        return new LegacyRecordResponse(
                record.getId(),
                record.getCodigoAntiguo(),
                record.getTipoEntidad(),
                record.getDatosMigrados(),
                record.getEstado(),
                record.getFechaSincronizacion()
        );
    }
}