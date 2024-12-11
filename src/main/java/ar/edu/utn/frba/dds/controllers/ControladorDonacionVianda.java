package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.AccionApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.SolicitudApertura;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.repositories.imp.*;
import ar.edu.utn.frba.dds.dtos.ViandaDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import com.google.gson.Gson;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ControladorDonacionVianda implements ICrudViewsHandler, WithSimplePersistenceUnit {
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private RepositorioDonacionVianda repositorioDonacionVianda;
  private RepositorioHeladera repositorioHeladera;
  private RepositorioSolicitudApertura repositorioSolicitud;
  private final String rutaDonacionHbs = "colaboraciones/donacionVianda.hbs";
  private final String rutaListadoHbs = "colaboraciones/listadoDonacionesViandas.hbs";
  private final String rutaListadoDonaciones = "/donacionVianda";
  private final String ERROR = "error";
  private final Gson gson = GsonFactory.createGson();

  public ControladorDonacionVianda(RepositorioPersonaHumana repositorioPersonaHumana
                                  , RepositorioDonacionVianda repositorioDonacionVianda
                                  , RepositorioHeladera repositorioHeladera
                                  , RepositorioSolicitudApertura repositorioSolicitud) {
    this.repositorioPersonaHumana = repositorioPersonaHumana;
    this.repositorioDonacionVianda = repositorioDonacionVianda;
    this.repositorioHeladera = repositorioHeladera;
    this.repositorioSolicitud = repositorioSolicitud;
  }

  @Override
  public void index(Context context) {
    Long id = context.sessionAttribute("id");
    List<Vianda> donaciones = this.repositorioDonacionVianda.buscarViandasDe(id);

    List<ViandaDTO> donacionesDTO = donaciones.stream().map(donacion -> new ViandaDTO(donacion)).collect(Collectors.toList());

    Map<String, Object> model = new HashMap<>();
    model.put("donacionesVianda", donacionesDTO);
    context.render(rutaListadoHbs, model);
  }

  @Override
  public void show(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<Vianda> vianda = repositorioDonacionVianda
          .buscarPorId(Long.valueOf(context.pathParam("id")), Vianda.class);

      if (vianda.isEmpty()) {
        throw new ValidacionFormularioException("No existe la donación de vianda.");
      }

      ViandaDTO dto = new ViandaDTO(vianda.get());
      model.put("dto", dto);
      model.put("readonly", true);
      model.put("id", context.pathParam("id"));
      model.put("title", "Vianda para "+dto.getHeladeraNombre());
      model.put("jsonHeladeras", gson.toJson(this.repositorioDonacionVianda.buscarTodos(Heladera.class)));
      context.render(rutaDonacionHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      context.render(rutaListadoHbs, model);
    }
  }

  @Override
  public void create(Context context) {
    Map<String, Object> model = new HashMap<>();
    model.put("title", "Donar vianda");
    model.put("jsonHeladeras", gson.toJson(this.repositorioDonacionVianda.buscarTodos(Heladera.class)));
    context.render(rutaDonacionHbs, model);
  }

  @Override
  public void save(Context context) {
    ViandaDTO dto = new ViandaDTO();
    dto.obtenerFormulario(context);
    Long id = context.sessionAttribute("id");

    try {
      // agrego colaborador
      Optional<PersonaHumana> optPersona = repositorioPersonaHumana.buscarPorUsuario(id);
      if (optPersona.isEmpty()) {
          throw new ValidacionFormularioException("No se ha encontrado el id del usuario. Error en servidor.");
      }
      PersonaHumana persona = optPersona.get();
      Vianda nuevaDonacion = Vianda.fromDTO(dto);
      if (nuevaDonacion == null) {
        throw new ValidacionFormularioException("Se ha ingresado información incorrecta sobre la donación.");
      }
      nuevaDonacion.setPersonaHumana(persona);

      // agrego heladera
      Optional<Heladera> optHeladera = repositorioHeladera.buscarPorId(dto.getHeladeraId());
      if (optHeladera.isEmpty()) {
        throw new ValidacionFormularioException("Debe especificar una heladera.");
      }
      Heladera heladera = optHeladera.get();
      nuevaDonacion.setHeladera(heladera);

      // agrego solicitud de apertura para ingresar viandas
      SolicitudApertura soliApertura = SolicitudApertura.builder()
          .accion(AccionApertura.INGRESAR_VIANDA)
          .fechaSolicitud(LocalDateTime.now())
          .tarjeta(persona.getTarjetaEnUso())
          .cantidadViandas(1)
          .aperturaConcretada(false)
          .vianda(nuevaDonacion)
          .build();

      heladera.agregarSolicitudApertura(soliApertura);

      // guardar cambios
      //persona.sumarPuntaje(nuevaDonacion.calcularPuntaje());
      withTransaction(() -> {
        this.repositorioSolicitud.guardar(soliApertura);
        repositorioDonacionVianda.guardar(nuevaDonacion);
        this.repositorioHeladera.actualizar(heladera);
        repositorioPersonaHumana.actualizar(persona);
      });

      context.redirect(rutaListadoDonaciones);
    } catch (RuntimeException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("title", "Donar vianda");
      model.put("jsonHeladeras", gson.toJson(this.repositorioDonacionVianda.buscarTodos(Heladera.class)));
      model.put("dto", dto);
      context.render(rutaDonacionHbs, model);
    }
  }

  @Override // No usa.
  public void edit(Context context) {}

  @Override // No usa
  public void update(Context context) { }

  public void procesarIngresoDeViandaEnHeladera(Long idHeladera, Long idHumano, Long idVianda) {
    Optional<Vianda> viandaExistente = repositorioDonacionVianda.buscarPorId(idVianda, Vianda.class);
    if (viandaExistente.isEmpty()) {
      throw new ValidacionFormularioException("Donación de vianda no encontrada.");
    }

    // marcar vianda como entregad
    Optional<PersonaHumana> optPersona = repositorioPersonaHumana.buscarPorUsuario(idHumano);
    if (optPersona.isEmpty()) {
      throw new ValidacionFormularioException("No se ha encontrado el id del usuario. Error en servidor.");
    }

    PersonaHumana persona = optPersona.get();
    persona.sumarPuntaje(viandaExistente.get().calcularPuntaje());
    viandaExistente.get().setEntregada(true);

    // cerrar solicitud de apertura
    List<SolicitudApertura> solicitudes = this.repositorioSolicitud.listarRecientes(viandaExistente.get().getId(), "vianda");
    Optional<SolicitudApertura> optSolicitud = solicitudes.stream().filter(c-> !c.isAperturaConcretada()).findFirst();
    if (optSolicitud.isEmpty()) {
      throw new RuntimeException("No se encontró ninguna solicitud de apertura previa. Contactese con el administrador del sistema.");
    }
    SolicitudApertura solicitud = optSolicitud.get();
    solicitud.setAperturaConcretada(true);

    // agrego vianda a la heladera una vez que realmente es ingresada
    Optional<Heladera> optHeladera = repositorioHeladera.buscarPorId(idHeladera);
    if (optHeladera.isEmpty()) {
      throw new ValidacionFormularioException("Debe especificar una heladera.");
    }
    Heladera heladera = optHeladera.get();
    viandaExistente.get().setHeladera(heladera);

    // guardo cambios
    withTransaction(() -> {
      repositorioDonacionVianda.actualizar(viandaExistente.get());
      this.repositorioHeladera.actualizar(heladera);
      this.repositorioSolicitud.actualizar(solicitud);
    });
  }

  @Override
  public void delete(Context context) {
    Long id = Long.valueOf(context.pathParam("id"));
    Optional<Vianda> vianda = repositorioDonacionVianda.buscarPorId(id, Vianda.class);

    if (vianda.isPresent()) {
      withTransaction(() -> this.repositorioDonacionVianda.eliminarFisico(Vianda.class, id));
      context.redirect(rutaListadoDonaciones);
    } else {
      context.status(400).result("No se puede cancelar la donación de vianda, pues no fue encontrada.");
    }
  }
}
