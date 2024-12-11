package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.reportes.GeneradorReportes;
import ar.edu.utn.frba.dds.domain.entities.reportes.IAdapterPDF;
import ar.edu.utn.frba.dds.domain.entities.reportes.Reporte;
import ar.edu.utn.frba.dds.domain.entities.reportes.Temporalidad;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class ReportesTest {/*
  private static GeneradorReportes generador;
  private static IAdapterPDF mockAdapterPDF;
  private static Reporte mockReporte;

  @BeforeAll
  public static void antesDeTestear() {
    generador = GeneradorReportes.getInstance();
    mockAdapterPDF = Mockito.mock(IAdapterPDF.class);
    generador.setAdapterPDF(mockAdapterPDF);

    mockReporte = Mockito.mock(Reporte.class);
    when(mockReporte.titulo()).thenReturn("Reporte de prueba");
    List<String> contenido = new ArrayList<>();
    contenido.add("Contenido del reporte");
    when(mockReporte.generarReporte(any(LocalDate.class), any(LocalDate.class))).thenReturn(contenido);

    generador.agregarReportes(mockReporte);
  }

  @Test
  @DisplayName("La temporalidad de la generacion de reportes es parametrizable")
  void laTemporalidadEnElGeneradorDeReporesEsParametrizable() {
    generador.setTemporalidad(Temporalidad.DIARIO);
    LocalDate fechaEsperadaInicio = LocalDate.now().minusDays(1);
    Assertions.assertEquals(fechaEsperadaInicio, generador.calcularFechaInicio());

    generador.setTemporalidad(Temporalidad.SEMANAL);
    fechaEsperadaInicio = LocalDate.now().minusWeeks(1);
    Assertions.assertEquals(fechaEsperadaInicio, generador.calcularFechaInicio());

    generador.setTemporalidad(Temporalidad.MENSUAL);
    fechaEsperadaInicio = LocalDate.now().minusMonths(1);
    Assertions.assertEquals(fechaEsperadaInicio, generador.calcularFechaInicio());

    generador.setTemporalidad(Temporalidad.ANUAL);
    fechaEsperadaInicio = LocalDate.now().minusYears(1);
    Assertions.assertEquals(fechaEsperadaInicio, generador.calcularFechaInicio());
  }

  @Test
  @DisplayName("La generacion y exportacion del contenido de un reporte semanal en PDF es correcta")
  void verificarGeneracionYExportacionDeReportesEnPDF() {
    generador.setTemporalidad(Temporalidad.SEMANAL);
    generador.generarReportes();

    ArgumentCaptor<LocalDate> fechaInicioCaptor = ArgumentCaptor.forClass(LocalDate.class);
    ArgumentCaptor<LocalDate> fechaFinCaptor = ArgumentCaptor.forClass(LocalDate.class);

    List<String> contenido = new ArrayList<>();
    contenido.add("Contenido del reporte");
    verify(mockAdapterPDF, times(1)).exportarPDF(eq("Reporte de prueba"), eq("nombre-archivo"), eq(contenido), "2024-09-01");
    verify(mockReporte, times(1)).generarReporte(fechaInicioCaptor.capture(), fechaFinCaptor.capture());

    LocalDate fechaInicioEsperada = LocalDate.now().minusWeeks(1);
    LocalDate fechaFinEsperada = LocalDate.now();

    Assertions.assertEquals(fechaInicioEsperada, fechaInicioCaptor.getValue());
    Assertions.assertEquals(fechaFinEsperada, fechaFinCaptor.getValue());
  }*/
}
