package cl.paris.marketplace.ms.clientes.mapper;

import org.springframework.stereotype.Component;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.model.MetodoPago;

@Component
public class ClienteMapper {

    // ==========================================
    // MAPPERS PARA METODO DE PAGO
    // ==========================================

    // Recibe directamente el DTO de entrada. Ya no necesita el objeto Usuario,
    // solo tomará el UUID que venga en el JSON.
    public MetodoPago toMetodoPagoEntity(MetodoPagoRequest request) {
        MetodoPago metodoPago = new MetodoPago();
        
        // Usamos la Soft Foreign Key (el UUID) en lugar del objeto completo
    
        metodoPago.setTokenTarjeta(request.tokenTarjeta());
        metodoPago.setTipo(request.tipo());
        
        return metodoPago;
    }

    public MetodoPagoResponse toMetodoPagoResponse(MetodoPago metodoPago) {
        return new MetodoPagoResponse(
                metodoPago.getId(),
                metodoPago.getUsuarioId(), // Agregado para que el frontend sepa de quién es la tarjeta
                metodoPago.getTipo()
        );
    }
}