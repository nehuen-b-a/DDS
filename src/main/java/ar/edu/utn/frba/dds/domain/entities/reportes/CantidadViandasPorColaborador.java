package ar.edu.utn.frba.dds.domain.entities.reportes;

import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.utils.manejos.ManejoFechas;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class CantidadViandasPorColaborador implements Reporte {
  @Getter @Setter
  private RepositorioPersonaHumana repositorioColaboradores;

  public List<String> generarReporte(LocalDate fechaInicio, LocalDate fechaFin) {
    List<String> parrafos = new ArrayList<>();
    int cantidadViandas;
    String parrafo;

    for (PersonaHumana persona : repositorioColaboradores.buscarTodos(PersonaHumana.class)) {
      cantidadViandas = cantidadViandasDonadadas(persona, fechaInicio, fechaFin);
      parrafo = "Cantidad de viandas donadas de "
          + persona.getNombre() + " "
          + persona.getApellido()
          + " (Id: " + persona.getId()
          + "): " + cantidadViandas;
      parrafos.add(parrafo);
    }

    return parrafos;
  }

  private int cantidadViandasDonadadas(PersonaHumana persona, LocalDate fechaInicio, LocalDate fechaFin) {
    return (int) persona.getContribuciones().stream().filter(con ->
        con.obtenerTipoContribucion() == TipoContribucion.DONACION_VIANDA
            && ManejoFechas.fechaEnRango(con.obtenerFechaRegistro(), fechaInicio, fechaFin)
    ).count();
  }

  public String titulo() {
    return "Cantidad de viandas por colaborador";
  }

  public String nombreArchivo() { return "cantidad-viandas-colaborador"; }
}
