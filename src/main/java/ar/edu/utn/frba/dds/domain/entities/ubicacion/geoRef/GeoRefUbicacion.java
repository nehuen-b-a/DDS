package ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef;

import lombok.Data;

@Data
public class GeoRefUbicacion {
  public Ubicacion ubicacion;

  @Data
  public class Ubicacion {
    public String municipio_nombre;
    public String provincia_nombre;
  }
}

