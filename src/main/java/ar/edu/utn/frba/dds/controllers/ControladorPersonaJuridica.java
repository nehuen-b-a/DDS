package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.Rubro;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaJuridica;
import ar.edu.utn.frba.dds.dtos.PersonaJuridicaDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ControladorPersonaJuridica implements ICrudViewsHandler, WithSimplePersistenceUnit {

  private RepositorioPersonaJuridica repositorioPersonaJuridica;
  private final String rutaPersonaJuridicaHbs = "cuenta/formularioPersonaJuridica.hbs";
  private final String rutaPersonaJuridica = "/personaJuridica";
  private final String rutaPantallaPrincipal = "/";
  private final String ERROR = "error";

  public ControladorPersonaJuridica(RepositorioPersonaJuridica repositorioPersonaJuridica) {
    this.repositorioPersonaJuridica = repositorioPersonaJuridica;
  }

  @Override
  public void index(Context context) {
    // There's nothing here
  }

  @Override
  public void show(Context context) {
    // There's nothing here, as well
  }

  @Override
  public void create(Context context) {
    Map<String, Object> model = new HashMap<>();
    model.put("rubros", this.repositorioPersonaJuridica.buscarTodos(Rubro.class));
    context.render(rutaPersonaJuridicaHbs, model);
  }

  @Override
  public void save(Context context) {
    PersonaJuridicaDTO dto = new PersonaJuridicaDTO();
    dto.obtenerFormulario(context);

    try {
      PersonaJuridica nuevaPersona = PersonaJuridica.fromDTO(dto);
      if (nuevaPersona == null) {
        throw new ValidacionFormularioException("Se han ingresado datos incorrectos.");
      }

      Optional<Rubro> rubro = this.repositorioPersonaJuridica.buscarPorId(Long.parseLong(dto.getRubro()), Rubro.class);
      if (rubro.isEmpty()) {
        throw new ValidacionFormularioException("Se han ingresado datos incorrectos.");
      }
      nuevaPersona.setRubro(rubro.get());

      Long id = context.sessionAttribute("id");
      Usuario usuario = this.repositorioPersonaJuridica.buscarPorId(id, Usuario.class).orElseThrow(() ->
          new ValidacionFormularioException("No se ha encontrado tu usuario.")
      );
      nuevaPersona.setUsuario(usuario);

      withTransaction(() -> repositorioPersonaJuridica.guardar(nuevaPersona));
      context.redirect(rutaPantallaPrincipal);
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put(ERROR, e.getMessage());
      model.put("dto", dto);
      model.put("rubros", this.repositorioPersonaJuridica.buscarTodos(Rubro.class));
      context.render(rutaPersonaJuridicaHbs, model);
    }
  }

  @Override
  public void edit(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<PersonaJuridica> persona = repositorioPersonaJuridica.buscarPorUsuario(context.sessionAttribute("id"));

      if (persona.isEmpty()) {
        throw new ValidacionFormularioException("No existe la persona jurídica.");
      }

      PersonaJuridicaDTO dto = new PersonaJuridicaDTO(persona.get());
      model.put("dto", dto);
      model.put("edicion", true);
      model.put("editado", false);
      model.put("mostrarPersonaJuridica", true);
      model.put("mostrarIrAtras", true);
      model.put("paginaAtras", "/");
      model.put("rubros", this.repositorioPersonaJuridica.buscarTodos(Rubro.class));
      model.put("id", context.sessionAttribute("id"));
      context.render(rutaPersonaJuridicaHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put(ERROR, e.getMessage());
      model.put("edicion", true);
      model.put("editado", false);
      model.put("mostrarPersonaJuridica", true);
      model.put("mostrarIrAtras", true);
      model.put("paginaAtras", "/");
      model.put("rubros", this.repositorioPersonaJuridica.buscarTodos(Rubro.class));
      model.put("id", context.sessionAttribute("id"));
      context.render(rutaPersonaJuridicaHbs, model);
    }
  }

  @Override
  public void update(Context context) {
    Map<String, Object> model = new HashMap<>();
    PersonaJuridicaDTO dtoNuevo = new PersonaJuridicaDTO();
    dtoNuevo.obtenerFormulario(context);

    try {
      Optional<PersonaJuridica> personaExistente = repositorioPersonaJuridica.buscarPorUsuario(context.sessionAttribute("id"));

      if (personaExistente.isEmpty()) {
        throw new ValidacionFormularioException("Persona jurídica no encontrada.");
      }
      dtoNuevo.setId(personaExistente.get().getId());

      PersonaJuridicaDTO dtoExistente = new PersonaJuridicaDTO(personaExistente.get());
      if (dtoExistente.equals(dtoNuevo)) {
        throw new ValidacionFormularioException("No se detectaron cambios en el formulario.");
      }

      personaExistente.get().actualizarFromDto(dtoNuevo);
      withTransaction(() -> repositorioPersonaJuridica.actualizar(personaExistente.get()));
      context.redirect(rutaPantallaPrincipal);
    } catch (ValidacionFormularioException e) {
      model.put(ERROR, e.getMessage());
      model.put("dto", dtoNuevo);
      model.put("edicion", true);
      model.put("editado", true);
      model.put("mostrarPersonaJuridica", true);
      model.put("mostrarIrAtras", true);
      model.put("paginaAtras", "/");
      model.put("rubros", this.repositorioPersonaJuridica.buscarTodos(Rubro.class));
      model.put("id", context.sessionAttribute("id"));
      context.render(rutaPersonaJuridicaHbs, model);
    }
  }

  @Override
  public void delete(Context context) {
    Long id = context.sessionAttribute("id");
    Optional<PersonaJuridica> persona = repositorioPersonaJuridica.buscarPorUsuario(id);

    if (persona.isPresent()) {
      withTransaction(() -> repositorioPersonaJuridica.eliminarFisico(PersonaJuridica.class, id));
      context.redirect(rutaPersonaJuridica);
    } else {
      context.status(400).result("No se pudo eliminar la cuenta, reintente más tarde.");
    }
  }
}
