package ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef;

import java.util.List;

public class GeoRefDirecciones {
  public int cantidad;
  public List<GeoRefDireccion> direcciones;

  public class GeoRefDireccion {
    public GeoRefUbicacion ubicacion;
    public String nomenclatura;
  }

  public class GeoRefUbicacion {
    public String lat;
    public String lon;
  }
}
