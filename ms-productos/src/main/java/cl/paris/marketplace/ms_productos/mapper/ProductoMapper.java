package cl.paris.marketplace.ms_productos.mapper;

import java.util.UUID; // Importante agregar esto

import org.springframework.stereotype.Component;

import cl.paris.marketplace.ms_productos.dto.CategoriaResponse;
import cl.paris.marketplace.ms_productos.dto.ProductoRequest;
import cl.paris.marketplace.ms_productos.dto.ProductoResponse;
import cl.paris.marketplace.ms_productos.model.Categoria;
import cl.paris.marketplace.ms_productos.model.Producto;

@Component
public class ProductoMapper {

    // ==========================================
    // MAPPERS PARA PRODUCTO
    // ==========================================

    // Ahora recibe el proveedorId inyectado desde el Service
    public Producto toProductoEntity(ProductoRequest request, Categoria categoria, UUID proveedorId) {
        if (request == null) return null;
        
        Producto producto = new Producto();
        producto.setSku(request.sku());
        producto.setNombre(request.nombre());
        producto.setDescripcion(request.descripcion());
        producto.setPrecio(request.precio());
        producto.setStock(request.stock());
        producto.setProveedorId(proveedorId); // Se asigna el ID seguro
        producto.setCategoria(categoria);
        
        return producto;
    }

    public ProductoResponse toProductoResponse(Producto producto) {
        if (producto == null) return null;

        return new ProductoResponse(
                producto.getId(),
                producto.getSku(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getProveedorId(),
                producto.getCategoria().getId(),          
                producto.getCategoria().getNombre(),      
                producto.getCategoria().getDescripcion(), 
                producto.getActivo(),
                producto.getFechaCreacion()
        );
    }

    // ==========================================
    // MAPPERS PARA CATEGORÍA
    // ==========================================

    public CategoriaResponse toCategoriaResponse(Categoria categoria) {
        if (categoria == null) return null;
        
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescripcion()
        );
    }
}