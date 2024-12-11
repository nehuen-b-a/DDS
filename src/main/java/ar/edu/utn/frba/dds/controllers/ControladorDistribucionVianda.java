package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.HeladeraVirtualmenteVaciaException;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.AccionApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.SolicitudApertura;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioSolicitudApertura;
import ar.edu.utn.frba.dds.exceptions.SinTarjetaException;
import com.google.gson.Gson;
import ar.edu.utn.frba.dds.domain.entities.viandas.DistribucionVianda;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioDistribucionVianda;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.dtos.DistribucionViandaDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class ControladorDistribucionVianda implements ICrudViewsHandler, WithSimplePersistenceUnit {
  private RepositorioDistribucionVianda repositorioDistribucion;
  private RepositorioPersonaHumana repositorioPHumana;
  private RepositorioHeladera repositorioHeladera;
  private RepositorioSolicitudApertura repositorioSoliApe;
  private final String rutaAltaHbs = "colaboraciones/distribucionDeVianda.hbs";
  private final String rutaListadoHbs = "colaboraciones/distribucionesDeViandas.hbs";
  private final Gson gson = GsonFactory.createGson();

  public ControladorDistribucionVianda(RepositorioDistribucionVianda repositorioDistribucion,
                                       RepositorioPersonaHumana repositorioPHumana,
                                       RepositorioHeladera repositorioHeladera,
                                       RepositorioSolicitudApertura repositorioSoliApe) {
    this.repositorioDistribucion = repositorioDistribucion;
    this.repositorioPHumana = repositorioPHumana;
    this.repositorioHeladera = repositorioHeladera;
    this.repositorioSoliApe = repositorioSoliApe;
  }

  @Override
  public void index(Context context) {
    List<DistribucionVianda> distribuciones = this.repositorioDistribucion.buscarDistribuciones(context.sessionAttribute("id"));

    List<DistribucionViandaDTO> distDTOs = distribuciones.stream().map(dist -> fromEntity(dist, this.repositorioSoliApe)).collect(Collectors.toList());

    Map<String, Object> model = new HashMap<>();
    model.put("distribuciones", distDTOs);

    context.render(this.rutaListadoHbs, model);
  }

  @Override
  public void show(Context context) {
    showEntity(context, true);
  }

  @Override
  public void create(Context context) {
    Map<String, Object> model = new HashMap<>();
    model.put("readonly", false);
    model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
    context.render(this.rutaAltaHbs, model);
  }

  @Override
  public void save(Context context) {
    DistribucionVianda dist = null;
    try {
      dist = entityfromContext(context);
      if (dist.getColaborador().getTarjetaEnUso() == null) {
        throw new SinTarjetaException("No puede distribuir viandas sin antes solicitar una tarjeta.");
      }
    } catch (RuntimeException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      model.put("dto", DTOfromContext(context));
      context.render(rutaAltaHbs, model);
      return;
    }

    // agrego solicitud de apertura para quitar viandas de origen
    Heladera origen = dist.getHeladeraOrigen();
    SolicitudApertura soliApertura = SolicitudApertura.builder()
        .accion(AccionApertura.QUITAR_VIANDA)
        .fechaSolicitud(LocalDateTime.now())
        .tarjeta(dist.getColaborador().getTarjetaEnUso())
        .cantidadViandas(dist.getCantidadViandas())
        .aperturaConcretada(false)
        .distribucion(dist)
        .build();
    try {
      origen.agregarSolicitudApertura(soliApertura);
    } catch (Exception e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", DTOfromContext(context));
      model.put("readonly", false);
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }
    DistribucionVianda nuevaDist = dist;

    withTransaction(() -> {
      this.repositorioSoliApe.guardar(soliApertura);
      this.repositorioDistribucion.guardar(nuevaDist);
      this.repositorioHeladera.guardar(origen);
    });

    context.redirect("/distribucionVianda");
  }

  @Override
  public void edit(Context context) {
    showEntity(context, false);
  }

  private void showEntity(Context context, boolean readonly) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<DistribucionVianda> optDistribucion = this.repositorioDistribucion.buscarPorId(Long.valueOf(context.pathParam("id")), DistribucionVianda.class);

      if (optDistribucion.isEmpty()) {
        throw new ValidacionFormularioException("No existe una distribución de vianda con este id.");
      }

      DistribucionVianda distribucion = optDistribucion.get();
      if (!distribucion.getColaborador().getUsuario().getId().equals(context.sessionAttribute("id"))) {
        throw new ValidacionFormularioException("No tiene permiso para acceder a este recurso.");
      }

      List<Heladera> heladeras = new ArrayList<>();
      heladeras.add(distribucion.getHeladeraOrigen());
      heladeras.add(distribucion.getHeladeraDestino());
      DistribucionViandaDTO dto = fromEntity(distribucion, this.repositorioSoliApe);
      model.put("dto", dto);
      model.put("readonly", distribucion.isTerminada() || readonly);
      model.put("id", context.pathParam("id"));
      model.put("jsonHeladeras", gson.toJson(heladeras));
      context.render(this.rutaAltaHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      context.render(this.rutaListadoHbs, model);
    }
  }

  @Override
  public void update(Context context) {
    DistribucionVianda viejaDist = null;
    try {
      Long id = Long.parseLong(context.pathParam("id"));
      Optional<DistribucionVianda> optViejaDist = this.repositorioDistribucion.buscarPorId(
          id, DistribucionVianda.class);

      if (optViejaDist.isEmpty()) {
        throw new ValidacionFormularioException("Distribución de vianda no encontrada.");
      }
      viejaDist = optViejaDist.get();
      if (!context.sessionAttribute("id").equals(viejaDist.getColaborador().getUsuario().getId())) {
        throw new ValidacionFormularioException("No puede modificar las distribuciones de vianda de otros colaboradores.");
      }
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", fromEntity(viejaDist, this.repositorioSoliApe));
      model.put("readonly", false);
      model.put("id", context.pathParam("id"));
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }

    // agrego solicitud de apertura para agregar viandas al destino
    Heladera destino = viejaDist.getHeladeraDestino();
    SolicitudApertura soliApertura = SolicitudApertura.builder()
        .accion(AccionApertura.INGRESAR_VIANDA)
        .fechaSolicitud(LocalDateTime.now())
        .tarjeta(viejaDist.getColaborador().getTarjetaEnUso())
        .cantidadViandas(viejaDist.getCantidadViandas())
        .aperturaConcretada(false)
        .distribucion(viejaDist)
        .build();

    try {
      destino.agregarSolicitudApertura(soliApertura);
    } catch (Exception e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", fromEntity(viejaDist, this.repositorioSoliApe));
      model.put("readonly", false);
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }

    // actualizo solicitud anterior
    List<SolicitudApertura> solicitudes = this.repositorioSoliApe.listarRecientes(viejaDist.getId(), "distribucion");
    Optional<SolicitudApertura> optSolOrigen = solicitudes.stream().filter(c-> !c.isAperturaConcretada()
            && c.getAccion().equals(AccionApertura.QUITAR_VIANDA)).findFirst();
    if (optSolOrigen.isEmpty()) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "No se encontró que la distribución haya sido inicializada. Contactese con el administrador del sistema.");
      model.put("dto", fromEntity(viejaDist, this.repositorioSoliApe));
      model.put("readonly", false);
      model.put("id", context.pathParam("id"));
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }
    SolicitudApertura solOrigen = optSolOrigen.get();
    solOrigen.setAperturaConcretada(true);

    withTransaction(() -> {
      this.repositorioSoliApe.actualizar(solOrigen);
      this.repositorioSoliApe.guardar(soliApertura);
      this.repositorioHeladera.guardar(destino);
    });

    context.redirect("/distribucionVianda");
  }

  public void finish(Context context) {
    DistribucionVianda viejaDist = null;
    try {
      Long id = Long.parseLong(context.pathParam("id"));
      Optional<DistribucionVianda> optViejaDist = this.repositorioDistribucion.buscarPorId(
          id, DistribucionVianda.class);

      if (optViejaDist.isEmpty()) {
        throw new ValidacionFormularioException("Distribución de vianda no encontrada.");
      }
      viejaDist = optViejaDist.get();
      if (!context.sessionAttribute("id").equals(viejaDist.getColaborador().getUsuario().getId())) {
        throw new ValidacionFormularioException("No puede modificar las distribuciones de vianda de otros colaboradores.");
      }
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", fromEntity(viejaDist, this.repositorioSoliApe));
      model.put("readonly", false);
      model.put("id", context.pathParam("id"));
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }

    // distribución finalizada
    viejaDist.setTerminada(true);

    // actualizo la 2da solicitud de apertura
    // actualizo solicitud anterior
    List<SolicitudApertura> solicitudes = this.repositorioSoliApe.listarRecientes(viejaDist.getId(), "distribucion");
    Optional<SolicitudApertura> optSolDest = solicitudes.stream().filter(c-> !c.isAperturaConcretada()
        && c.getAccion().equals(AccionApertura.INGRESAR_VIANDA)).findFirst();
    if (optSolDest.isEmpty()) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "No se encontró que se haya iniciado el tramo a la healdera destino previamente. Contactese con el administrador del sistema.");
      model.put("dto", fromEntity(viejaDist, this.repositorioSoliApe));
      model.put("readonly", false);
      model.put("id", context.pathParam("id"));
      model.put("jsonHeladeras", gson.toJson(this.repositorioHeladera.buscarTodos(Heladera.class)));
      context.render(rutaAltaHbs, model);
      return;
    }
    SolicitudApertura solDest = optSolDest.get();
    solDest.setAperturaConcretada(true);

    DistribucionVianda dist = viejaDist;
    withTransaction(() -> {
      this.repositorioDistribucion.actualizar(dist);
      this.repositorioSoliApe.actualizar(solDest);
    });

    context.redirect("/distribucionVianda");
  }

  @Override
  public void delete(Context context) {
    Long id = Long.valueOf(context.pathParam("id"));
    Optional<DistribucionVianda> dist = this.repositorioDistribucion.buscarPorId(id, DistribucionVianda.class);
    if (dist.isPresent()) {
      withTransaction(() -> this.repositorioDistribucion.eliminarFisico(DistribucionVianda.class, id));
      context.redirect("/distribucionVianda");
    } else {
      context.status(400).result("No se puede eliminar, la distribución de vianda no cumple con las condiciones para ser eliminada.");
    }
  }

  private DistribucionViandaDTO DTOfromContext(Context context) {
    return DistribucionViandaDTO.builder()
        .heladeraOrigenId(Long.parseLong(context.formParam("heladeraOrigenId") == "" ? "0" : context.formParam("heladeraOrigenId")))
        .heladeraOrigenNombre(context.formParam("heladeraOrigen"))
        .heladeraDestinoId(Long.parseLong(context.formParam("heladeraDestinoId") == "" ? "0" : context.formParam("heladeraDestinoId")))
        .heladeraDestinoNombre(context.formParam("heladeraDestino"))
        .motivo(context.formParam("motivo"))
        .cantidadViandas(Integer.parseInt(context.formParam("cantidadViandas")))
        .build();
  }

  private DistribucionVianda entityfromContext(Context context) {
    int cantidadViandas = 0;
    try {
      cantidadViandas = Integer.parseInt(context.formParam("cantidadViandas"));
    } catch (NumberFormatException e) {
      throw new ValidacionFormularioException("Cantidad de viandas inválida.");
    }

    Long helOrigenId = Long.parseLong(context.formParam("heladeraOrigenId") == "" ? "0" : context.formParam("heladeraOrigenId"));
    Long helDestinoId = Long.parseLong(context.formParam("heladeraDestinoId") == "" ? "0" : context.formParam("heladeraDestinoId"));

    CamposObligatoriosVacios.validarCampos(
        Pair.of("Cantidad de viandas", cantidadViandas),
        Pair.of("Motivo", context.formParam("motivo")),
        Pair.of("Heladera origen", helOrigenId),
        Pair.of("Heladera destino", helDestinoId)
    );

    if (cantidadViandas <= 0) {
      throw new ValidacionFormularioException("Cantidad de viandas inválida.");
    }

    Optional<PersonaHumana> optPHumana = this.repositorioPHumana.buscarPorUsuario(context.sessionAttribute("id"));
    if (optPHumana.isEmpty()) {
      throw new ValidacionFormularioException("Colaborador inválido.");
    }

    Optional<Heladera> optHelOrigen = this.repositorioHeladera.buscarPorId(helOrigenId);
    if (optHelOrigen.isEmpty()) {
      throw new ValidacionFormularioException("Heladera origen inválida.");
    }

    Optional<Heladera> optHelDestino = this.repositorioHeladera.buscarPorId(helDestinoId);
    if (optHelDestino.isEmpty()) {
      throw new ValidacionFormularioException("Heladera destino inválida.");
    }

    if (helDestinoId.equals(helOrigenId)) {
      throw new ValidacionFormularioException("El origen y destino deben ser distintos.");
    }

    Heladera destino = optHelDestino.get();
    if (cantidadViandas > destino.getCapacidadMaximaViandas()) {
      throw new ValidacionFormularioException("La heladera '"
          + destino.getNombre()
          + "' tiene una capacidad máxima de "
          + destino.getCapacidadMaximaViandas() + "."
          + " Reduzca la cantidad.");
    }

    DistribucionVianda distribucionVianda = DistribucionVianda.builder()
        .colaborador(optPHumana.get())
        .heladeraOrigen(optHelOrigen.get())
        .heladeraDestino(destino)
        .fecha(LocalDate.now())
        .cantidadViandas(cantidadViandas)
        .motivo(context.formParam("motivo")).build();

    optPHumana.get().sumarPuntaje(distribucionVianda.calcularPuntaje());

    withTransaction(() -> repositorioPHumana.actualizar(optPHumana.get()));

    return distribucionVianda;
  }

  public static DistribucionViandaDTO fromEntity(DistribucionVianda entity, RepositorioSolicitudApertura repoSoli) {
    DistribucionViandaDTO.DistribucionViandaDTOBuilder dtoBuilder = DistribucionViandaDTO.builder()
        .id(entity.getId())
        .fecha(entity.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yy")))
        .heladeraOrigenId(entity.getHeladeraOrigen().getId())
        .heladeraOrigenNombre(entity.getHeladeraOrigen().getNombre())
        .heladeraDestinoId(entity.getHeladeraDestino().getId())
        .heladeraDestinoNombre(entity.getHeladeraDestino().getNombre())
        .terminada(entity.isTerminada())
        .motivo(entity.getMotivo())
        .cantidadViandas(entity.getCantidadViandas());

    List<SolicitudApertura> solicitudes = repoSoli.listarRecientes(entity.getId(), "distribucion");
    Optional<SolicitudApertura> solicitudOrigen = solicitudes.stream().filter(c-> c.getAccion().equals(AccionApertura.QUITAR_VIANDA)).findFirst();

    if (solicitudOrigen.isPresent()) {
      dtoBuilder.horaSolicitudEnOrigen(solicitudOrigen.get().getFechaSolicitud().format(DateTimeFormatter. ofPattern("HH:mm:ss")));

      Optional<SolicitudApertura> solicitudDestino = solicitudes.stream().filter(c-> c.getAccion().equals(AccionApertura.INGRESAR_VIANDA)).findFirst();

      if (solicitudDestino.isPresent()) {
        dtoBuilder.horaSolicitudEnDestino(solicitudDestino.get().getFechaSolicitud().format(DateTimeFormatter. ofPattern("HH:mm:ss")));
      }
    }

    return dtoBuilder.build();
  }
}
