package cl.paris.marketplace.ms.clientes.dto;
import java.util.List;
import java.util.UUID;

public record UsuarioCompletoResponse(
    UUID id,
    String email,
    RolResponse rol,
    PerfilResponse perfil,
    List<MetodoPagoResponse> metodosPago
) {}