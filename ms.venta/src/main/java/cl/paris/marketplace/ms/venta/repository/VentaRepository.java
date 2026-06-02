package cl.paris.marketplace.ms.venta.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.venta.model.Venta;

@Repository
public interface VentaRepository extends JpaRepository<Venta, UUID> {

    // Busca todas las ventas de un cliente específico
    List<Venta> findByClienteId(UUID clienteId);

    // Busca ventas dentro de un rango de fechas
    List<Venta> findByFechaCompraBetween(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );
}