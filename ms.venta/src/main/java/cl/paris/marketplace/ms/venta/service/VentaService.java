package cl.paris.marketplace.ms.venta.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.venta.client.NotificacionClient;
import cl.paris.marketplace.ms.venta.client.ProductoClient;
import cl.paris.marketplace.ms.venta.dto.NotificacionRequest;
import cl.paris.marketplace.ms.venta.dto.ProductoResponse;
import cl.paris.marketplace.ms.venta.dto.VentaRequest;
import cl.paris.marketplace.ms.venta.dto.VentaResponse;
import cl.paris.marketplace.ms.venta.mapper.VentaMapper;
import cl.paris.marketplace.ms.venta.model.DetalleVenta;
import cl.paris.marketplace.ms.venta.model.EstadoVenta;
import cl.paris.marketplace.ms.venta.model.Venta;
import cl.paris.marketplace.ms.venta.repository.VentaRepository;
import feign.FeignException;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoClient productoClient;
    private final NotificacionClient notificacionClient; 

    public VentaService(
            VentaRepository ventaRepository,
            ProductoClient productoClient,
            NotificacionClient notificacionClient) {

        this.ventaRepository = ventaRepository;
        this.productoClient = productoClient;
        this.notificacionClient = notificacionClient;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: REGISTRO DE VENTAS
    // ==========================================

    @Transactional
    public VentaResponse registrarVenta(VentaRequest request, UUID clienteId) {

        // Se pasa el clienteId al Mapper
        Venta nuevaVenta = VentaMapper.toModel(request, clienteId); 

        BigDecimal totalCompra = BigDecimal.ZERO;

        for (DetalleVenta detalle : nuevaVenta.getDetalles()) {

            ProductoResponse productoReal;

            try {
                productoReal = productoClient.obtenerProductoPorId(
                        detalle.getProductoId());

            } catch (FeignException.NotFound e) {
                throw new RuntimeException(
                        "Error: El producto con ID "
                                + detalle.getProductoId()
                                + " no existe.");

            } catch (Exception e) {
                throw new RuntimeException(
                        "Error de comunicación con el servicio de productos.");
            }

            if (productoReal.getStock() < detalle.getCantidad()) {
                throw new RuntimeException(
                        "Stock insuficiente para el producto "
                                + productoReal.getNombre()
                                + ". Stock disponible: "
                                + productoReal.getStock());
            }

            // ==========================================
            // El proveedor se autodescubre desde el producto, no desde el cliente.
            // ==========================================
            detalle.setProveedorId(productoReal.getProveedorId()); 

            detalle.setEstado(EstadoVenta.PENDIENTE);
            detalle.setPrecioUnitario(productoReal.getPrecio()); 

            BigDecimal subtotal =
                    productoReal.getPrecio().multiply(
                            BigDecimal.valueOf(detalle.getCantidad()));

            detalle.setSubtotal(subtotal);
            totalCompra = totalCompra.add(subtotal);
        }

        nuevaVenta.setTotal(totalCompra);

        // Guardar primero la venta
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        // Descontar stock en ms-productos
        for (DetalleVenta detalle : ventaGuardada.getDetalles()) {
            try {
                productoClient.actualizarStock(
                        detalle.getProductoId(),
                        -detalle.getCantidad());

            } catch (Exception e) {
                throw new RuntimeException(
                        "Error al actualizar stock del producto "
                                + detalle.getProductoId());
            }
        }

        // ==========================================
        // ENVIAR NOTIFICACIÓN DE COMPRA
        // ==========================================
        try {
            // Extraemos el correo del cliente que hizo la petición directamente desde el Token JWT
            String emailCliente = SecurityContextHolder.getContext().getAuthentication().getName();
            
            NotificacionRequest notificacion = new NotificacionRequest(
                    emailCliente,
                    "Confirmación de Pedido #" + ventaGuardada.getId(),
                    "¡Hola! Tu compra por un total de $" + ventaGuardada.getTotal() + " ha sido confirmada y procesada exitosamente."
            );
            
            notificacionClient.enviarNotificacion(notificacion);
        } catch (Exception e) {
            // Un fallo en el envío del correo no deshace la venta (evita perder el registro del pago)
            System.err.println("Aviso: Venta guardada con éxito, pero falló la comunicación con ms.notificacion. " + e.getMessage());
        }

        return VentaMapper.toResponse(ventaGuardada);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: CONSULTAS
    // ==========================================

    @Transactional(readOnly = true)
    public VentaResponse buscarPorId(UUID ventaId) {

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Error: Venta no encontrada."));

        return VentaMapper.toResponse(venta);
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> buscarPorCliente(UUID clienteId) {

        return ventaRepository.findByClienteId(clienteId)
                .stream()
                .map(VentaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VentaResponse> buscarPorRangoFechas(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        return ventaRepository.findByFechaCompraBetween(
                        fechaInicio,
                        fechaFin)
                .stream()
                .map(VentaMapper::toResponse)
                .toList();
    }
}