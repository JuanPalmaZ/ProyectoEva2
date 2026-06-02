package cl.paris.marketplace.ms.venta.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.paris.marketplace.ms.venta.dto.NotificacionRequest;

@FeignClient(
        name = "ms-notificacion",
        configuration = FeignClientConfig.class
)
public interface NotificacionClient {

    @PostMapping("/api/notificaciones")
    void enviarNotificacion(@RequestBody NotificacionRequest request);
}