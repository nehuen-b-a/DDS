package ar.edu.utn.frba.dds.domain.entities.usuarios;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "permiso")
@NoArgsConstructor
public class Permiso {
  @Id
  @GeneratedValue
  private Long id;
  @Column(name="nombre")
  private String nombre;

  public Permiso(String nombre) {
    this.nombre = nombre;
  }
}
