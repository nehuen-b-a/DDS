package ar.edu.utn.frba.dds.domain.entities;

import java.time.LocalDate;

public interface Contribucion {
  public float calcularPuntaje();

  public TipoContribucion obtenerTipoContribucion();

  public LocalDate obtenerFechaRegistro();
}
