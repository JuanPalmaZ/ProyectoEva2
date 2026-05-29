package cl.paris.marketplace.ms.usuarios.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "perfil")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    private Usuario usuario;
    @Column(name = "primer_nombre", nullable = false, length = 20)
    private String primerNombre;
    @Column(name = "segundo_nombre", length = 20)
    private String segundoNombre;
    @Column(name = "primer_apellido", nullable = false, length = 20)
    private String primerApellido;
    @Column(name = "segundo_apellido", length = 20)
    private String segundoApellido;
    @Column(name = "telefono", length = 20)
    private String telefono;
}
