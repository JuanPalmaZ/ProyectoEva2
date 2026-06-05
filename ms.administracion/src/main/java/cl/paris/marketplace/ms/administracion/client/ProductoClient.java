package cl.paris.marketplace.ms.administracion.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-productos", configuration = FeignClientConfig.class)
public interface ProductoClient {
    
    @PutMapping("/api/productos/{id}/estado-moderacion")
    void actualizarEstadoModeracion(
            @PathVariable("id") UUID id, 
            @RequestParam("estado") String estado);
}