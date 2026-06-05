package cl.paris.marketplace.ms.feedback.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.paris.marketplace.ms.feedback.client.ProductoClient;
import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.dto.ProductoResponse;
import cl.paris.marketplace.ms.feedback.mapper.FeedbackMapper;
import cl.paris.marketplace.ms.feedback.model.Feedback;
import cl.paris.marketplace.ms.feedback.repository.FeedbackRepository;
import feign.FeignException;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final ProductoClient productoClient; // Inyectamos OpenFeign

    public FeedbackService(
            FeedbackRepository feedbackRepository, 
            FeedbackMapper feedbackMapper,
            ProductoClient productoClient) {
        this.feedbackRepository = feedbackRepository;
        this.feedbackMapper = feedbackMapper;
        this.productoClient = productoClient;
    }

    @Transactional
    public FeedbackResponse registrarFeedback(FeedbackRequest request, UUID clienteIdFidedigno) {
        
        // ======================================================
        // PUENTE INTERNO: Validar producto y autodescrubir al vendedor
        // ======================================================
        ProductoResponse productoReal;
        try {
            productoReal = productoClient.obtenerProductoPorId(request.productoId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Error: El producto a evaluar no existe en el catálogo.");
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación al intentar validar el producto.");
        }

        UUID vendedorAutodescubierto = productoReal.proveedorId();

        Feedback feedback = feedbackMapper.toEntity(request, clienteIdFidedigno, vendedorAutodescubierto);
        Feedback feedbackGuardado = feedbackRepository.save(feedback);
        
        return feedbackMapper.toResponse(feedbackGuardado);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> listarTodosLosFeedbacks() {
        return feedbackRepository.findAll().stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorProducto(UUID productoId) {
        return feedbackRepository.findByProductoIdOrderByFechaCreacionDesc(productoId).stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorVendedor(UUID vendedorId) {
        return feedbackRepository.findByVendedorIdOrderByFechaCreacionDesc(vendedorId).stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> obtenerFeedbackPorCliente(UUID clienteId) {
        return feedbackRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId).stream()
                .map(feedbackMapper::toResponse)
                .toList();
    }
}