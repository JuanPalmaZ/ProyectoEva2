package cl.paris.marketplace.ms.clientes.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms.clientes.dto.MetodoPagoRequest;
import cl.paris.marketplace.ms.clientes.dto.MetodoPagoResponse;
import cl.paris.marketplace.ms.clientes.dto.PerfilRequest;
import cl.paris.marketplace.ms.clientes.dto.PerfilResponse;
import cl.paris.marketplace.ms.clientes.dto.RolRequest;
import cl.paris.marketplace.ms.clientes.dto.RolResponse;
import cl.paris.marketplace.ms.clientes.dto.UsuarioCompletoResponse;
import cl.paris.marketplace.ms.clientes.dto.UsuarioRequest;
import cl.paris.marketplace.ms.clientes.dto.UsuarioResponse;
import cl.paris.marketplace.ms.clientes.service.ClienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección de dependencias por constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ==========================================
    // ENDPOINTS: USUARIOS
    // ==========================================
    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> registrarUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = clienteService.registrarUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable UUID id) {
        UsuarioResponse response = clienteService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: PERFILES
    // ==========================================
    @PostMapping("/perfiles")
    public ResponseEntity<PerfilResponse> crearPerfil(@Valid @RequestBody PerfilRequest request) {
        PerfilResponse response = clienteService.crearPerfil(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==========================================
    // ENDPOINTS: MÉTODOS DE PAGO
    // ==========================================
    @PostMapping("/metodos-pago")
    public ResponseEntity<MetodoPagoResponse> agregarMetodoPago(@Valid @RequestBody MetodoPagoRequest request) {
        MetodoPagoResponse response = clienteService.agregarMetodoPago(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/usuarios/{usuarioId}/metodos-pago")
    public ResponseEntity<List<MetodoPagoResponse>> listarMetodosPagoUsuario(@PathVariable UUID usuarioId) {
        List<MetodoPagoResponse> response = clienteService.listarMetodosPagoUsuario(usuarioId);
        return ResponseEntity.ok(response);
    }
    // ==========================================
    // ENDPOINTS: ROLES
    // ==========================================

    @PostMapping("/roles")
    public ResponseEntity<RolResponse> crearRol(@Valid @RequestBody RolRequest request) {
        RolResponse response = clienteService.crearRol(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    // ==========================================
    // ENDPOINTS: ROLES (Agrega esto debajo del @PostMapping de roles)
    // ==========================================

    @GetMapping("/roles")
    public ResponseEntity<java.util.List<RolResponse>> listarRoles() {
        return ResponseEntity.ok(clienteService.listarRoles());
    }

    // ==========================================
    // ENDPOINTS: PERFILES (Agrega esto debajo del @PostMapping de perfiles)
    // ==========================================
    @GetMapping("/usuarios/{usuarioId}/perfil")
    public ResponseEntity<PerfilResponse> obtenerPerfil(@PathVariable UUID usuarioId) {
        PerfilResponse response = clienteService.obtenerPerfilPorUsuarioId(usuarioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuarios/{id}/completo")
    public ResponseEntity<UsuarioCompletoResponse> obtenerUsuarioCompleto(@PathVariable UUID id) {
        UsuarioCompletoResponse response = clienteService.obtenerUsuarioCompleto(id);
        return ResponseEntity.ok(response);
    }
}
