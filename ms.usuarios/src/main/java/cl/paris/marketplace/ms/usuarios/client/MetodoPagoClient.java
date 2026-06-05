package cl.paris.marketplace.ms.usuarios.client;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.paris.marketplace.ms.usuarios.dto.MetodoPagoResponse;

@FeignClient(
    name = "ms-clientes", 
    configuration = FeignClientConfig.class 
)
public interface MetodoPagoClient {
    @GetMapping("/api/clientes/usuario/{usuarioId}/metodos-pago")
    List<MetodoPagoResponse> obtenerMetodosPagoUsuario(@PathVariable("usuarioId") UUID usuarioId);
}