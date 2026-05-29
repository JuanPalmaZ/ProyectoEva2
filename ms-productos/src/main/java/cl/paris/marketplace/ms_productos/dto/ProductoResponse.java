package cl.paris.marketplace.ms_productos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductoResponse(
    UUID id,
    String sku,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Integer stock,
    UUID proveedorId,
    UUID categoriaId,
    String categoriaNombre,
    String categoriaDescripcion,
    Boolean activo,
    LocalDateTime fechaCreacion
) {}

