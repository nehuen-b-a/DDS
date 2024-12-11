package ar.edu.utn.frba.dds.domain.entities.ubicacion;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "municipio")
@NoArgsConstructor
public class Municipio {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "municipio")
  private String municipio;

  @ManyToOne
  @JoinColumn(name = "provincia_id", referencedColumnName = "id")
  private Provincia provincia;

  public Municipio(String municipio, Provincia provincia) {
    this.municipio = municipio;
    this.provincia = provincia;
  }
}