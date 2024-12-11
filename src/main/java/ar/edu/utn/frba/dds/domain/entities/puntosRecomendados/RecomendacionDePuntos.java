package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados;

import ar.edu.utn.frba.dds.domain.adapters.AdapterRecomendacionPuntosHeladera;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Coordenada;
import java.io.IOException;
import java.util.List;
import lombok.Setter;

@Setter
public class RecomendacionDePuntos {
  private AdapterRecomendacionPuntosHeladera adapterPuntos;

  public List<Coordenada> recomendacion(String latitud, String longitud, float radio) throws IOException {
    return adapterPuntos.recomendacion(latitud, longitud, radio);
  }
}
