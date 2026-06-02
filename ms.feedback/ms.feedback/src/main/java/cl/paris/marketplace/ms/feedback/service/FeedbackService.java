package cl.paris.marketplace.ms.feedback.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.mapper.FeedbackMapper;
import cl.paris.marketplace.ms.feedback.model.Feedback;
import cl.paris.marketplace.ms.feedback.repository.FeedbackRepository;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    // Inyección por constructor idéntica al estándar estricto de tu equipo
    public FeedbackService(FeedbackRepository feedbackRepository, FeedbackMapper feedbackMapper) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: REGISTRO DE RESEÑAS
    // ==========================================
    
    @Transactional
    public FeedbackResponse registrarFeedback(FeedbackRequest request) {
        // Validación previa antes de guardar (Garantiza requerimiento del documento: Producto y/o Vendedor)
        if (request.productoId() == null && request.vendedorId() == null) {
            throw new RuntimeException("Debe asociar la reseña a un producto o a un vendedor de forma obligatoria.");
        }

        // Validación complementaria de la escala de calificaciones
        if (request.calificacion() < 1 || request.calificacion() > 5) {
            throw new RuntimeException("La calificación es inválida. Debe estar en el rango de 1 a 5 estrellas.");
        }

        Feedback feedback = feedbackMapper.toEntity(request);
        Feedback feedbackGuardado = feedbackRepository.save(feedback);
        
        return feedbackMapper.toResponse(feedbackGuardado);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> listarTodosLosFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(feedbackMapper::toResponse)
                .toList(); // Uso de .toList() nativo igual que tu compañero
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: EXPOSICIÓN Y CONSULTAS (CUMPLE CUADRO INFORMATIVO)
    // ==========================================

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorProducto(UUID productoId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductoIdOrderByFechaCreacionDesc(productoId);
        
        // Estilo espejo de error si no hay registros en la base de datos Neon
        if (feedbacks.isEmpty()) {
            throw new RuntimeException("No se encontraron calificaciones ni reseñas para el producto especificado.");
        }

        return feedbacks.stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorVendedor(UUID vendedorId) {
        List<Feedback> feedbacks = feedbackRepository.findByVendedorIdOrderByFechaCreacionDesc(vendedorId);
        
        // Estilo espejo de error para asegurar la consistencia del negocio con el vendedor
        if (feedbacks.isEmpty()) {
            throw new RuntimeException("No se encontraron calificaciones ni reseñas para el vendedor especificado.");
        }

        return feedbacks.stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorCliente(UUID clienteId) {
        List<Feedback> feedbacks = feedbackRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId);
        
        // Estilo espejo de error por si el historial de compras/reseñas del cliente está limpio
        if (feedbacks.isEmpty()) {
            throw new RuntimeException("El cliente especificado no registra historial de opiniones publicadas.");
        }

        return feedbacks.stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }
}