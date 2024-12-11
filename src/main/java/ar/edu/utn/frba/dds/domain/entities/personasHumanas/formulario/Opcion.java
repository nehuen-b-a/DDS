package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "opcion")
public class Opcion {
  @Id
  @GeneratedValue
  private long id;
  @Column(name= "campo")
  private String campo;
}
