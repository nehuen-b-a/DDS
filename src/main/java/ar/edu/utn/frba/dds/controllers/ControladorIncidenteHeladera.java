package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.GsonFactory;
import ar.edu.utn.frba.dds.domain.entities.heladeras.CambioEstado;
import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Alerta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.TipoAlerta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.TipoIncidente;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.dtos.DTO;
import ar.edu.utn.frba.dds.dtos.IncidenteDTO;
import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import com.google.gson.Gson;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ControladorIncidenteHeladera implements WithSimplePersistenceUnit {
  private RepositorioHeladera repositorioHeladera;
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private Repositorio repositorioIncidente;
  private final String rutaReporteIncidente = "heladeras/reportarFalla.hbs";

  public ControladorIncidenteHeladera(RepositorioHeladera repositorioHeladera, RepositorioPersonaHumana repositorioPersonaHumana, Repositorio repositorioIncidente) {
    this.repositorioHeladera = repositorioHeladera;
    this.repositorioPersonaHumana = repositorioPersonaHumana;
    this.repositorioIncidente = repositorioIncidente;
  }

  public void create(Context context) {
    try {
      long heladeraId = Long.parseLong(context.pathParam("heladeraId"));
      Heladera heladera = repositorioHeladera.buscarPorId(heladeraId, Heladera.class)
          .orElseThrow(() -> new IllegalArgumentException("Heladera no encontrada."));

      Map<String, Object> model = new HashMap<>();
      model.put("heladeraId", heladera.getId());
      context.render(rutaReporteIncidente, model);
    } catch (IllegalArgumentException e) {
      throw new MensajeAmigableException(e.getMessage(), 400);
    } catch (Exception e) {
      e.printStackTrace();
      throw new MensajeAmigableException("Operación inválida.", 400);
    }
  }

  public void save(Context ctx) {
    IncidenteDTO dto = new IncidenteDTO();
    dto.obtenerFormulario(ctx);

    try {
      // Buscar la heladera usando el ID de la heladera desde el DTO
      Heladera heladera = repositorioHeladera.buscarPorId(dto.getHeladeraId(), Heladera.class)
          .orElseThrow(() -> new ValidacionFormularioException("Heladera no encontrada."));

      // Buscar al colaborador usando el ID del colaborador desde el DTO
      PersonaHumana colaborador = repositorioPersonaHumana.buscarPorUsuario(dto.getUsuarioId())
          .orElseThrow(() -> new ValidacionFormularioException("Colaborador no encontrado."));

      // Crear y guardar el incidente
      Incidente incidente = Incidente.fromDTO(dto, heladera, colaborador);
      incidente.setFecha(LocalDateTime.now());

      // Asignar técnico y actualizar estado de la heladera
      Tecnico tecnicoSeleccionado = incidente.asignarTecnico(heladera,
          this.repositorioIncidente.buscarTodos(Tecnico.class));
      incidente.setTecnicoSeleccionado(tecnicoSeleccionado);
      CambioEstado cambio = new CambioEstado(EstadoHeladera.valueOf(dto.getTipoAlerta()), LocalDate.now());
      heladera.cambiarEstado(cambio);

      withTransaction(() -> {
        repositorioIncidente.guardar(cambio);
        repositorioHeladera.actualizar(heladera);
        repositorioIncidente.guardar(incidente);
      });

      // Renderizar mensaje de éxito
      Map<String, Object> model = new HashMap<>();
      model.put("success", "Tu reporte ha sido guardado y se le ha avisado a un técnico para que vaya a arreglarlo.");
      ctx.render(rutaReporteIncidente, model);
    } catch (ValidacionFormularioException e) {
      // Manejo de validaciones específicas
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      ctx.render(rutaReporteIncidente, model);
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, Object> model = new HashMap<>();
      model.put("error", "Ocurrió un error al guardar el reporte. Intente nuevamente.");
      model.put("dto", dto);
      ctx.render(rutaReporteIncidente, model);
    }
  }

  public void procesarFraude(Long idHeladera) {
    try{
    Heladera heladera = this.repositorioHeladera.buscarPorId(idHeladera)
        .orElseThrow(() -> new IllegalArgumentException("Heladera no encontrada al procesar fraude."));
    CambioEstado cambioEstado = new CambioEstado(EstadoHeladera.FRAUDE, LocalDate.now());
    heladera.cambiarEstado(cambioEstado);
    withTransaction(() -> this.repositorioHeladera.actualizar(heladera));

    Incidente incidente = Incidente.builder()
        .fecha(LocalDateTime.now())
        .solucionado(false)
        .heladera(heladera)
        .tipoIncidente(new Alerta())
        .tipoAlerta(TipoAlerta.FALLA_CONEXION)
        .build();

    withTransaction(() -> repositorioIncidente.guardar(incidente));
    }
    catch(IllegalArgumentException e){
      System.out.println("Heladera no encontrada al procesar fraude.");
    }
    catch(Exception e){
      System.out.println("Ocurrió un error al procesar alerta por fraude");
    }
    // avisar a los suscritos a la heladera por fraude
    // cambiar el estado de la heladera
    // crear incidente
    // avisar al tecnico mas cercano a la heladera
  }
  public void procesarFallaConexion(String idHeladera){
    try {
      Heladera heladera = this.repositorioHeladera.buscarPorId(Long.parseLong(idHeladera))
          .orElseThrow(() -> new IllegalArgumentException("Heladera no encontrada al procesar falla de conexión."));
      CambioEstado cambioEstado = new CambioEstado(EstadoHeladera.FALLA_CONEXION, LocalDate.now());
      heladera.cambiarEstado(cambioEstado);
      withTransaction(() -> this.repositorioHeladera.actualizar(heladera));

      Incidente incidente = Incidente.builder()
          .fecha(LocalDateTime.now())
          .solucionado(false)
          .heladera(heladera)
          .tipoIncidente(new Alerta())
          .tipoAlerta(TipoAlerta.FALLA_CONEXION)
          .build();

//        Tecnico tecnicoSeleccionado = incidente.asignarTecnico(heladera,
//            this.repositorioIncidente.buscarTodos(Tecnico.class));
//        incidente.setTecnicoSeleccionado(tecnicoSeleccionado);

      withTransaction(() -> repositorioIncidente.guardar(incidente));
    }
    catch(IllegalArgumentException e){
      //manejar logica de heladera no encontrada en bd.
    }
    catch (Exception e){
      System.out.println("Ocurrió un error al registrar la falla de conexión");
    }
    // avisar a los suscritos a la heladera por fraude
    // crear incidente
    // avisar al tecnico mas cercano a la heladera
  }
}