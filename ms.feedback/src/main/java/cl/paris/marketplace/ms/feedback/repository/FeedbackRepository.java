package cl.paris.marketplace.ms.feedback.repository;

import cl.paris.marketplace.ms.feedback.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    
    // Exponer reseñas de un producto
    List<Feedback> findByProductoIdOrderByFechaCreacionDesc(UUID productoId);

    // Exponer reseñas de un vendedor 
    List<Feedback> findByVendedorIdOrderByFechaCreacionDesc(UUID vendedorId);

    // Historial del cliente
    List<Feedback> findByClienteIdOrderByFechaCreacionDesc(UUID clienteId);
}