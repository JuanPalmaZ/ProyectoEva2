package cl.paris.marketplace.ms.feedback.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================================
    // 1. CAPTURA DE ERRORES DE VALIDACIÓN (DTOs / Records)
    // =========================================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCausa = new HashMap<>();
        
        // Extrae dinámicamente qué campo falló y qué mensaje de @NotBlank, @Min o @Max gatilló
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCausa.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400
        respuesta.put("error", "Bad Request - Error de Validación");
        respuesta.put("errors", erroresCausa); // Entrega el mapa ordenado con los detalles

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // 2. CAPTURA DE ERRORES DE NEGOCIO (RuntimeException de Feedback)
    // =========================================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarErroresNegocio(RuntimeException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value()); // Código 400
        respuesta.put("error", "Feedback Business Error");
        respuesta.put("message", ex.getMessage()); // Captura el texto exacto que pusimos en el Service
        respuesta.put("path", request.getDescription(false).replace("uri=", "")); // Indica el endpoint que falló
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}