package ar.edu.utn.frba.dds.domain.entities.personasJuridicas;

import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Getter @Setter
@Entity @Table(name="rubro")
@NoArgsConstructor
public class Rubro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="nombre")
    private String nombre;
    public Rubro(String nombre) {
        this.nombre = nombre;
    }
}
