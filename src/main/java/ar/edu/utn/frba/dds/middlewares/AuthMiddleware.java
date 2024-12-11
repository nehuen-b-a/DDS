package ar.edu.utn.frba.dds.middlewares;

import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AuthMiddleware {
  public static void apply(Javalin app) {
    app.beforeMatched(ctx -> {
      var userRole = getUserRoleType(ctx);
      if (!ctx.routeRoles().isEmpty() && !ctx.routeRoles().contains(userRole)) {
        throw new MensajeAmigableException("No estas autorizado para acceder a este contenido.", 401);
      }
      if (userRole != null) {
        if (ctx.sessionAttribute("id") == null) {
          throw new MensajeAmigableException("No se encontraron los datos básicos de la sesión.", 401);
        }
      }
    });
  }

  private static TipoRol getUserRoleType(Context context) {
    return context.sessionAttribute("rol") != null
        ? TipoRol.valueOf(context.sessionAttribute("rol"))
        : null;
  }
}
