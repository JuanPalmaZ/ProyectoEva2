package cl.paris.marketplace.ms.feedback.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.feedback.dto.FeedbackRequest;
import cl.paris.marketplace.ms.feedback.dto.FeedbackResponse;
import cl.paris.marketplace.ms.feedback.service.FeedbackService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // ==========================================
    // ENDPOINTS: ESCRITURA
    // ==========================================
    
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')") 
    public ResponseEntity<?> registrarFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication
    ) {
        try {
            if (authentication.getCredentials() == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error Crítico: Identidad no encontrada en el token.");
            }

            UUID clienteId;
            try {
                clienteId = UUID.fromString(authentication.getCredentials().toString());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El formato del Token no es válido.");
            }
            
            FeedbackResponse response = feedbackService.registrarFeedback(request, clienteId);
            return new ResponseEntity<>(response, HttpStatus.CREATED); 

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error procesando la reseña: " + e.getMessage());
        }
    }

    // ==========================================
    // ENDPOINTS: LECTURA PÚBLICA / PRIVADA
    // ==========================================

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<List<FeedbackResponse>> listarTodosLosFeedbacks() {
        return ResponseEntity.ok(feedbackService.listarTodosLosFeedbacks()); 
    }

    // Los productos y su reputación son públicos para cualquier usuario logueado
    @GetMapping("/producto/{productoId}")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorProducto(@PathVariable UUID productoId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorProducto(productoId));
    }

    @GetMapping("/vendedor/{vendedorId}")
    @PreAuthorize("isAuthenticated()") 
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorVendedor(@PathVariable UUID vendedorId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorVendedor(vendedorId));
    }

    // ¡CANDADO ANTI-IDOR! Solo tú (o un admin) pueden ver tu historial de reseñas
    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and #clienteId.toString() == authentication.credentials)")
    public ResponseEntity<List<FeedbackResponse>> obtenerFeedbackPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(feedbackService.obtenerFeedbackPorCliente(clienteId));
    }
}