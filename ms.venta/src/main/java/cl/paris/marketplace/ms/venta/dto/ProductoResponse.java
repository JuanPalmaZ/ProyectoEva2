package cl.paris.marketplace.ms.venta.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ProductoResponse {
    private UUID id;
    private String nombre;
    private BigDecimal precio;
    private Integer stock;
    private UUID proveedorId;
}