package cl.paris.marketplace.ms_productos.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // <-- Importación agregada
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; 
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.paris.marketplace.ms_productos.dto.CategoriaRequest;
import cl.paris.marketplace.ms_productos.dto.CategoriaResponse;
import cl.paris.marketplace.ms_productos.dto.ProductoRequest;
import cl.paris.marketplace.ms_productos.dto.ProductoResponse;
import cl.paris.marketplace.ms_productos.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // ==========================================
    // CRUD: PRODUCTOS
    // ==========================================

    // 1. AGREGAR PRODUCTO (Extrae el ID del token)
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductoResponse> registrarProducto(
            @Valid @RequestBody ProductoRequest request,
            Authentication authentication // <-- Interceptamos
    ) {
        UUID usuarioId = UUID.fromString(authentication.getCredentials().toString());
        ProductoResponse response = productoService.registrarProducto(request, usuarioId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 2. MODIFICAR PRODUCTO (Extrae el ID del token)
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> modificarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ProductoRequest request,
            Authentication authentication // <-- Interceptamos
    ) {
        UUID usuarioId = UUID.fromString(authentication.getCredentials().toString());
        ProductoResponse response = productoService.modificarProducto(id, request, usuarioId);
        return ResponseEntity.ok(response);
    }

    // 3. ELIMINAR PRODUCTO (Borrado lógico)
    @PreAuthorize("hasRole('PROVEEDOR') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoLogico(@PathVariable UUID id) {
        productoService.eliminarProductoLogico(id);
        return ResponseEntity.noContent().build();
    }

    // 4. ACTUALIZAR STOCK 
    @PreAuthorize("hasAnyRole('PROVEEDOR', 'ADMIN', 'CLIENTE')")
    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductoResponse> actualizarStock(
            @PathVariable UUID id,
            @RequestParam Integer cantidad) {
        ProductoResponse response = productoService.actualizarStock(id, cantidad);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: CONSULTAS (PÚBLICAS / AUTENTICADAS)
    // ==========================================

    @PreAuthorize("hasAnyRole('CLIENTE', 'PROVEEDOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtenerProductoPorId(@PathVariable UUID id) {
        ProductoResponse response = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'PROVEEDOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductosActivos() {
        List<ProductoResponse> response = productoService.listarProductosActivos();
        return ResponseEntity.ok(response);
    }

    // CERRADURA ANTI-IDOR APLICADA AQUÍ: Busca por el usuarioId para sacar su catálogo de productos
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PROVEEDOR') and #usuarioId.toString() == authentication.credentials)")
    @GetMapping("/proveedor/usuario/{usuarioId}")
    public ResponseEntity<List<ProductoResponse>> listarProductosPorProveedor(@PathVariable UUID usuarioId) {
        List<ProductoResponse> response = productoService.listarProductosPorProveedor(usuarioId);
        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ENDPOINTS: CATEGORÍAS
    // ==========================================

    @PreAuthorize("hasAnyRole('CLIENTE', 'PROVEEDOR', 'ADMIN')")
    @PostMapping("/categorias")
    public ResponseEntity<CategoriaResponse> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = productoService.crearCategoria(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('CLIENTE', 'PROVEEDOR', 'ADMIN')")
    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        List<CategoriaResponse> response = productoService.listarCategorias();
        return ResponseEntity.ok(response);
    }
}