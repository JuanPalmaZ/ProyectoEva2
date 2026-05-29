package cl.paris.marketplace.ms.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.administracion.dto.AdminAccionRequest;
import cl.paris.marketplace.ms.administracion.dto.AdminAccionResponse;
import cl.paris.marketplace.ms.administracion.dto.EstadoUsuarioRequest;
import cl.paris.marketplace.ms.administracion.dto.ModerarProductoRequest;
import cl.paris.marketplace.ms.administracion.mapper.AdminMapper;
import cl.paris.marketplace.ms.administracion.model.LogAuditoria;
import cl.paris.marketplace.ms.administracion.repository.LogAuditoriaRepository;

@Service
public class AdminService {

    private final LogAuditoriaRepository logRepository;
    private final AdminMapper adminMapper;

    // Inyección por constructor idéntica al estándar de tu equipo
    public AdminService(LogAuditoriaRepository logRepository, AdminMapper adminMapper) {
        this.logRepository = logRepository;
        this.adminMapper = adminMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: AUDITORÍA MANUAL
    // ==========================================
    
    @Transactional
    public AdminAccionResponse registrarAccionManual(AdminAccionRequest request) {
        // Validación previa antes de guardar (Ejemplo de consistencia con el servicio de usuarios)
        if (request.accion() == null || request.accion().trim().isEmpty()) {
            throw new RuntimeException("El tipo de acción de auditoría no puede estar vacío.");
        }

        LogAuditoria log = adminMapper.toEntity(request);
        LogAuditoria logGuardado = logRepository.save(log);
        
        return adminMapper.toResponse(logGuardado);
    }

    @Transactional(readOnly = true)
    public List<AdminAccionResponse> listarHistorial() {
        return logRepository.findAll().stream()
                .map(adminMapper::toResponse)
                .toList(); // Uso de .toList() nativo igual que tu compañero
    }

    @Transactional(readOnly = true)
    public List<AdminAccionResponse> listarPorUsuarioAdmin(UUID usuarioId) {
        List<LogAuditoria> logs = logRepository.findByUsuarioIdOrderByFechaAccionDesc(usuarioId);
        
        if (logs.isEmpty()) {
            throw new RuntimeException("No se encontraron registros de auditoría para el administrador especificado.");
        }

        return logs.stream()
                .map(adminMapper::toResponse)
                .toList();
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: MODERACIÓN (ACCIONES REALES DE ADMIN)
    // ==========================================

    @Transactional
    public AdminAccionResponse moderarProducto(ModerarProductoRequest request) {
        // Validación de consistencia del estado del negocio en Paris.cl
        String estadoUpper = request.estado().toUpperCase();
        if (!estadoUpper.equals("APROBADO") && !estadoUpper.equals("RECHAZADO")) {
            throw new RuntimeException("Estado de moderación inválido. Debe ser 'APROBADO' o 'RECHAZADO'.");
        }

        String detalleLog = String.format("El administrador cambió el estado del producto ID [%s] a [%s]. Motivo: %s", 
                request.productoId(), estadoUpper, request.motivo());

        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(request.adminId());
        log.setAccion("MODERAR_PRODUCTO");
        log.setDetalle(detalleLog);
        
        LogAuditoria guardado = logRepository.save(log);
        return adminMapper.toResponse(guardado);
    }

    @Transactional
    public AdminAccionResponse cambiarEstadoUsuario(EstadoUsuarioRequest request) {
        String accionTipo = request.baneo() ? "BANEAR_USUARIO" : "ACTIVAR_USUARIO";
        String detalleLog = String.format("El administrador aplicó [%s] al usuario ID [%s]. Razón: %s", 
                accionTipo, request.usuarioId(), request.razon());

        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(request.adminId());
        log.setAccion(accionTipo);
        log.setDetalle(detalleLog);
        
        LogAuditoria guardado = logRepository.save(log);
        return adminMapper.toResponse(guardado);
    }
}