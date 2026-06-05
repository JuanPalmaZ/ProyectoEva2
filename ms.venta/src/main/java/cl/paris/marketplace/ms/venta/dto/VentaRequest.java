package cl.paris.marketplace.ms.venta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VentaRequest {

    @NotEmpty(message = "El carrito no puede estar vacío")
    @Valid 
    private List<DetalleVentaRequest> items;
}