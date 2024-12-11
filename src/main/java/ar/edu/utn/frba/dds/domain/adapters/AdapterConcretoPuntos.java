package ar.edu.utn.frba.dds.domain.adapters;

import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.RecomendacionPuntosServicio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Coordenada;
import java.io.IOException;
import java.util.List;
import lombok.Setter;

@Setter
public class AdapterConcretoPuntos implements AdapterRecomendacionPuntosHeladera {

  private RecomendacionPuntosServicio recomendacionPuntosServicio;

  @Override
  public List<Coordenada> recomendacion(String latitud, String longitud, float radio) throws IOException {
    return recomendacionPuntosServicio.listadoPuntosRecomendados(radio, latitud, longitud).puntos;
  }
}
