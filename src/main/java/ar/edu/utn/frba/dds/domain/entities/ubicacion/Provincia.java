package ar.edu.utn.frba.dds.domain.entities.ubicacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @Table(name = "provincia")
@NoArgsConstructor
public class Provincia {
  @Id @GeneratedValue
  private Long id;

  @Column(name = "provincia")
  private String provincia;

  public Provincia(String nombre) {
    this.provincia = nombre;
  }
}