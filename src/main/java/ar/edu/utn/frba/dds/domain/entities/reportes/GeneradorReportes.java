package ar.edu.utn.frba.dds.domain.entities.reportes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class GeneradorReportes {
  @Getter
  private static List<Reporte> reportes;
  @Getter @Setter
  private IAdapterPDF adapterPDF;
  private static GeneradorReportes instancia = null;
  @Getter @Setter
  private Temporalidad temporalidad;

  public static GeneradorReportes getInstance() {
    if (instancia == null) {
      instancia = new GeneradorReportes();
      reportes = new ArrayList<>();
    }
    return instancia;
  }

  public void agregarReportes(Reporte ...reportes) {
    Collections.addAll(this.reportes, reportes);
  }

  public LocalDate calcularFechaInicio() {
    LocalDate ahora = LocalDate.now();
    return switch (this.temporalidad) {
      case DIARIO -> ahora.minusDays(1);
      case SEMANAL -> ahora.minusWeeks(1);
      case MENSUAL -> ahora.minusMonths(1);
      case ANUAL -> ahora.minusYears(1);
      default ->
          ahora; // En caso de que no se establezca ninguna temporalidad, usar la fecha actual.
    };
  }

  public void generarReportes() {
    LocalDate fechaInicio = calcularFechaInicio();
    LocalDate fechaFin = LocalDate.now();

    for (Reporte reporte : reportes) {
      List<String> parrafos = reporte.generarReporte(fechaInicio, fechaFin);
      adapterPDF.exportarPDF(reporte.titulo(), reporte.nombreArchivo(), parrafos, fechaInicio.toString());
    }
  }
}
