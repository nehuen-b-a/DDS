package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.personasVulnerables.PersonaVulnerable;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaVulnerable;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTarjetas;
import ar.edu.utn.frba.dds.dtos.PersonaVulnerableDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ControladorPersonaVulnerable implements ICrudViewsHandler, WithSimplePersistenceUnit {
  private RepositorioPersonaVulnerable repositorioPersonaVulnerable;
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private RepositorioTarjetas repositorioTarjetas;
  private final String rutaRegistroHbs = "/colaboraciones/registroPersonaVulnerable.hbs";
  private final String rutaListadoHbs = "colaboraciones/listadoPersonasVulnerables.hbs";
  private final String rutaSolicitudHbs = "/colaboraciones/solicitudTarjetas.hbs";
  private final Integer CANTIDAD_TARJETAS_SIN_ENTREGAR_MAXIMAS = 20;

  public ControladorPersonaVulnerable(RepositorioPersonaVulnerable repositorioPersonaVulnerable, RepositorioPersonaHumana repositorioPersonaHumana, RepositorioTarjetas repositorioTarjetas) {
    this.repositorioPersonaVulnerable = repositorioPersonaVulnerable;
    this.repositorioPersonaHumana = repositorioPersonaHumana;
    this.repositorioTarjetas = repositorioTarjetas;
  }

  @Override
  public void index(Context context) {
    Long id = context.sessionAttribute("id");

    Optional<List<PersonaVulnerable>> vulnerables = this.repositorioPersonaVulnerable.buscarPersonasDe(id);

    Map<String, Object> model = new HashMap<>();
    model.put("id", id);
    vulnerables.ifPresent(personaVulnerables -> model.put("personasVulnerables", personaVulnerables));

    context.render(rutaListadoHbs, model);
  }

  @Override
  public void show(Context context) {
    // En este caso, no tiene sentido hacer este metodo.
  }

  @Override
  public void create(Context context) {
    context.render(rutaRegistroHbs);
  }

  @Override
  public void save(Context context) {
    PersonaVulnerableDTO dto = new PersonaVulnerableDTO();
    dto.obtenerFormulario(context);
    Long id = context.sessionAttribute("id");

    try {
      Optional<PersonaHumana> registrador = repositorioPersonaHumana.buscarPorUsuario(id);
      if (registrador.isEmpty()) {
        throw new ValidacionFormularioException("No se ha encontrado la persona que lo está registrando. Reintentar.");
      }

      PersonaVulnerable nuevaPersona = PersonaVulnerable.fromDTO(dto);
      if (nuevaPersona == null) {
        throw new ValidacionFormularioException("Se ha ingresado información incorrecta sobre la persona vulnerable.");
      }

      nuevaPersona.setPersonaQueLoRegistro(registrador.get());
      nuevaPersona.setFechaDeRegistro(LocalDate.now());

      // asigno tarjeta sin entregar del registrador a la persona vulnerable
      Tarjeta tarjeta = asignarTarjeta(registrador.get());
      registrador.get().agregarTarjetaEntregada(tarjeta);
      nuevaPersona.agregarTarjeta(tarjeta);

      registrador.get().sumarPuntaje(tarjeta.calcularPuntaje());
      withTransaction(() -> {
        repositorioPersonaVulnerable.guardar(nuevaPersona);
        repositorioPersonaVulnerable.actualizar(tarjeta);
        repositorioPersonaHumana.actualizar(registrador.get());
      });

      context.redirect("/personasVulnerables");
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      model.put("id", id);
      context.render(rutaRegistroHbs, model);
    }
  }

  private Tarjeta asignarTarjeta(PersonaHumana registrador) throws ValidacionFormularioException {
    List<Tarjeta> tarjetas = repositorioTarjetas.buscarTarjetasDe(registrador.getId());
    if (tarjetas.isEmpty()) {
      throw new ValidacionFormularioException("No puede registrar personas vulnerables ya que no tiene tarjetas para entregar.");
    }
    Tarjeta tarjeta = registrador.getTarjetasSinEntregar().remove(0);
    tarjeta.setFechaRecepcionPersonaVulnerable(LocalDate.now());
    return tarjeta;
  }

  @Override
  public void edit(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<PersonaVulnerable> vulnerable = this.repositorioPersonaVulnerable.buscarPorId(Long.valueOf(context.pathParam("id")), PersonaVulnerable.class);

      if (vulnerable.isEmpty()) {
        throw new ValidacionFormularioException("No existe una persona vulnerable con este id.");
      }

      PersonaVulnerableDTO dto = new PersonaVulnerableDTO(vulnerable.get());
      model.put("dto", dto);
      model.put("edicion", true);
      model.put("id", context.pathParam("id"));
      context.render(rutaRegistroHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      context.render(rutaListadoHbs, model);
    }
  }

  @Override
  public void update(Context context) {
    Map<String, Object> model = new HashMap<>();
    PersonaVulnerableDTO dtoNuevo = new PersonaVulnerableDTO();
    dtoNuevo.obtenerFormulario(context);

    try {
      Optional<PersonaVulnerable> vulnerableExistente = repositorioPersonaVulnerable
          .buscarPorId(Long.valueOf(context.pathParam("id")), PersonaVulnerable.class);

      if (vulnerableExistente.isEmpty()) {
        throw new ValidacionFormularioException("Persona vulnerable no encontrada.");
      }

      PersonaVulnerableDTO dtoExistente = new PersonaVulnerableDTO(vulnerableExistente.get());
      if (dtoExistente.equals(dtoNuevo)) {
        throw new ValidacionFormularioException("No se detectaron cambios en el formulario.");
      }

      vulnerableExistente.get().actualizarFromDto(dtoNuevo);
      withTransaction(() -> repositorioPersonaVulnerable.actualizar(vulnerableExistente.get()));
      context.redirect("/personasVulnerables");
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      model.put("dto", dtoNuevo);
      model.put("edicion", true);
      model.put("id", context.pathParam("id"));
      context.render(rutaRegistroHbs, model);
    }
  }

  @Override //TODO: no funciona porque ya tiene asignada una tarjeta. No puede ser borrado *físico*
  public void delete(Context context) {
    Long id = Long.valueOf(context.pathParam("id"));
    Optional<PersonaVulnerable> persona = this.repositorioPersonaVulnerable.buscarPorId(id, PersonaVulnerable.class);
    if (persona.isPresent()) {
      withTransaction(() -> this.repositorioPersonaVulnerable.eliminarFisico(PersonaVulnerable.class, id));
      context.redirect("/personasVulnerables");
    } else {
      context.status(400).result("No se puede eliminar, la persona no cumple con las condiciones para ser eliminada.");
    }
  }

  public void solicitudTarjetas(Context context) {
    Long id = context.sessionAttribute("id");
    context.render(rutaSolicitudHbs, Map.of("id", id));
  }

  public void solicitarTarjetas(Context context) {
    int cantidadTarjetas = 0;
    String cantidad = context.formParam("cantidad");

    try {
      // Validación de campo "cantidad"
      if (cantidad == null || cantidad.isEmpty()) {
        throw new ValidacionFormularioException("El campo 'cantidad' está vacío. Por favor, complételo.");
      }

      Long id = context.sessionAttribute("id");
      Optional<PersonaHumana> registrador = repositorioPersonaHumana.buscarPorUsuario(id);
      if (registrador.isEmpty()) {
        throw new ValidacionFormularioException("No se ha encontrado al solicitante. Reintente.");
      }

      // Validación de tarjetas sin entregar
      if (registrador.get().getTarjetasSinEntregar().size() > CANTIDAD_TARJETAS_SIN_ENTREGAR_MAXIMAS) {
        throw new ValidacionFormularioException("No puede tener más de " + CANTIDAD_TARJETAS_SIN_ENTREGAR_MAXIMAS + " tarjetas sin entregar.");
      }

      // Validación de dirección
      validarDireccion(registrador.get());

      // Validación de cantidad de tarjetas
      cantidadTarjetas = validarCantidad(cantidad);

      // Solicita las tarjetas y las guarda en la base de datos
      List<Tarjeta> tarjetas = new ArrayList<>();
      for (int i = 0; i < cantidadTarjetas; i++) {
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setFechaRecepcionColaborador(LocalDate.now());
        registrador.get().agregarTarjetaSinEntregar(tarjeta);
        withTransaction(() -> repositorioTarjetas.guardar(tarjeta));
      }
      withTransaction(() -> repositorioPersonaHumana.actualizar(registrador.get()));

      // Renderiza el mensaje de éxito
      context.render(rutaSolicitudHbs, Map.of(
          "success", "La solicitud fue aceptada. Se le asignaron " + cantidadTarjetas + " tarjetas. El correo argentino las enviará a la dirección: " + registrador.get().getDireccion().getNomenclatura()
      ));

    } catch (ValidacionFormularioException e) {
      // Renderiza el mensaje de error
      context.render(rutaSolicitudHbs, Map.of("error", e.getMessage(), "cantidad", cantidad));
    }
  }

  private void validarDireccion(PersonaHumana registrador) throws ValidacionFormularioException {
    Direccion direccion = registrador.getDireccion();
    if (direccion == null || direccion.getNomenclatura() == null || direccion.getNomenclatura().isEmpty() || direccion.getCoordenada() == null) {
      throw new ValidacionFormularioException("No puede solicitar tarjetas porque no tiene asignada una dirección válida. Por favor, complete su dirección y luego regrese.");
    }
  }

  private int validarCantidad(String cantidad) throws ValidacionFormularioException {
    try {
      int cantidadTarjetas = Integer.parseInt(cantidad);
      if (cantidadTarjetas <= 0) {
        throw new ValidacionFormularioException("Debe solicitar al menos una tarjeta.");
      }
      return cantidadTarjetas;
    } catch (NumberFormatException e) {
      throw new ValidacionFormularioException("El valor ingresado en 'cantidad' no es válido. Debe ser un número.");
    }
  }
}