package cl.paris.marketplace.ms.feedback.dto;

import jakarta.validation.constraints.*;
import java.util.UUID;

public record FeedbackRequest(
    @NotNull(message = "El ID del cliente es obligatorio") UUID clienteId,
    UUID productoId, // Puede ser null si solo califica al vendedor
    UUID vendedorId, // Puede ser null si solo califica al producto
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5") 
    Integer calificacion,
    
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 1000, message = "Máximo 1000 caracteres") 
    String comentario
) {}