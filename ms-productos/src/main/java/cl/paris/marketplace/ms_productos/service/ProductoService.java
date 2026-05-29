package cl.paris.marketplace.ms_productos.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // Inyección por constructor (Garantiza que Spring cargue todos los componentes)
    public ProductoService(ProductoRepository productoRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoMapper productoMapper) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoMapper = productoMapper;
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

        // 2. Buscar la Categoría en la base de datos
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new RuntimeException("La Categoría especificada no existe."));

        // 3. Transformar Record a Entidad usando el Mapper
        Producto producto = productoMapper.toProductoEntity(request, categoria);

        // 4. Guardar en la Base de Datos y responder con el Record plano seguro
        Producto productoGuardado = productoRepository.save(producto);
        return productoMapper.toProductoResponse(productoGuardado);
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

        int nuevoStock = producto.getStock() + cantidad;
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