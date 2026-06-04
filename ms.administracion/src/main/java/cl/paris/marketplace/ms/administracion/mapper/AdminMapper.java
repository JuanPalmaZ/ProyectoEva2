package cl.paris.marketplace.ms.administracion.mapper;

import org.springframework.stereotype.Component;
import cl.paris.marketplace.ms.administracion.dto.AdminAccionRequest;
import cl.paris.marketplace.ms.administracion.dto.AdminAccionResponse;
import cl.paris.marketplace.ms.administracion.model.LogAuditoria;

@Component
public class AdminMapper {

    public LogAuditoria toEntity(AdminAccionRequest request) {
        if (request == null) return null;
        
        LogAuditoria log = new LogAuditoria();
        log.setAccion(request.accion());
        log.setDetalle(request.detalle());
        return log;
    }

    public AdminAccionResponse toResponse(LogAuditoria log) {
        if (log == null) return null;

        return new AdminAccionResponse(
                log.getId(),
                log.getUsuarioId(),
                log.getAccion(),
                log.getDetalle(),
                log.getFechaAccion()
        );
    }
}