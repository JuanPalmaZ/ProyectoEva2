package cl.paris.marketplace.ms.feedback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Candado de seguridad adaptado
import org.springframework.web.bind.annotation.*;

import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.service.FeedbackService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    // Inyección por constructor idéntica al estándar estricto de tu equipo
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ==========================================
    // ENDPOINTS: ACCIÓN DEL CLIENTE (ESCRITURA)
    // ==========================================
    
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") // REGLA DE ORO: Solo los usuarios con rol CLIENTE pueden publicar una reseña tras su compra
    public ResponseEntity<FeedbackResponse> registrarFeedback(@Valid @RequestBody FeedbackRequest request) {
        FeedbackResponse response = feedbackService.registrarFeedback(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED); // Retorna 201 Created idéntico a tu plantilla
    }

    // ==========================================
    // ENDPOINTS: EXPOSICIÓN DE RESEÑAS (LECTURA)
    // ==========================================

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // Historial masivo para auditoría interna o paneles
    public ResponseEntity<List<FeedbackResponse>> listarTodosLosFeedbacks() {
        return ResponseEntity.ok(feedbackService.listarTodosLosFeedbacks()); // Retorna 200 OK nativo
    }

    @GetMapping("/producto/{productoId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // Expone las reseñas en la vitrina del producto para los usuarios
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorProducto(@PathVariable UUID productoId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorProducto(productoId));
    }

    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // CUMPLE EL DOCUMENTO: Expone la reputación y opiniones de un vendedor específico
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorVendedor(@PathVariable UUID vendedorId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorVendedor(vendedorId));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // Permite ver el historial de opiniones que ha redactado un cliente
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorCliente(clienteId));
    }
}