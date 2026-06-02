package cl.paris.marketplace.ms.feedback.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FeedbackResponse(
    UUID id,
    UUID clienteId,
    UUID productoId,
    UUID vendedorId,
    Integer calificacion,
    String comentario,
    LocalDateTime fechaCreacion
) {}