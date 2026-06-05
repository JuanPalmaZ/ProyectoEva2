package cl.paris.marketplace.ms.legacy.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "legacy_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LegacyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo_antiguo", nullable = false)
    private String codigoAntiguo; // Código SKU o ID que venía del ERP viejo

    @Column(name = "tipo_entidad", nullable = false)
    private String tipoEntidad; // PRODUCTO, CLIENTE, STOCK

    @Column(name = "datos_migrados", nullable = false, length = 3000)
    private String datosMigrados; // Estructura JSON simulada del sistema viejo

    @Column(nullable = false)
    private String estado; // PROCESADO, ERROR, PENDIENTE

    @Column(name = "fecha_sincronizacion", nullable = false)
    private LocalDateTime fechaSincronizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaSincronizacion = LocalDateTime.now();
        this.estado = "PROCESADO"; // Al pasar por la API se consolida como procesado
    }
}