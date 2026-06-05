package cl.paris.marketplace.ms.administracion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.administracion.client.ProductoClient;
import cl.paris.marketplace.ms.administracion.client.UsuarioClient;
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
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public AdminService(
            LogAuditoriaRepository logRepository, 
            AdminMapper adminMapper,
            ProductoClient productoClient,
            UsuarioClient usuarioClient) {
        this.logRepository = logRepository;
        this.adminMapper = adminMapper;
        this.productoClient = productoClient;
        this.usuarioClient = usuarioClient;
    }

    @Transactional
    public AdminAccionResponse registrarAccionManual(AdminAccionRequest request, UUID adminId) {
        if (request.accion() == null || request.accion().trim().isEmpty()) {
            throw new RuntimeException("El tipo de acción de auditoría no puede estar vacío.");
        }

        LogAuditoria log = adminMapper.toEntity(request);
        log.setUsuarioId(adminId); 
        
        LogAuditoria logGuardado = logRepository.save(log);
        return adminMapper.toResponse(logGuardado);
    }

    @Transactional(readOnly = true)
    public List<AdminAccionResponse> listarHistorial() {
        return logRepository.findAll().stream()
                .map(adminMapper::toResponse)
                .toList(); 
    }

    @Transactional(readOnly = true)
    public List<AdminAccionResponse> listarPorUsuarioAdmin(UUID usuarioId) {
        List<LogAuditoria> logs = logRepository.findByUsuarioIdOrderByFechaAccionDesc(usuarioId);
        if (logs.isEmpty()) {
            throw new RuntimeException("No se encontraron registros de auditoría para el administrador especificado.");
        }
        return logs.stream().map(adminMapper::toResponse).toList();
    }

   @Transactional
    public AdminAccionResponse moderarProducto(ModerarProductoRequest request, UUID adminId) {
        String estadoUpper = request.estado().toUpperCase();
        if (!estadoUpper.equals("APROBADO") && !estadoUpper.equals("RECHAZADO")) {
            throw new RuntimeException("Estado de moderación inválido. Debe ser 'APROBADO' o 'RECHAZADO'.");
        }

       try {
            productoClient.actualizarEstadoModeracion(request.productoId(), estadoUpper);
        } catch (Exception e) { 
            System.err.println("=== ERROR INESPERADO AL LLAMAR A MS-PRODUCTOS ===");
            System.err.println("Clase del error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace(); 
            throw new RuntimeException("Fallo al intentar aplicar la moderación. Revisa la consola.");
        }


        String detalleLog = String.format("El administrador cambió el estado del producto ID [%s] a [%s]. Motivo: %s", 
                request.productoId(), estadoUpper, request.motivo());

        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(adminId);
        log.setAccion("MODERAR_PRODUCTO");
        log.setDetalle(detalleLog);

        LogAuditoria guardado = logRepository.save(log);
        return adminMapper.toResponse(guardado);
    }

   @Transactional
    public AdminAccionResponse cambiarEstadoUsuario(EstadoUsuarioRequest request, UUID adminId) {

       try {
            usuarioClient.actualizarEstadoBaneo(request.usuarioId(), request.baneo());
        } catch (Exception e) { 
            System.err.println("=== ERROR INESPERADO AL LLAMAR A MS-USUARIOS ===");
            System.err.println("Clase del error: " + e.getClass().getName());
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Fallo al intentar cambiar el estado del usuario.");
        }

        String accionTipo = request.baneo() ? "BANEAR_USUARIO" : "ACTIVAR_USUARIO";
        String detalleLog = String.format("El administrador aplicó [%s] al usuario ID [%s]. Razón: %s", 
                accionTipo, request.usuarioId(), request.razon());

        LogAuditoria log = new LogAuditoria();
        log.setUsuarioId(adminId);
        log.setAccion(accionTipo);
        log.setDetalle(detalleLog);

        LogAuditoria guardado = logRepository.save(log);
        return adminMapper.toResponse(guardado);
    }

}