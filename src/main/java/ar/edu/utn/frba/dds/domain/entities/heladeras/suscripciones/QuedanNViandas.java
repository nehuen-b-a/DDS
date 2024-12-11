package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@DiscriminatorValue("QUEDAN_N_VIANDAS")
@NoArgsConstructor
public class QuedanNViandas extends Suscripcion {
  @Column(name="cantidadViandasQueQuedan")
  private int cantidadViandasDisponibles;

  protected boolean cumpleCondicion(Heladera heladera) {
    return heladera.cantidadViandasVirtuales() == cantidadViandasDisponibles;
  }

  protected String armarCuerpo(Heladera heladera) {
    return "Quedan únicamente " + cantidadViandasDisponibles
        + " viandas disponibles en la heladera "
        + heladera.getNombre() + ".";
  }
}
