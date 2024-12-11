package ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public class FallaTecnica implements TipoIncidente {
  @Override
  public String obtenerDescripcionIncidente(Incidente incidente) {
    String nombreColaborador = incidente.getColaborador().getNombre();
    String descripcionFalla = incidente.getDescripcionDelColaborador();

    return String.format("Se ha reportado una falla técnica por parte de %s. Descripción del incidente: %s.",
        nombreColaborador,
        descripcionFalla);
  }
}

