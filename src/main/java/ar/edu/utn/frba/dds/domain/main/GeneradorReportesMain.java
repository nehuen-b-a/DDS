package ar.edu.utn.frba.dds.domain.main;

import ar.edu.utn.frba.dds.domain.entities.reportes.AdapterPDFiText;
import ar.edu.utn.frba.dds.domain.entities.reportes.CantidadFallasPorHeladera;
import ar.edu.utn.frba.dds.domain.entities.reportes.CantidadViandasPorColaborador;
import ar.edu.utn.frba.dds.domain.entities.reportes.GeneradorReportes;
import ar.edu.utn.frba.dds.domain.entities.reportes.ITextPDF;
import ar.edu.utn.frba.dds.domain.entities.reportes.MovimientoViandasPorHeladera;
import ar.edu.utn.frba.dds.domain.entities.reportes.Temporalidad;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefMunicipios;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefProvincias;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefServicio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefUbicacion;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import java.io.IOException;

public class GeneradorReportesMain {
  public static void main(String[] args) throws IOException {
    // configuro generador de reportes
    String rutaReportes = "src/main/resources/reportes/";
    AdapterPDFiText adaptador = new AdapterPDFiText(new ITextPDF(rutaReportes));
    GeneradorReportes.getInstance().setAdapterPDF(adaptador);
    GeneradorReportes.getInstance().setTemporalidad(Temporalidad.SEMANAL);

    // configuro reportes
    CantidadFallasPorHeladera fallasHeladeras = new CantidadFallasPorHeladera();
    fallasHeladeras.setRepositorioHeladera(new RepositorioHeladera());

    CantidadViandasPorColaborador viandasPorColaborador = new CantidadViandasPorColaborador();
    viandasPorColaborador.setRepositorioColaboradores(new RepositorioPersonaHumana());

    MovimientoViandasPorHeladera movimientosHeladera = new MovimientoViandasPorHeladera();
    movimientosHeladera.setRepositorioHeladera(new RepositorioHeladera());

    GeneradorReportes.getInstance().agregarReportes(fallasHeladeras, viandasPorColaborador, movimientosHeladera);
    GeneradorReportes.getInstance().generarReportes();
  }
}
