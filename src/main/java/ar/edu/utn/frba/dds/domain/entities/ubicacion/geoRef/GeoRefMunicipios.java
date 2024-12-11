package ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef;

import java.util.List;
import lombok.Getter;

@Getter
public class GeoRefMunicipios {
  public List<Municipio> municipios;

  @Getter
  public class Municipio {
    public String nombre;
  }
}
