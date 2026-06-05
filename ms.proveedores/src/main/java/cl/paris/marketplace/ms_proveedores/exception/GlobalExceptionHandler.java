package cl.paris.marketplace.ms_proveedores.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura los 404 (Ej: Proveedor o Documento no encontrado)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> manejarResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.NOT_FOUND.value());
        respuesta.put("error", "Not Found");
        respuesta.put("message", ex.getMessage());
        respuesta.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    // 2. Captura los errores de validación de los DTOs (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCausa = new HashMap<>();
        
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCausa.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Bad Request");
        respuesta.put("message", "Error en la validación de los datos enviados.");
        respuesta.put("errors", erroresCausa);

        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    // 3. Captura los errores de Reglas de Negocio (Ej: RUT duplicado, Documento ya existe)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> manejarReglasDeNegocio(RuntimeException ex, WebRequest request) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("timestamp", LocalDateTime.now());
        respuesta.put("status", HttpStatus.BAD_REQUEST.value());
        respuesta.put("error", "Business Rule Violation");
        respuesta.put("message", ex.getMessage());
        respuesta.put("path", request.getDescription(false).replace("uri=", ""));
        
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
}