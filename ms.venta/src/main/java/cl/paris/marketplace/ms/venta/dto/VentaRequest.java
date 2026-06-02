package cl.paris.marketplace.ms.venta.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequest {

    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotEmpty(message = "El carrito no puede estar vacío")
    @Valid // Obliga a validar también lo que hay adentro de la lista
    private List<DetalleVentaRequest> items;
}