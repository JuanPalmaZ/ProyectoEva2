package cl.paris.marketplace.ms_proveedores.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "documento")

public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id",nullable=false)
    private Proveedor proveedor;
    @Column(name = "tipo_documento", nullable = false, length = 50) // Ej: "CERTIFICADO_CALIDAD", "CONTRATO"
    private String tipoDocumento;
    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

}
