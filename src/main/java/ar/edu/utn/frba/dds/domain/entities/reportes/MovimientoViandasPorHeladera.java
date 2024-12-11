package ar.edu.utn.frba.dds.domain.entities.reportes;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.AccionApertura;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.utils.manejos.ManejoFechas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class MovimientoViandasPorHeladera implements Reporte {
  @Getter @Setter
  private RepositorioHeladera repositorioHeladera;
  public List<String> generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
    List<String> parrafos = new ArrayList<>();
    for (Heladera heladera : this.repositorioHeladera.buscarTodos(Heladera.class)) {
      int cantidadViandasRetiradas = cantidadViandasSegunAccion(heladera, AccionApertura.QUITAR_VIANDA, fechaInicio, fechaFin);
      int cantidadViandasColocadas = cantidadViandasSegunAccion(heladera, AccionApertura.INGRESAR_VIANDA, fechaInicio, fechaFin);
      String parrafo = heladera.getNombre() + " (Id: " + heladera.getId() + "): "
          + "\n   - Viandas retiradas: " + cantidadViandasRetiradas + "."
          + "\n   - Viandas colocadas: " + cantidadViandasColocadas + ".";
      parrafos.add(parrafo);
      parrafos.add("\n");
    }

    return parrafos;
  }

  private int cantidadViandasSegunAccion(Heladera heladera, AccionApertura accion, LocalDate fechaInicio, LocalDate fechaFin) {
    // como para meter o sacar cosas en la heladera primero hay que solicitarlas
    return heladera.getSolicitudesDeApertura().stream().filter(sol ->
        sol.isAperturaConcretada() && sol.getAccion().equals(accion)
        && ManejoFechas.fechaEnRango(sol.getFechaConcrecion().toLocalDate(), fechaInicio, fechaFin)
    ).mapToInt(sol -> sol.getCantidadViandas()).sum();
  }

  public String titulo() {
    return "Movimientos de viandas por heladera";
  }

  public String nombreArchivo() { return "cantidad-viandas-heladera"; }
}
