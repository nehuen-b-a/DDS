package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoCuentaRegistro;
import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import io.javalin.http.Context;

public class ControladorEleccionTipoCuenta {
  private final String rutaHbs = "/cuenta/eleccionTipoDeCuenta.hbs";

  public void create(Context context) {
    context.render(rutaHbs);
  }

  public void save(Context context) {
    String tipoCuenta = context.formParam("tipoCuenta");
    try {
      validarTipoCuenta(tipoCuenta);
      context.sessionAttribute("tipoCuenta", tipoCuenta);
      context.redirect("/usuarios/nuevo");
    } catch (IllegalArgumentException e) {
      throw new MensajeAmigableException("Se ha indicado un tipo de cuenta inválido.", 400);
    }
  }

  private void validarTipoCuenta(String tipoCuenta) {
    if (tipoCuenta == null
        || !(tipoCuenta.equals(TipoCuentaRegistro.PERSONA_HUMANA.name())
        || tipoCuenta.equals(TipoCuentaRegistro.PERSONA_JURIDICA.name()))) {
      throw new IllegalArgumentException();
    }
  }
}
