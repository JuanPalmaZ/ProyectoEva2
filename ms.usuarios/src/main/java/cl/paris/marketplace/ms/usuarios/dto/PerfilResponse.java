package cl.paris.marketplace.ms.usuarios.dto;
import java.util.UUID;

public record PerfilResponse(
    UUID id,
    String primerNombre,
    String segundoNombre,
    String primerApellido,
    String segundoApellido,
    String telefono
) {}