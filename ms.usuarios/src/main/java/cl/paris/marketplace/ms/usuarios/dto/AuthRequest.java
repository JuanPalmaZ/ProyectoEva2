package cl.paris.marketplace.ms.usuarios.dto;

public record AuthRequest(
    String email, 
    String password
) {}