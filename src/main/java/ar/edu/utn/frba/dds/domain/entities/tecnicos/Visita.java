package ar.edu.utn.frba.dds.domain.entities.tecnicos;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="visita")
public class Visita {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="descripcion", nullable = false)
    private String descripcion;

    @Column(name="foto_ruta")
    private String foto;
}
