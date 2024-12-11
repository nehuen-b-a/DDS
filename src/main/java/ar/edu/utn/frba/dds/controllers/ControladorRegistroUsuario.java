package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.usuarios.Rol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoCuentaRegistro;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioRol;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioUsuario;
import ar.edu.utn.frba.dds.dtos.UsuarioDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

public class ControladorRegistroUsuario implements WithSimplePersistenceUnit {
  private RepositorioUsuario repositorioUsuario;
  private RepositorioRol repositorioRol;
  private final String rutaHbs = "/cuenta/crearCuenta.hbs";

  public ControladorRegistroUsuario(RepositorioUsuario repositorioUsuario, RepositorioRol repositorioRol) {
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioRol = repositorioRol;
  }

  public void create(Context context) {
    String tipoCuenta = context.sessionAttribute("tipoCuenta");
    if (tipoCuenta == null) {
      throw new ValidacionFormularioException("No se ha indicado un tipo de cuenta.");
    }

    Map<String, Object> model = new HashMap<>();
    if (TipoCuentaRegistro.valueOf(tipoCuenta) == TipoCuentaRegistro.PERSONA_HUMANA) {
      model.put("personaHumana", true);
    }

    context.render(rutaHbs, model);
  }

  public void save(Context context) {
    UsuarioDTO dto = new UsuarioDTO();
    dto.obtenerFormulario(context);

    try {
      if (repositorioUsuario.existeUsuarioPorNombre(dto.getNombre())) {
        throw new ValidacionFormularioException("El nombre de usuario ya está en uso. Por favor, elige uno diferente.");
      }

      if (dto.getRol() == null || !(dto.getRol().equals(TipoRol.PERSONA_HUMANA.name())
          || dto.getRol().equals(TipoRol.PERSONA_JURIDICA.name()))) {
        throw new ValidacionFormularioException("No se ha indicado un tipo de cuenta o se indicó uno incorrecto. Vuelve a /tipoCuenta.");
      }

      Optional<Rol> rol = repositorioRol.buscarPorTipo(TipoRol.valueOf(dto.getRol()));
      if (rol.isEmpty()) {
        throw new ValidacionFormularioException("El rol indicado no existe. Por favor, elige uno diferente.");
      }

      Usuario usuario = Usuario.fromDTO(dto);
      usuario.setRol(rol.get());
      withTransaction(() -> repositorioUsuario.guardar(usuario));

      context.sessionAttribute("id", usuario.getId());
      context.sessionAttribute("rol", usuario.getRol().getTipoRol().name());
      if (usuario.getRol().getTipoRol().equals(TipoRol.PERSONA_HUMANA)) {
        context.redirect("/personaHumana/nuevo");
      } else {
        context.redirect("/personaJuridica/nuevo");
      }
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      context.render(rutaHbs, model);
    }
  }
}