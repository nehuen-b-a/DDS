package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioSuscripcion;
import com.google.gson.Gson;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorMapa {
  private RepositorioHeladera repositorioHeladera;
  private RepositorioSuscripcion repositorioSuscripcion;
  private Repositorio repositorioPersonaJuridica;
  private final String rutaListadoHbs = "heladeras/mapa.hbs";
  private final Gson gson = GsonFactory.createGson();

  public ControladorMapa(RepositorioHeladera repositorioHeladera, Repositorio repositorioPersonaJuridica, RepositorioSuscripcion repositorioSuscripcion) {
    this.repositorioHeladera = repositorioHeladera;
    this.repositorioPersonaJuridica = repositorioPersonaJuridica;
    this.repositorioSuscripcion = repositorioSuscripcion;
  }

  public void mapa(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      String filtro = context.queryParam("q");  // Obtener el parámetro de filtro de la query string
      List<Heladera> heladeras;
      String rol = context.sessionAttribute("rol");
      Long idUsuario = context.sessionAttribute("id"); // Asumiendo que tienes el id del usuario en la sesión

      // Determinar qué heladeras buscar según el rol y el filtro
      if (TipoRol.valueOf(rol).equals(TipoRol.PERSONA_HUMANA)) {
        heladeras = this.repositorioHeladera.buscarTodos(Heladera.class);
        model.put("mostrarPersonaHumana", true);
      } else {
        model.put("mostrarPersonaJuridica", true);
        heladeras = obtenerHeladerasPorFiltro(filtro, idUsuario);
      }

      String jsonHeladeras = gson.toJson(heladeras);
      model.put("jsonHeladeras", jsonHeladeras);
      model.put("heladeras", heladeras);
      model.put("buscadorMapa", true);

      context.render(rutaListadoHbs, model);
    } catch (Exception e) {
      e.printStackTrace();
      context.status(500).result("Error interno del servidor"); // FIXME
    }
  }

  private List<Heladera> obtenerHeladerasPorFiltro(String filtro, Long idUsuario) {
    if ("misHeladeras".equals(filtro)) {
      return this.repositorioHeladera.buscarPorUsuario(idUsuario);
    } else if ("heladerasConAlerta".equals(filtro)) {
      return this.repositorioHeladera.buscarHeladerasConAlertaPorUsuario(idUsuario);
    }
    // Si no hay filtro aplicable, devolver todas las heladeras
    return this.repositorioHeladera.buscarTodos(Heladera.class);
  }
}
