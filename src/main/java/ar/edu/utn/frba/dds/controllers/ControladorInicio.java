package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

public class ControladorInicio {
  private static final Map<String, String> RUTAS = new HashMap<>();

  static {
    RUTAS.put(TipoRol.PERSONA_HUMANA.name(), "pantallaInicioHumana.hbs");
    RUTAS.put(TipoRol.PERSONA_JURIDICA.name(), "pantallaInicioJuridica.hbs");
    RUTAS.put(TipoRol.ADMIN.name(), "admin/adminInicio.hbs");
  }

  public void create(Context context) {
    String rol = context.sessionAttribute("rol");
    Long id = context.sessionAttribute("id");

    // Si no tiene rol o id, redirige a la pantalla de inicio común
    String rutaHbs = (rol == null || id == null) ? "pantallaInicio.hbs" : RUTAS.getOrDefault(rol, "pantallaInicio.hbs");
    context.render(rutaHbs, Map.of("id", id == null ? "" : id));
  }
}
