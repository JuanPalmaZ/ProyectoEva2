package cl.paris.marketplace.ms.feedback.mapper;

import org.springframework.stereotype.Component;
import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.model.Feedback;
import java.util.UUID;

@Component
public class FeedbackMapper {

    public Feedback toEntity(FeedbackRequest request, UUID clienteId, UUID vendedorId) {
        Feedback feedback = new Feedback();
        feedback.setClienteId(clienteId);
        feedback.setProductoId(request.productoId());
        feedback.setVendedorId(vendedorId);
        feedback.setCalificacion(request.calificacion());
        feedback.setComentario(request.comentario());
        return feedback;
    }

    public FeedbackResponse toResponse(Feedback feedback) {
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