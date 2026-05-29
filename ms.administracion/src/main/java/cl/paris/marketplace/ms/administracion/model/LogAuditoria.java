package cl.paris.marketplace.ms.administracion.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "logs_auditoria_admin")
public class LogAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId; 

    @Column(name = "accion", nullable = false, length = 100)
    private String accion; // Ej: "BLOQUEAR_PROVEEDOR", "APROBAR_PRODUCTO"

    @Column(name = "detalle", nullable = false, columnDefinition = "TEXT")
    private String detalle; 

    @Column(name = "fecha_accion", nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    @PrePersist
    protected void onCreate() {
        this.fechaAccion = LocalDateTime.now();
    }
}