package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@DiscriminatorValue("abierta")

public class PreguntaAbierta extends Pregunta {
  @Override
  public boolean esValida(String respuesta) {
    return true;
  }
}
