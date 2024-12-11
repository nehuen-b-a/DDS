package ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef;

import java.util.List;
import lombok.Getter;

@Getter
public class GeoRefProvincias {
  public List<Provincia> provincias;

  @Getter
  public class Provincia {
    public String nombre;
  }
}
