package ar.edu.utn.frba.dds.utils.manejos;

import java.time.LocalDate;

public class ManejoFechas {
  public static boolean fechaEnRango(LocalDate fecha, LocalDate fechaInicio, LocalDate fechaFin) {
    return (fecha.isEqual(fechaInicio) || fecha.isAfter(fechaInicio))
        && (fecha.isEqual(fechaFin) || fecha.isBefore(fechaFin));
  }
}