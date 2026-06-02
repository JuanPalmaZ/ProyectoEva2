package cl.paris.marketplace.ms.venta.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.venta.model.DetalleVenta;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, UUID> {
    
    // Custom Query: Busca todos los productos vendidos por un proveedor específico
    List<DetalleVenta> findByProveedorId(UUID proveedorId);
}