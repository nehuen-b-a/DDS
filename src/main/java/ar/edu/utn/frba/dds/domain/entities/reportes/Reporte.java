package ar.edu.utn.frba.dds.domain.entities.reportes;

import java.time.LocalDate;
import java.util.List;

public interface Reporte {
  List<String> generarReporte(LocalDate fechaInicio, LocalDate fechaFin);

  String titulo();

  String nombreArchivo();
}
