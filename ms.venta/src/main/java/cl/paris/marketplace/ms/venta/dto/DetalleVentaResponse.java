package cl.paris.marketplace.ms.venta.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleVentaResponse {
    private UUID productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String estado;
    private String direccion;
}