package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.domain.converters.LocalDateTimeAttributeConverter;
import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.AccionApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.SolicitudApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.GestorSuscripciones;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.TipoSuscripcion;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.dtos.HeladeraDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import com.google.gson.annotations.Expose;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import kotlin.BuilderInference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Builder
@Entity @Table(name="heladera")
@AllArgsConstructor
public class Heladera implements Contribucion {
  @Getter @Setter @Expose
  @Id @GeneratedValue
  private Long id;

  @Setter @Expose
  @Column(name="nombre", nullable = false)
  private String nombre;

  @Setter @Expose
  @Embedded
  private Direccion direccion;

  @Setter @Expose
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @Column(name = "fechaRegistro", nullable = false)
  private LocalDateTime fechaRegistro;

  @Setter @Expose
  @Column(name = "capacidadMaximaVianda", nullable = false)
  private int capacidadMaximaViandas;

  @Setter @Expose
  @ManyToOne
  @JoinColumn(name = "modelo_id", referencedColumnName = "id", nullable = false)
  private Modelo modelo;

  @OneToMany(mappedBy = "heladera") @Expose
  private Set<Vianda> viandas;

  @Setter @Expose
  @Enumerated(EnumType.STRING)
  @Column(name="estadoHeladera", nullable = false)
  private EstadoHeladera estado;

  @Setter @Expose
  @Column(name="temperaturaEsperada")
  private float temperaturaEsperada;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name="id_heladera", referencedColumnName = "id")
  private List<CambioEstado> historialEstados;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name="id_heladera", referencedColumnName = "id")
  private List<CambioTemperatura> historialTemperaturas;

  @OneToMany
  @JoinColumn(name="id_heladera", referencedColumnName = "id")
  private List<SolicitudApertura> solicitudesDeApertura;

  @Transient
  private GestorSuscripciones gestorSuscripciones;

  @Transient
  private static int minutosMargenFallaConexion = 7;

  public Heladera() {
    this.viandas = new HashSet<>();
    this.historialEstados = new ArrayList<>();
    this.estado = EstadoHeladera.ACTIVA;
    this.historialTemperaturas = new ArrayList<>();
    this.solicitudesDeApertura = new ArrayList<>();
    // this.incidentes = new ArrayList<>();
    this.gestorSuscripciones = new GestorSuscripciones();
  }

  public void ingresarViandas(List<Vianda> viandas) {
    if (this.cantidadViandasIngresadasVirtualmente() + this.cantidadViandas() + viandas.size() == this.capacidadMaximaViandas) {
      throw new HeladeraVirtualmenteLlenaException();
    }

    this.viandas.addAll(viandas);
    this.avisoGestorParaNotificarCantidades();
  }

  public void quitarViandas(List<Vianda> viandas) {
    if (this.cantidadViandasQuitadasVirtualmente() + viandas.size() == this.cantidadViandas()) {
      throw new HeladeraVirtualmenteVaciaException();
    }

    this.viandas.removeAll(viandas);
    this.avisoGestorParaNotificarCantidades();
  }

  private int calcularMesesActiva() {
    int mesesActiva = 0;
    LocalDate fechaInicio = fechaRegistro.toLocalDate();

    for (CambioEstado cambio : historialEstados) {
      if (cambio.getEstado() == EstadoHeladera.ACTIVA && fechaInicio.isBefore(cambio.getFechaCambio())) {
        mesesActiva += Period.between(LocalDate.from(fechaInicio), cambio.getFechaCambio()).getMonths();
        fechaInicio = cambio.getFechaCambio();
      } else if (cambio.getEstado() != EstadoHeladera.ACTIVA && fechaInicio.isBefore(cambio.getFechaCambio())) {
        fechaInicio = cambio.getFechaCambio();
      }
    }

    if (estado == EstadoHeladera.ACTIVA) {
      mesesActiva += Period.between(LocalDate.from(fechaInicio), LocalDate.now()).getMonths();
    }

    return mesesActiva;
  }

  public float calcularPuntaje() {
    float coeficiente = ReconocimientoTrabajoRealizado.getInstance().obtenerCoeficientes("coeficienteCantidadHeladerasActivas");
    return coeficiente * this.calcularMesesActiva();
  }

  public TipoContribucion obtenerTipoContribucion() {
    return TipoContribucion.ENCARGARSE_DE_HELADERA;
  }

  public LocalDate obtenerFechaRegistro() {
    return this.fechaRegistro.toLocalDate();
  }

  public void cambiarEstado(CambioEstado cambioEstado) {
    if (this.estado != cambioEstado.getEstado()) {
      this.estado = cambioEstado.getEstado();
      this.agregarCambioDeEstado(cambioEstado);
    }
  }

  private void agregarCambioDeEstado(CambioEstado cambioEstado) {
    this.historialEstados.add(cambioEstado);
  }

  public boolean temperaturaEnRango(float temperatura) {
    return temperatura >= modelo.getTemperaturaMinima() && temperatura <= modelo.getTemperaturaMaxima();
  }

  public void cambiarTemperatura(float nuevaTemperatura) {
    if (!temperaturaEnRango(nuevaTemperatura)) {
      this.cambiarEstado(new CambioEstado(EstadoHeladera.FALLA_TEMPERATURA, LocalDate.now()));
    }
    agregarTemperaturaAlHistorial(new CambioTemperatura(LocalDateTime.now(), nuevaTemperatura));
  }

  private void agregarTemperaturaAlHistorial(CambioTemperatura temperatura) {
    if(this.historialTemperaturas == null){
      this.historialTemperaturas = new ArrayList<>();
    }
    this.historialTemperaturas.add(temperatura);
  }

  public boolean estaActiva() {
    return this.estado == EstadoHeladera.ACTIVA;
  }

  public boolean validarApertura(Tarjeta tarjeta) {
    return this.solicitudesDeApertura.stream().anyMatch(sol -> sol.esValida(tarjeta));
  }

  public void agregarSolicitudApertura(SolicitudApertura solicitud) {
    if (!this.estaActiva()) {
      throw new HeladeraInactivaException();
    }
    // FIXME: Habria que separarlo en distintos metodos
    if (this.cantidadViandasIngresadasVirtualmente() + this.cantidadViandas() + solicitud.getCantidadViandas() >= this.capacidadMaximaViandas
        && solicitud.getAccion() == AccionApertura.INGRESAR_VIANDA) {
      throw new HeladeraVirtualmenteLlenaException();
    }

    if (this.cantidadViandasQuitadasVirtualmente() + solicitud.getCantidadViandas() >= this.cantidadViandas()
        && solicitud.getAccion() == AccionApertura.QUITAR_VIANDA) {
      throw new HeladeraVirtualmenteVaciaException();
    }

    this.solicitudesDeApertura.add(solicitud);
    // TODO: levantar mqtt
    /*PublicadorSolicitudApertura
        .getInstance()
        .publicarSolicitudApertura(solicitud.getTarjeta().getCodigo(), solicitud.getFechaSolicitud(), this.id);
        */
  }

  public int cantidadViandas() {
    return this.cantidadViandasIngresadas() - this.cantidadViandasRetiradas();
    //return this.viandas.size(); //FIXME hay que ver qué hacer con esto
  }

  public int cantidadViandasIngresadas(){
    Stream<SolicitudApertura> ingresadas = this.solicitudesDeApertura.stream().filter(SolicitudApertura::esIngresadaRealmente);
    return ingresadas.mapToInt(SolicitudApertura::getCantidadViandas).sum();
  }

  public int cantidadViandasRetiradas(){
    Stream<SolicitudApertura> quitadas = this.solicitudesDeApertura.stream().filter(SolicitudApertura::esQuitadaRealmente);
    return quitadas.mapToInt(SolicitudApertura::getCantidadViandas).sum();
  }

  public int cantidadViandasVirtuales() {
    return this.cantidadViandas() + this.cantidadViandasIngresadasVirtualmente() - this.cantidadViandasQuitadasVirtualmente();
  }

  public int cantidadViandasQuitadasVirtualmente() {
    Stream<SolicitudApertura> quitadas = this.solicitudesDeApertura.stream().filter(SolicitudApertura::esQuitadaVirtualmente);
    return quitadas.mapToInt(SolicitudApertura::getCantidadViandas).sum();
  }

  public int cantidadViandasIngresadasVirtualmente() {
    Stream<SolicitudApertura> ingresadas = this.solicitudesDeApertura.stream().filter(SolicitudApertura::esIngresadaVirtualmente);
    return ingresadas.mapToInt(SolicitudApertura::getCantidadViandas).sum();
  }

  public void recibirAlertaFraude() {
    this.setEstado(EstadoHeladera.FRAUDE);
    this.agregarCambioDeEstado(new CambioEstado(EstadoHeladera.FRAUDE, LocalDate.now()));

    gestorSuscripciones.notificar(TipoSuscripcion.DESPERFECTO, this);
  }

  public void detectarFallaDeConexion() {
    boolean huboFallaDesconexion = historialTemperaturas
        .get(historialTemperaturas.size() - 1)
        .getFecha()
        .plusMinutes(Heladera.minutosMargenFallaConexion)
        .isBefore(LocalDateTime.now());

    if (huboFallaDesconexion) {
      gestorSuscripciones.notificar(TipoSuscripcion.DESPERFECTO, this);
    }
  }
  
  /*public void agregarIncidente(Incidente incidente) {
    this.incidentes.add(incidente);
  }*/

  public void quitarVianda(Vianda vianda) {
    if (this.cantidadViandasQuitadasVirtualmente() == this.cantidadViandas()) {
      throw new HeladeraVirtualmenteVaciaException();
    }

    this.viandas.remove(vianda);
    this.avisoGestorParaNotificarCantidades();
  }

  public void ingresarVianda(Vianda vianda) {
    if (this.cantidadViandasIngresadasVirtualmente() + this.cantidadViandas() == this.capacidadMaximaViandas) {
      throw new HeladeraVirtualmenteLlenaException();
    }
    
    this.viandas.add(vianda);
    this.avisoGestorParaNotificarCantidades();
  }

  private void avisoGestorParaNotificarCantidades() {
    gestorSuscripciones.notificar(TipoSuscripcion.QUEDAN_N_VIANDAS, this);
    gestorSuscripciones.notificar(TipoSuscripcion.FALTAN_N_VIANDAS, this);
  }

  public static Heladera fromDTO(HeladeraDTO dto) {
    validarCamposObligatorios(dto);
    validarRangoTemperaturaEsperada(dto);

    Direccion direccion = Direccion.fromCoordenada(dto.getLatitud(), dto.getLongitud());
    HeladeraBuilder heladeraBuilder = Heladera.builder()
        .id(dto.getId())
        .nombre(dto.getNombre())
        .capacidadMaximaViandas(dto.getCapacidadMaximaViandas())
        .direccion(direccion);

    if (dto.getTemperaturaEsperada() != null) {
      heladeraBuilder.temperaturaEsperada(dto.getTemperaturaEsperada());
    }

    return heladeraBuilder.build();
  }

  private static void validarCamposObligatorios(HeladeraDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre", dto.getNombre()),
        Pair.of("capacidad máxima de viandas", String.valueOf(dto.getCapacidadMaximaViandas()))
    );

    if (dto.getCapacidadMaximaViandas() <= 0) {
      throw new ValidacionFormularioException("La capacidad máxima de viandas debe ser mayor a cero.");
    }
  }

  private static void validarRangoTemperaturaEsperada(HeladeraDTO dto) {
    if (dto.getTemperaturaEsperada() != null) {
      if (dto.getTemperaturaEsperada() < dto.getTemperaturaMinima() || dto.getTemperaturaEsperada() > dto.getTemperaturaMaxima()) {
        throw new ValidacionFormularioException("La temperatura esperada debe estar dentro del rango mínimo y máximo permitido para el modelo.");
      }
    }
  }
}

