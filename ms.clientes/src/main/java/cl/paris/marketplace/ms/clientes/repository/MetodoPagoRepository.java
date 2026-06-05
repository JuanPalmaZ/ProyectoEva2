package cl.paris.marketplace.ms.clientes.repository;


import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.paris.marketplace.ms.clientes.model.MetodoPago;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, UUID> {
    // Query Method para listar todas las tarjetas guardadas de un cliente específico
    List<MetodoPago> findByUsuarioId(UUID usuarioId);

    // Custom Query para buscar métodos de pago filtrados por tipo (CREDITO/DEBITO) de un usuario
@Query("SELECT m FROM MetodoPago m WHERE m.usuarioId = :usuarioId AND m.tipo = :tipo")
List<MetodoPago> encontrarPorUsuarioYTipo(@Param("usuarioId") UUID usuarioId, @Param("tipo") String tipo);
}
