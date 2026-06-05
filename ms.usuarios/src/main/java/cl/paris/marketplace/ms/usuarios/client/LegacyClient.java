package cl.paris.marketplace.ms.usuarios.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import cl.paris.marketplace.ms.usuarios.dto.LegacySyncRequest;

@FeignClient(
    name = "legacy",
    configuration = FeignClientConfig.class
)
public interface LegacyClient {

    @PostMapping("/api/legacy/sincronizar")
    void sincronizarUsuario(
            @RequestBody LegacySyncRequest request
    );
}