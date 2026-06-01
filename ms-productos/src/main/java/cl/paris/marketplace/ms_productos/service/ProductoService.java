package cl.paris.marketplace.ms_productos.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException; // Importación obligatoria para capturar el error si el ID no existe

import cl.paris.marketplace.ms_productos.client.ProveedorClient;
import cl.paris.marketplace.ms_productos.dto.CategoriaRequest;
import cl.paris.marketplace.ms_productos.dto.CategoriaResponse;
import cl.paris.marketplace.ms_productos.dto.ProductoRequest;
import cl.paris.marketplace.ms_productos.dto.ProductoResponse;
import cl.paris.marketplace.ms_productos.mapper.ProductoMapper;
import cl.paris.marketplace.ms_productos.model.Categoria;
import cl.paris.marketplace.ms_productos.model.Producto;
import cl.paris.marketplace.ms_productos.repository.CategoriaRepository;
import cl.paris.marketplace.ms_productos.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final ProveedorClient proveedorClient;

    // Inyección por constructor actualizada con el cliente Feign
    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoMapper productoMapper,
                           ProveedorClient proveedorClient) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
        this.proveedorClient = proveedorClient;
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: PRODUCTOS
    // ==========================================
    @Transactional
    public ProductoResponse registrarProducto(ProductoRequest request) {
        // 1. Validación de negocio: Evitar duplicados por SKU
        if (productoRepository.findBySku(request.sku()).isPresent()) {
            throw new RuntimeException("El SKU '" + request.sku() + "' ya se encuentra registrado en el Marketplace.");
        }

        // 2. Validar únicamente la existencia del ID del proveedor vía Feign
        try {
            proveedorClient.obtenerProveedorSimplificado(request.proveedorId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Error de validación: El ID de proveedor " + request.proveedorId() + " no existe.");
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación con el servicio de proveedores: " + e.getMessage());
        }

        // 3. Buscar la Categoría en la base de datos
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("La Categoría especificada no existe."));

        // 4. Transformar Record a Entidad usando el Mapper
        Producto producto = productoMapper.toProductoEntity(request, categoria);

        // 5. Guardar en la Base de Datos y responder con el Record plano seguro
        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toProductoResponse(productoGuardado);
    }

    // Método necesario para soportar el PUT del controlador
    @Transactional
    public ProductoResponse modificarProducto(UUID id, ProductoRequest request) {
        // 1. Buscar el producto existente
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para modificar."));

        // 2. Validar el SKU si es que se está intentando cambiar
        if (!producto.getSku().equals(request.sku()) && productoRepository.findBySku(request.sku()).isPresent()) {
            throw new RuntimeException("El SKU '" + request.sku() + "' ya se encuentra registrado por otro artículo.");
        }

        // 3. Validar la existencia del ID de proveedor destino
        try {
            proveedorClient.obtenerProveedorSimplificado(request.proveedorId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Error: El ID de proveedor " + request.proveedorId() + " no existe.");
        } catch (Exception e) {
            throw new RuntimeException("Error de comunicación interservicio: " + e.getMessage());
        }

        // 4. Buscar la Categoría elegida
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("La Categoría especificada no existe."));

        // 5. Actualizar los datos del producto
        producto.setSku(request.sku());
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setCategoria(categoria);
        producto.setProveedorId(request.proveedorId());

        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toProductoResponse(productoActualizado);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoPorId(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));
        return productoMapper.toProductoResponse(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosActivos() {
        return productoRepository.findByActivoTrue().stream()
                .map(productoMapper::toProductoResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarProductosPorProveedor(UUID proveedorId) {
        return productoRepository.findByProveedorId(proveedorId).stream()
                .map(productoMapper::toProductoResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductoResponse actualizarStock(UUID id, Integer cantidad) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar stock."));

        int nuevoStock = producto.getStock() + Math.toIntExact(cantidad);
        if (nuevoStock < 0) {
            throw new RuntimeException("Operación inválida: El stock no puede quedar en negativo. Stock actual: " + producto.getStock());
        }

        producto.setStock(nuevoStock);
        Producto productoActualizado = productoRepository.save(producto);
        return productoMapper.toProductoResponse(productoActualizado);
    }

    @Transactional
    public void eliminarProductoLogico(UUID id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado."));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    // ==========================================
    // LÓGICA DE NEGOCIO: CATEGORÍAS
    // ==========================================
    @Transactional
    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        // Validar si la categoría ya existe por nombre para no tener duplicados
        if (categoriaRepository.findByNombre(request.nombre()).isPresent()) {
            throw new RuntimeException("La categoría ya existe en el sistema.");
        }

        Categoria categoria = new Categoria();
        categoria.setNombre(request.nombre());
        categoria.setDescripcion(request.descripcion());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);
        return productoMapper.toCategoriaResponse(categoriaGuardada);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarCategorias() {
        return categoriaRepository.findAll().stream()
                .map(productoMapper::toCategoriaResponse)
                .collect(Collectors.toList());
    }
}