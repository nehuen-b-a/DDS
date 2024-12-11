package ar.edu.utn.frba.dds.domain.adapters;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Coordenada;
import java.io.IOException;
import java.util.List;

public interface AdapterRecomendacionPuntosHeladera {
  public List<Coordenada> recomendacion(String latitud, String longitud, float radio) throws IOException;
}
