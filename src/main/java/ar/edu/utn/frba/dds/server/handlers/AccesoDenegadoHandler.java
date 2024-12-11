package ar.edu.utn.frba.dds.server.handlers;

import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import io.javalin.Javalin;
import java.util.HashMap;
import java.util.Map;

public class AccesoDenegadoHandler implements IHandler {

  @Override
  public void setHandle(Javalin app) {
    app.exception(MensajeAmigableException.class, (e, context) -> {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      context.status(e.getStatusCode());
      context.render("error.hbs", model);
    });
  }
}