package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Setter @Getter
@Entity
@DiscriminatorValue("FALTAN_N_VIANDAS")
public class FaltanNViandas extends Suscripcion {
  @Column(name="cantidadViandasFaltantes")
  private int cantidadViandasParaLlenarse;

  protected boolean cumpleCondicion(Heladera heladera) {
    return heladera.cantidadViandasVirtuales() + cantidadViandasParaLlenarse == heladera.getCapacidadMaximaViandas();
  }

  protected String armarCuerpo(Heladera heladera) {
    return "Faltan " + cantidadViandasParaLlenarse
        + " viandas para que la heladera "
        + heladera.getNombre() + " este llena.";
  }
}
