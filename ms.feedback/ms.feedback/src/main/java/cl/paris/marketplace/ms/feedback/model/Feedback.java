package cl.paris.marketplace.ms.feedback.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "feedbacks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId; // Cliente que compra y evalúa

    @Column(name = "producto_id")
    private UUID productoId; // Opcional (por si evalúa solo al vendedor)

    @Column(name = "vendedor_id")
    private UUID vendedorId; // Opcional (por si evalúa solo al producto o ambos)

    @Column(nullable = false)
    private Integer calificacion; // 1 a 5 estrellas

    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}