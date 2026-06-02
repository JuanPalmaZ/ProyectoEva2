package cl.paris.marketplace.ms.feedback.mapper;

import org.springframework.stereotype.Component;
import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.model.Feedback;

@Component
public class FeedbackMapper {

    // Transforma el Request enviado por el cliente a la entidad JPA/Lombok
    public Feedback toEntity(FeedbackRequest request) {
        if (request == null) return null; // Validación estricta idéntica a tu plantilla
        
        Feedback feedback = new Feedback();
        feedback.setClienteId(request.clienteId());
        feedback.setProductoId(request.productoId());
        feedback.setVendedorId(request.vendedorId()); // Calificación de vendedor requerida por el documento
        feedback.setCalificacion(request.calificacion());
        feedback.setComentario(request.comentario());
        
        return feedback;
    }

    // Transforma la entidad de la base de datos Neon al Record de respuesta para Postman
    public FeedbackResponse toResponse(Feedback feedback) {
        if (feedback == null) return null; // Validación estricta idéntica a tu plantilla

        return new FeedbackResponse(
                feedback.getId(),
                feedback.getClienteId(),
                feedback.getProductoId(),
                feedback.getVendedorId(),
                feedback.getCalificacion(),
                feedback.getComentario(),
                feedback.getFechaCreacion()
        );
    }
}