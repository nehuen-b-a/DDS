package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;

import java.util.ArrayList;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity @Table(name="pregunta")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="tipo")
public abstract class Pregunta {
  @Id
  @GeneratedValue
  private long id;
  @Column(name = "campo", nullable = false)
  private String campo;
  @Column(name = "activa", nullable = false)
  private boolean activa;
  public abstract boolean esValida(String respuesta);
}
