package cl.paris.marketplace.ms.feedback.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import cl.paris.marketplace.ms.feedback.dto.ProductoResponse;

@FeignClient(
        name = "ms-productos", 
        configuration = FeignClientConfig.class
)
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(@PathVariable("id") UUID id);
}