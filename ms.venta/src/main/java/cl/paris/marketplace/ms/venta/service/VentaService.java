package cl.paris.marketplace.ms.venta.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.venta.client.ProductoClient;
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

    public VentaService(
            VentaRepository ventaRepository,
            ProductoClient productoClient) {

        this.ventaRepository = ventaRepository;
        this.productoClient = productoClient;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: REGISTRO DE VENTAS
    // ==========================================

    @Transactional
    public VentaResponse registrarVenta(VentaRequest request) {

        Venta nuevaVenta = VentaMapper.toModel(request);

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