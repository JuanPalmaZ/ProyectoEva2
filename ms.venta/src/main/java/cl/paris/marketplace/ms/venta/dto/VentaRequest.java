package cl.paris.marketplace.ms.venta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class VentaRequest {

    // El clienteId se elimina porque ahora lo sacaremos del Token
    @NotEmpty(message = "El carrito no puede estar vacío")
    @Valid 
    private List<DetalleVentaRequest> items;
}