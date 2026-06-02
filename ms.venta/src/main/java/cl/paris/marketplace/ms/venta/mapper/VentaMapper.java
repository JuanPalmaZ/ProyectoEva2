package cl.paris.marketplace.ms.venta.mapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import cl.paris.marketplace.ms.venta.dto.DetalleVentaRequest;
import cl.paris.marketplace.ms.venta.dto.DetalleVentaResponse;
import cl.paris.marketplace.ms.venta.dto.VentaRequest;
import cl.paris.marketplace.ms.venta.dto.VentaResponse;
import cl.paris.marketplace.ms.venta.model.DetalleVenta;
import cl.paris.marketplace.ms.venta.model.Venta;

public interface VentaMapper {

    // Ahora recibe el clienteId extraído del token
    static Venta toModel(VentaRequest request, UUID clienteId) {

        Venta venta = Venta.builder()
                .clienteId(clienteId) // Se inyecta el ID seguro
                .build();

        if (request.getItems() != null) {
            for (DetalleVentaRequest item : request.getItems()) {
                DetalleVenta detalle = DetalleVenta.builder()
                        .productoId(item.getProductoId())
                        .proveedorId(item.getProveedorId())
                        .cantidad(item.getCantidad())
                        .direccion(item.getDireccion())
                        .build();

                venta.addDetalle(detalle);
            }
        }

        return venta;
    }

    static VentaResponse toResponse(Venta venta) {

        List<DetalleVentaResponse> detallesResponse =
                venta.getDetalles()
                        .stream()
                        .map(detalle -> DetalleVentaResponse.builder()
                                .productoId(detalle.getProductoId())
                                .cantidad(detalle.getCantidad())
                                .precioUnitario(detalle.getPrecioUnitario())
                                .subtotal(detalle.getSubtotal())
                                .estado(detalle.getEstado().name())
                                .direccion(detalle.getDireccion())
                                .build())
                        .collect(Collectors.toList());

        return VentaResponse.builder()
                .idVenta(venta.getId())
                .fecha(venta.getFechaCompra())
                .totalPagar(venta.getTotal())
                .mensaje("Operación exitosa")
                .detalles(detallesResponse)
                .build();
    }
}