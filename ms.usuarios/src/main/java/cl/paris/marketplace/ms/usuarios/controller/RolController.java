package cl.paris.marketplace.ms.usuarios.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.usuarios.dto.RolRequest;
import cl.paris.marketplace.ms.usuarios.dto.RolResponse;
import cl.paris.marketplace.ms.usuarios.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles") 
public class RolController {

    private final UsuarioService usuarioService;

    // Inyección manual del servicio
    public RolController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody RolRequest request) {
        RolResponse response = usuarioService.crearRol(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(usuarioService.listarRoles());
    }
}