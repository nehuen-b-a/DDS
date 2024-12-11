package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.HeladeraInactivaException;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Desperfecto;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.FaltanNViandas;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.QuedanNViandas;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioSuscripcion;
import ar.edu.utn.frba.dds.dtos.SuscripcionDTO;
import com.google.gson.Gson;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

public class ControladorSuscripcion implements WithSimplePersistenceUnit {
  private final String rutaSuscripcionHbs = "heladeras/heladeraParticular.hbs";
  private RepositorioHeladera repositorioHeladera;
  private RepositorioSuscripcion repositorioSuscripcion;
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private final Gson gson = GsonFactory.createGson();

  public ControladorSuscripcion(RepositorioHeladera repositorioHeladera, RepositorioSuscripcion repositorioSuscripcion, RepositorioPersonaHumana repositorioPersonaHumana) {
    this.repositorioHeladera = repositorioHeladera;
    this.repositorioSuscripcion = repositorioSuscripcion;
    this.repositorioPersonaHumana = repositorioPersonaHumana;
  }

  public void suscribir(Context context) {
    SuscripcionDTO dto = new SuscripcionDTO();
    dto.obtenerFormulario(context);
    Long id = context.sessionAttribute("id");

    Heladera heladera = null;
    try {
      PersonaHumana personaHumana = obtenerPersonaHumana(context);
      heladera = obtenerHeladera(context);

      // Validar entradas
      validarEntradas(dto);
      if(!heladera.estaActiva()){
        throw new HeladeraInactivaException();
      }
      // Procesar suscripciones
      if (existeSuscripcion(personaHumana, heladera)) {
        // Actualizar suscripción existente
        actualizarSuscripcion(dto, personaHumana, heladera);
      } else {
        // Crear nueva suscripción
        crearNuevaSuscripcion(dto, personaHumana, heladera);
      }

      // Redirigir a la heladera
      context.redirect("/heladeras/" + context.pathParam("heladeraId"));
    } catch (IllegalArgumentException e) {
      manejarError(context, e.getMessage(), dto, heladera, id);

    } catch(HeladeraInactivaException e){
      Map<String, Object> model = new HashMap<>();
      model.put("heladera", heladera);
      model.put("mostrarPersonaHumana",true);
      model.put("error", e.getMessage());
      context.render(rutaSuscripcionHbs, model);

    } catch (Exception e) {
      manejarError(context, "Ocurrió un error inesperado.", dto, heladera, id);
      e.printStackTrace();
    }

  }

  private boolean existeSuscripcion(PersonaHumana personaHumana, Heladera heladera) {
    // Verifica si ya existe una suscripción para la persona y heladera
    return repositorioSuscripcion.existePorPersonaYHeladera(personaHumana.getId(), heladera.getId());
  }

  private void crearNuevaSuscripcion(SuscripcionDTO dto, PersonaHumana personaHumana, Heladera heladera) {
    // Crear la nueva suscripción según el tipo indicado en el DTO
    if (dto.getDesperfecto()) {
      crearDesperfectoSuscripcion(personaHumana, heladera);
    } else if (dto.getCantidadViandasFaltantes() != null) {
      crearFaltanNViandasSuscripcion(dto.getCantidadViandasFaltantes(), personaHumana, heladera);
    } else if (dto.getCantidadViandasQueQuedan() != null) {
      crearQuedanNViandasSuscripcion(dto.getCantidadViandasQueQuedan(), personaHumana, heladera);
    } else {
      // Aquí podrías manejar la lógica si se intentara crear una suscripción vacía.
      throw new IllegalArgumentException("No se pueden crear suscripciones vacías.");
    }
  }

  private void crearDesperfectoSuscripcion(PersonaHumana personaHumana, Heladera heladera) {
    Desperfecto desperfecto = new Desperfecto();
    desperfecto.setSuscriptor(personaHumana);
    desperfecto.setHeladera(heladera);

    withTransaction(() -> repositorioSuscripcion.guardar(desperfecto));
  }

  private void crearFaltanNViandasSuscripcion(int cantidadViandasFaltantes, PersonaHumana personaHumana, Heladera heladera) {
    FaltanNViandas faltanNViandas = new FaltanNViandas();
    faltanNViandas.setSuscriptor(personaHumana);
    faltanNViandas.setHeladera(heladera);
    faltanNViandas.setCantidadViandasParaLlenarse(cantidadViandasFaltantes);

    withTransaction(() -> repositorioSuscripcion.guardar(faltanNViandas));
  }

  private void crearQuedanNViandasSuscripcion(int cantidadViandasQueQuedan, PersonaHumana personaHumana, Heladera heladera) {
    QuedanNViandas quedanNViandas = new QuedanNViandas();
    quedanNViandas.setSuscriptor(personaHumana);
    quedanNViandas.setHeladera(heladera);
    quedanNViandas.setCantidadViandasDisponibles(cantidadViandasQueQuedan);

    withTransaction(() -> repositorioSuscripcion.guardar(quedanNViandas));
  }

  private void actualizarSuscripcion(SuscripcionDTO dto, PersonaHumana personaHumana, Heladera heladera) {
    // Buscar todas las suscripciones para la persona y heladera
    List<Suscripcion> suscripcionesActuales = repositorioSuscripcion.buscarTodasPorPersonaYHeladera(personaHumana.getId(), heladera.getId());

    // Verificar y procesar cada tipo de suscripción
    for (Suscripcion suscripcionActual : suscripcionesActuales) {
      if (suscripcionActual instanceof Desperfecto) {
        if (dto.getDesperfecto()) {
          // Si ya es un desperfecto y se quiere mantener, no se necesita hacer nada.
          continue;
        } else {
          // Lógica para desuscribirse del desperfecto
          eliminarSuscripcion(personaHumana, heladera, Desperfecto.class);
        }
      } else if (suscripcionActual instanceof FaltanNViandas) {
        FaltanNViandas faltanNViandas = (FaltanNViandas) suscripcionActual;
        if (dto.getCantidadViandasFaltantes() != null) {
          faltanNViandas.setCantidadViandasParaLlenarse(dto.getCantidadViandasFaltantes());
          withTransaction(() -> repositorioSuscripcion.guardar(faltanNViandas));
        } else {
          // Lógica para desuscribirse de faltan N viandas
          eliminarSuscripcion(personaHumana, heladera, FaltanNViandas.class);
        }
      } else if (suscripcionActual instanceof QuedanNViandas) {
        QuedanNViandas quedanNViandas = (QuedanNViandas) suscripcionActual;
        if (dto.getCantidadViandasQueQuedan() != null) {
          quedanNViandas.setCantidadViandasDisponibles(dto.getCantidadViandasQueQuedan());
          withTransaction(() -> repositorioSuscripcion.guardar(quedanNViandas));
        } else {
          // Lógica para desuscribirse de quedan N viandas
          eliminarSuscripcion(personaHumana, heladera, QuedanNViandas.class);
        }
      }
    }

    // Si la suscripción de tipo Desperfecto no existe y se desea crear, hacerlo aquí
    if (dto.getDesperfecto() && suscripcionesActuales.stream().noneMatch(s -> s instanceof Desperfecto)) {
      crearYGuardarDesperfecto(personaHumana, heladera);
    }
  }

  private void eliminarSuscripcion(PersonaHumana personaHumana, Heladera heladera, Class<? extends Suscripcion> tipoClase) {
    List<Suscripcion> suscripciones = repositorioSuscripcion.buscarTodasPorPersonaYHeladera(personaHumana.getId(), heladera.getId());
    for (Suscripcion suscripcion : suscripciones) {
      if (tipoClase.isInstance(suscripcion)) {
        try {
          withTransaction(() -> repositorioSuscripcion.eliminarFisico(Suscripcion.class, suscripcion.getId()));
        } catch (Exception e) {
          // Capturamos la excepción y lanzamos un nuevo error con un mensaje específico
          throw new IllegalArgumentException("No se puede desuscribir porque hay sugerencias asociadas a esta suscripción.");
        }
        break; // Solo eliminar la primera que coincida
      }
    }
  }

  private PersonaHumana obtenerPersonaHumana(Context context) {
    return repositorioPersonaHumana.buscarPorUsuario(context.sessionAttribute("id"))
        .orElseThrow(() -> new IllegalArgumentException("No se ha encontrado al usuario responsable."));
  }

  private Heladera obtenerHeladera(Context context) {
    return repositorioHeladera.buscarPorId(Long.valueOf(context.pathParam("heladeraId")))
        .orElseThrow(() -> new IllegalArgumentException("La heladera a la cual se quiere suscribir no fue encontrada."));
  }

  private void validarEntradas(SuscripcionDTO dto) {
    if (dto.getCantidadViandasFaltantes() == null && dto.getCantidadViandasQueQuedan() == null && !dto.getDesperfecto()) {
      throw new IllegalArgumentException("No se ha indicado ningún tipo de suscripción.");
    }
    validarCantidad(dto.getCantidadViandasFaltantes());
    validarCantidad(dto.getCantidadViandasQueQuedan());
  }

  private void validarCantidad(Integer cantidad) {
    if (cantidad != null && cantidad < 0) {
      throw new IllegalArgumentException("No se puede indicar una cantidad negativa.");
    }
  }

  private void crearYGuardarDesperfecto(PersonaHumana personaHumana, Heladera heladera) {
    Desperfecto desperfecto = new Desperfecto();
    desperfecto.setSuscriptor(personaHumana);
    desperfecto.setHeladera(heladera);
    withTransaction(() -> repositorioSuscripcion.guardar(desperfecto));
  }

  private void manejarError(Context context, String errorMessage, SuscripcionDTO dto, Heladera heladera, Long usuarioId) {
    Map<String, Object> model = new HashMap<>();

    model.put("heladera", heladera);
    model.put("jsonHeladera", gson.toJson(heladera));
    model.put("mostrarPersonaHumana", true);
    model.put("hayViandas", heladera != null && !heladera.getViandas().isEmpty());

    // Obtener suscripciones
    Desperfecto desperfecto = (Desperfecto) obtenerSuscripcionSiExiste(heladera.getId(), usuarioId, Desperfecto.class);
    FaltanNViandas faltanNViandas = (FaltanNViandas) obtenerSuscripcionSiExiste(heladera.getId(), usuarioId, FaltanNViandas.class);
    QuedanNViandas quedanNViandas = (QuedanNViandas) obtenerSuscripcionSiExiste(heladera.getId(), usuarioId, QuedanNViandas.class);

    // Rellenar el modelo con información de suscripciones
    if (faltanNViandas != null) {
      model.put("faltanNViandas", faltanNViandas.getCantidadViandasParaLlenarse());
    }
    if (quedanNViandas != null) {
      model.put("quedanNViandas", quedanNViandas.getCantidadViandasDisponibles());
    }
    if (desperfecto != null) {
      model.put("desperfecto", desperfecto.getId());
    }

    // Verificar si hay actualizaciones
    model.put("actualizacion", faltanNViandas != null || desperfecto != null || quedanNViandas != null);
    model.put("error", errorMessage);
    model.put("dto", dto);

    // Renderizar la vista
    context.render(rutaSuscripcionHbs, model);
  }

  private Suscripcion obtenerSuscripcionSiExiste(Long heladeraId, Long usuarioId, Class<? extends Suscripcion> tipo) {
    try {
      return repositorioSuscripcion.buscarPorTipo(heladeraId, usuarioId, tipo);
    } catch (NoResultException e) {
      // No hay resultados, retornamos null
      return null;
    } catch (NonUniqueResultException e) {
      // Si hay más de un resultado, esto puede ser un problema en la lógica de suscripción
      // Manejar el error según sea necesario; podría ser un log o lanzar una excepción
      // Aquí solo vamos a retornar null para evitar romper el flujo.
      return null;
    }
  }
}