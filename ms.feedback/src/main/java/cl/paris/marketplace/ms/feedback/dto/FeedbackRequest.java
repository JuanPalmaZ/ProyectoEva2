package cl.paris.marketplace.ms.feedback.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
    
    @NotNull(message = "El ID del producto es obligatorio") 
    UUID productoId,
    
    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1 estrella")
    @Max(value = 5, message = "La calificación máxima es 5 estrellas") 
    Integer calificacion,
    
    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(max = 1000, message = "Máximo 1000 caracteres") 
    String comentario
) {}