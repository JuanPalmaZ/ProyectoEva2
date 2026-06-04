package cl.paris.marketplace.ms.venta.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DetalleVentaRequest {

    @NotNull(message = "El ID del producto es obligatorio")
    private UUID productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima de compra es 1")
    private Integer cantidad;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccion;
}