package ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes;

import ar.edu.utn.frba.dds.domain.converters.TipoAlertaConverter;
import ar.edu.utn.frba.dds.domain.converters.TipoIncidenteConverter;
import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;
import ar.edu.utn.frba.dds.domain.entities.heladeras.CambioEstado;
import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Visita;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTecnicos;
import ar.edu.utn.frba.dds.dtos.IncidenteDTO;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import ar.edu.utn.frba.dds.utils.manejos.ManejoDistancias;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Table(name="incidente")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Incidente {
  @Id
  @GeneratedValue
  private Long id;

  @Setter
  @Column(name="fecha", columnDefinition = "DATE", nullable = false)
  private LocalDateTime fecha;

  @Setter
  @ManyToOne
  @JoinColumn(name="id_tecnico", referencedColumnName = "id")
  private Tecnico tecnicoSeleccionado;

  @OneToMany
  @JoinColumn(name = "id_incidente", nullable = false)
  private List<Visita> visitas;

  @Setter
  @Column(name="solucionado", columnDefinition = "TINYINT(1)", nullable = false)
  private boolean solucionado;

  @Setter
  @ManyToOne
  @JoinColumn(name="id_heladera", referencedColumnName = "id", nullable = false)
  private Heladera heladera;

  @Setter
  @Transient
  private TipoIncidente tipoIncidente;

  @Transient
  private RepositorioTecnicos repositorioTecnicos;

  @Setter
  @ManyToOne
  @JoinColumn(name="id_personaHumana", referencedColumnName = "id")
  private PersonaHumana colaborador;

  @Setter @Getter
  @Column(name="descripcion_del_colaborador", columnDefinition = "TEXT")
  private String descripcionDelColaborador;

  @Column(name="ruta_foto")
  private String foto;

  @Setter
  @Convert(converter = TipoAlertaConverter.class)
  @Column(name="tipo_alerta")
  private TipoAlerta tipoAlerta;
  public void registrarVisita(Visita visita, boolean solucionado) {
    if (this.visitas == null) {
      this.visitas = new ArrayList<>();
    }
    this.visitas.add(visita);
    if (solucionado) {
      heladera.cambiarEstado(new CambioEstado(EstadoHeladera.ACTIVA, LocalDate.now()));
      this.setSolucionado(true);
    }
  }

  public Tecnico asignarTecnico(Heladera heladera, List<Tecnico> tecnicos) throws MessagingException, UnsupportedEncodingException {
    double distanciaMaxima = Double.parseDouble(PrettyProperties.getInstance().propertyFromName("distancia_maxima_para_llamar_tecnico_en_km"));
    Tecnico tecnicoSeleccionado = null; // Inicializamos a null para verificar más adelante si se encontró un técnico.

    for (Tecnico tecnico : tecnicos) {
      double distanciaActual = ManejoDistancias.distanciaHaversineConCoordenadasEnKm(
        heladera.getDireccion().getCoordenada(),
        tecnico.getDireccion().getCoordenada()
      );

      // Verificamos si el técnico está dentro de la distancia máxima para ser avisado
      if (distanciaActual < distanciaMaxima && distanciaActual <= tecnico.getDistanciaMaximaEnKmParaSerAvisado()) {
        distanciaMaxima = distanciaActual; // Actualizamos la distancia máxima considerada.
        tecnicoSeleccionado = tecnico; // Actualizamos el técnico seleccionado.
      }
    }

    // Verificamos si se encontró un técnico para asignar
    if (tecnicoSeleccionado != null) {
      enviarNotificacion(tecnicoSeleccionado, heladera);
    } else {
      throw new IllegalStateException("No se encontró ningún técnico disponible para la heladera.");
    }

    return tecnicoSeleccionado;
  }

  private void enviarNotificacion(Tecnico tecnico, Heladera heladera) throws MessagingException, UnsupportedEncodingException {
    Mensaje mensaje = new Mensaje(
        "SMAACVS: Notificación de Revisión de Heladera",
        String.format("Estimado/a Técnico/a,\n\n"
                + "Se le ha asignado la tarea de revisar la heladera '%s', ubicada en la dirección '%s'.\n"
                + "Descripción del incidente: %s.\n\n"
                + "Le agradecemos su colaboración y compromiso con el sistema para la mejora del acceso alimentario en contextos de vulnerabilidad socioeconómica.\n\n"
                + "Atentamente,\n"
                + "Sistema para la Mejora del Acceso Alimentario en Contextos de Vulnerabilidad Socioeconómica",
            heladera.getNombre(),
            heladera.getDireccion().getNomenclatura(),
            tipoIncidente.obtenerDescripcionIncidente(this)
        ),
        LocalDateTime.now()
    );

    tecnico.getContacto().enviarMensaje(mensaje);
  }

  public static Incidente fromDTO(IncidenteDTO dto, Heladera heladera, PersonaHumana colaborador) {
    // Validar que los parámetros no sean nulos
    if (dto == null) {
      throw new IllegalArgumentException("El DTO del incidente no puede ser nulo.");
    }
    if (heladera == null) {
      throw new IllegalArgumentException("La heladera no puede ser nula.");
    }
    if (colaborador == null) {
      throw new IllegalArgumentException("El colaborador no puede ser nulo.");
    }

    // Validar que los campos necesarios en el DTO no sean nulos
    if (dto.getTipoAlerta() == null || dto.getTipoAlerta().isEmpty()) {
      throw new IllegalArgumentException("El tipo de incidente no puede ser nulo o vacío.");
    }
    if (dto.getDescripcionDelColaborador() == null) {
      throw new IllegalArgumentException("La descripción del colaborador no puede ser nula.");
    }

    Incidente incidente = new Incidente();
    incidente.heladera = heladera;
    incidente.colaborador = colaborador;

    // Determinar el tipo de incidente en base al DTO
    switch (dto.getTipoAlerta()) {
      case "FALLA_TECNICA":
        incidente.tipoIncidente = new FallaTecnica();
        incidente.tipoAlerta = TipoAlerta.FRAUDE;
        break;
      case "FRAUDE":
      case "FALLA_CONEXION":
      case "FALLA_TEMPERATURA":
        incidente.tipoIncidente = new Alerta();
        incidente.tipoAlerta = TipoAlerta.valueOf(dto.getTipoAlerta());
        break;
      default:
        throw new IllegalArgumentException("Tipo de incidente no reconocido: " + dto.getTipoAlerta());
    }
    // Asignar la descripción
    incidente.descripcionDelColaborador = dto.getDescripcionDelColaborador();

    return incidente;
  }
}