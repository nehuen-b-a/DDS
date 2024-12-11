package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioUsuario;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class ControladorInicioSesion {
  private RepositorioUsuario repositorioUsuario;
  private final String rutaInicioSesion = "/cuenta/inicioSesion.hbs";

  public ControladorInicioSesion(RepositorioUsuario repositorioUsuario) {
    this.repositorioUsuario = repositorioUsuario;
  }

  public void create(Context context) {
    context.render(rutaInicioSesion);
  }

  public void iniciarSesion(Context context) {
    String nombreUsuario = context.formParam("usuario");
    String clave = context.formParam("clave");

    try {
      // Validar que los campos requeridos no estén vacíos
      CamposObligatoriosVacios.validarCampos(
          Pair.of("nombreUsuario", nombreUsuario),
          Pair.of("clave", clave)
      );

      // Buscar el usuario en el repositorio
      Optional<Usuario> usuarioOpt = repositorioUsuario.buscarPorNombre(nombreUsuario);

      Usuario usuario = usuarioOpt.orElseThrow(() ->
          new ValidacionFormularioException("No existe un usuario con ese nombre.")
      );

      // Verificar si la clave ingresada es correcta
      if (!usuario.verificarClave(clave)) {
        throw new ValidacionFormularioException("La clave ingresada es inválida.");
      }

      // Si es válido, crear la sesión del usuario
      context.sessionAttribute("id", usuario.getId());
      context.sessionAttribute("rol", usuario.getRol().getTipoRol().name());
      context.redirect("/");
    } catch (ValidacionFormularioException e) {
      // En caso de error, renderizar la página con el mensaje de error
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("nombre", nombreUsuario);
      model.put("clave", clave);
      context.render(rutaInicioSesion, model);
    }
  }

  public void cerrarSesion(Context context) {
    context.req().getSession().invalidate();
    context.redirect("/");
  }
}
