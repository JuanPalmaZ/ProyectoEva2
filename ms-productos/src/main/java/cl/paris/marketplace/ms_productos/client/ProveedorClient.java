package cl.paris.marketplace.ms_productos.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "ms-proveedores", // Recuerda que este nombre debe coincidir con el spring.application.name de tu microservicio de proveedores
    configuration = FeignClientConfig.class // Engancha tu interceptor local para pasar el Bearer Token
)
public interface ProveedorClient {

    // Retorna Object para que Jackson acepte cualquier JSON que devuelva el ms-proveedores 
    // sin que te tire errores por campos o propiedades que no tengas mapeadas aquí.
    @GetMapping("/api/proveedores/{id}/completo")
    Object obtenerProveedorSimplificado(@PathVariable("id") UUID id);
}