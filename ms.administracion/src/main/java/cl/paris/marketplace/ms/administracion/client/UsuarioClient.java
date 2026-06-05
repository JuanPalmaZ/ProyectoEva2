package cl.paris.marketplace.ms.administracion.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-usuarios", configuration = FeignClientConfig.class)
public interface UsuarioClient {

    @PutMapping("/api/usuarios/{id}/estado-baneo")
    void actualizarEstadoBaneo(
            @PathVariable("id") UUID id, 
            @RequestParam("baneo") Boolean baneo);
}