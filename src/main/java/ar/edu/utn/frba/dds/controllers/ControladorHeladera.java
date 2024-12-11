package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Modelo;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Alerta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.TipoAlerta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorTemperatura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Desperfecto;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.FaltanNViandas;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.QuedanNViandas;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.TipoSuscripcion;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Municipio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Provincia;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefServicio;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioGeoRef;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaJuridica;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioSuscripcion;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTecnicos;
import ar.edu.utn.frba.dds.dtos.HeladeraDTO;
import ar.edu.utn.frba.dds.dtos.TecnicoDTO;
import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import com.google.gson.Gson;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.persistence.NoResultException;

public class ControladorHeladera implements ICrudViewsHandler, WithSimplePersistenceUnit {
  private RepositorioHeladera repositorioHeladera;
  private RepositorioPersonaJuridica repositorioPersonaJuridica;
  private RepositorioSuscripcion repositorioSuscripcion;
  private Repositorio repositorioIncidente;
  private RepositorioTecnicos repositorioTecnicos;
  private final String rutaAltaHbs = "colaboraciones/cuidarHeladera.hbs";
  private final String rutaRecomendacionHbs = "colaboraciones/recomendacionDePuntos.hbs";
  private final String rutaParticularHbs = "heladeras/heladeraParticular.hbs";
  private final Gson gson = GsonFactory.createGson();

  public ControladorHeladera(RepositorioHeladera repositoriaHeladera, RepositorioPersonaJuridica repositorioPersonaJuridica,
                             RepositorioSuscripcion repositorioSuscripcion, Repositorio repositorioIncidente,
                             RepositorioTecnicos repositorioTecnicos) {
    this.repositorioHeladera = repositoriaHeladera;
    this.repositorioSuscripcion = repositorioSuscripcion;
    this.repositorioPersonaJuridica = repositorioPersonaJuridica;
    this.repositorioIncidente = repositorioIncidente;
    this.repositorioTecnicos = repositorioTecnicos;
  }

  @Override
  public void index(Context context) {
    // tiene sentido? si ya estan plasmadas en el mapa
  }

  @Override
  public void show(Context context) {
    try {
      Long heladeraId = Long.parseLong(context.pathParam("heladeraId"));
      String rol = context.sessionAttribute("rol");
      Long usuarioId = context.sessionAttribute("id");

      // Buscar la heladera en el repositorio
      Optional<Heladera> heladeraOpt = repositorioHeladera.buscarPorId(heladeraId, Heladera.class);

      // Validar la existencia de la heladera
      if (heladeraOpt.isEmpty()) {
        throw new IllegalArgumentException("Heladera no encontrada.");
      }

      // Intentar obtener las suscripciones sin lanzar error si no existen
      Desperfecto desperfecto = obtenerSuscripcionSiExiste(heladeraId, usuarioId, Desperfecto.class);
      FaltanNViandas faltanNViandas = obtenerSuscripcionSiExiste(heladeraId, usuarioId, FaltanNViandas.class);
      QuedanNViandas quedanNViandas = obtenerSuscripcionSiExiste(heladeraId, usuarioId, QuedanNViandas.class);

      // Preparar el modelo para la vista
      Heladera heladera = heladeraOpt.get();
      Map<String, Object> model = new HashMap<>();
      model.put("heladera", heladera);
      model.put("hayViandas", !heladera.getViandas().isEmpty());
      model.put("actualizacion", faltanNViandas != null || desperfecto != null || quedanNViandas != null);
      if (faltanNViandas != null) {
        model.put("faltanNViandas", faltanNViandas.getCantidadViandasParaLlenarse());
      }
      if (quedanNViandas != null) {
        model.put("quedanNViandas", quedanNViandas.getCantidadViandasDisponibles());
      }
      if (desperfecto != null) {
        model.put("desperfecto", desperfecto.getId());
      }
      model.put("jsonHeladera", gson.toJson(heladera));

      // Configurar la visualización según el rol
      if (TipoRol.valueOf(rol).equals(TipoRol.PERSONA_HUMANA)) {
        model.put("mostrarPersonaHumana", true);
      } else {
        model.put("mostrarPersonaJuridica", true);
      }

      context.render(rutaParticularHbs, model);
    } catch (NumberFormatException e) {
      throw new MensajeAmigableException("ID de heladera inválido.", 400);
    } catch (Exception e) {
      throw new MensajeAmigableException("Operación inválida.", 400);
    }
  }

  private <T extends Suscripcion> T obtenerSuscripcionSiExiste(Long heladeraId, Long usuarioId, Class<T> tipo) {
    try {
      return (T) this.repositorioSuscripcion.buscarPorTipo(heladeraId, usuarioId, tipo);
    } catch (NoResultException e) {
      return null;
    }
  }

  @Override
  public void create(Context context) {
    Map<String, Object> model = new HashMap<>();
    Long id = context.sessionAttribute("id");
    model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
    model.put("modelos", this.repositorioHeladera.buscarTodos(Modelo.class));
    model.put("id", id);
    context.render(this.rutaAltaHbs, model);
  }

  @Override
  public void save(Context context) {
    HeladeraDTO dto = new HeladeraDTO();
    dto.obtenerFormulario(context);
    Long id = context.sessionAttribute("id");

    try {
      String idModelo = context.formParam("modelo");
      if (idModelo == null || idModelo.isEmpty()) {
        throw new ValidacionFormularioException("El modelo de la heladera es obligatorio y determina el rango de temperaturas válidas.");
      }

      Modelo modelo = this.repositorioHeladera
          .buscarPorId(Long.parseLong(idModelo), Modelo.class)
          .orElseThrow(() -> new ValidacionFormularioException("No se ha encontrado el modelo indicado."));

      dto.setTemperaturaMinima(modelo.getTemperaturaMinima());
      dto.setTemperaturaMaxima(modelo.getTemperaturaMaxima());
      dto.setModelo(idModelo);

      Heladera nuevaHeladera = Heladera.fromDTO(dto);
      nuevaHeladera.setFechaRegistro(LocalDateTime.now());
      nuevaHeladera.setEstado(EstadoHeladera.ACTIVA);
      nuevaHeladera.setModelo(modelo);

      PersonaJuridica personaJuridica = this.repositorioPersonaJuridica
          .buscarPorUsuario(id)
          .orElseThrow(() -> new ValidacionFormularioException("No se ha podido encontrar la persona jurídica responsable."));

      personaJuridica.agregarHeladera(nuevaHeladera);

      withTransaction(() -> {
        repositorioHeladera.guardar(nuevaHeladera);
        repositorioPersonaJuridica.actualizar(personaJuridica);
      });

      context.redirect("/heladeras");

    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      model.put("id", id);
      model.put("modelos", this.repositorioHeladera.buscarTodos(Modelo.class));
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
    }
  }

  @Override
  public void edit(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<Heladera> heladera = this.repositorioHeladera.buscarPorId(Long.valueOf(context.pathParam("id")), Heladera.class);

      if (heladera.isEmpty()) {
        throw new ValidacionFormularioException("No existe un técnico con este id.");
      }

      HeladeraDTO dto = new HeladeraDTO(heladera.get());
      model.put("dto", dto);
      model.put("edicion", true);
      model.put("id", context.pathParam("id"));
      context.render(rutaAltaHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      context.render("", model);
    }
  }

  @Override
  public void update(Context context) {
    // TODO: Estaria bueno hacerlo ya que es una de las entidades mas importantes
  }

  @Override
  public void delete(Context context) {
    Long id = Long.valueOf(context.pathParam("id"));
    Optional<Heladera> heladera = this.repositorioHeladera.buscarPorId(id, Heladera.class);
    if (heladera.isPresent()) {
      withTransaction(() -> this.repositorioHeladera.eliminarFisico(Heladera.class, id));
      context.redirect("/heladeras");
    } else {
      context.status(400).result("No se puede eliminar, la heladera no cumple con las condiciones para ser eliminada.");
    }
  }

  public void recomendacion(Context context) {
    Map<String, Object> model = new HashMap<>();
    Long id = context.sessionAttribute("id");
    model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
    model.put("id", id);
    context.render(this.rutaRecomendacionHbs, model);
  }

  public void actualizarTemperatura(String idHeladera, String valor, ReceptorTemperatura receptor) {
    Heladera heladera = repositorioHeladera.buscarPorId(Long.parseLong(idHeladera)).orElseThrow(() ->
        new IllegalArgumentException("Heladera no encontrada al actualizar temperatura")
    );

    float temperatura = Float.parseFloat(valor);
    heladera.cambiarTemperatura(temperatura);
    withTransaction(()-> this.repositorioHeladera.actualizar(heladera));

    if(!heladera.temperaturaEnRango(temperatura)){
      receptor.eliminarJobDeHeladera(idHeladera);
      Incidente incidente = Incidente.builder()
          .fecha(LocalDateTime.now())
          .solucionado(false)
          .heladera(heladera)
          .tipoIncidente(new Alerta())
          .tipoAlerta(TipoAlerta.FALLA_TEMPERATURA)
          .build();
      withTransaction(()->repositorioIncidente.guardar(incidente));
      try {
        Tecnico tecnicoSeleccionado = incidente.asignarTecnico(heladera, repositorioTecnicos.buscarTodos(Tecnico.class));
        incidente.setTecnicoSeleccionado(tecnicoSeleccionado);
        withTransaction(()->repositorioIncidente.actualizar(incidente));
      }
      catch (IllegalStateException e){
        throw new RuntimeException(e);
      } catch (MessagingException e) {
        throw new RuntimeException(e);
      } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }

    }

  }

}
