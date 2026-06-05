package cl.paris.marketplace.ms.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import cl.paris.marketplace.ms.usuarios.dto.LegacySyncRequest;

@FeignClient(name = "ms-legacy", configuration = FeignClientConfig.class)
public interface LegacyClient {

    // Fíjate bien en la parte de headers, debe ser idéntica a la que configuramos en el controlador
    @PostMapping(value = "/api/legacy/sincronizar", headers = "X-Internal-Secret=paris-legacy-secret-2026")
    void sincronizarUsuario(
            @RequestBody LegacySyncRequest request
    );
}