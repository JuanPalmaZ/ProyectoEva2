package cl.paris.marketplace.ms.venta.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import cl.paris.marketplace.ms.venta.dto.ProductoResponse;

@FeignClient(
        name = "ms-productos",
        configuration = FeignClientConfig.class
)
public interface ProductoClient {

    @GetMapping("/api/productos/{id}")
    ProductoResponse obtenerProductoPorId(
            @PathVariable("id") UUID id);

    @PatchMapping("/api/productos/{id}/stock")
    ProductoResponse actualizarStock(
            @PathVariable("id") UUID id,
            @RequestParam("cantidad") Integer cantidad);
}