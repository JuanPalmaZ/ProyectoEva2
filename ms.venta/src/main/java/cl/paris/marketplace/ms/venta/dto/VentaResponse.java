package cl.paris.marketplace.ms.venta.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VentaResponse {
    private UUID idVenta;
    private LocalDateTime fecha;
    private BigDecimal totalPagar;
    private String mensaje;
    private List<DetalleVentaResponse> detalles;
}