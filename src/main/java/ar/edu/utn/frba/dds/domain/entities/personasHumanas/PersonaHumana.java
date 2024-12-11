package ar.edu.utn.frba.dds.domain.entities.personasHumanas;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.contacto.Contacto;
import ar.edu.utn.frba.dds.domain.entities.contacto.IObserverNotificacion;
import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;
import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.oferta.OfertaCanjeada;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario.Respuesta;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario.RespuestaNoValidaException;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.UsoDeTarjeta;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.dtos.PersonaHumanaDTO;
import ar.edu.utn.frba.dds.exceptions.SinTarjetaException;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import org.apache.commons.lang3.tuple.Pair;

@Entity
@Table(name = "persona_humana")
@Builder
@AllArgsConstructor
public class PersonaHumana extends IObserverNotificacion {
  @Id @GeneratedValue
  @Getter @Setter
  private Long id;

  @Getter @Setter
  @OneToOne
  @JoinColumn(name = "usuario_id", referencedColumnName = "id")
  private Usuario usuario;

  @Getter @Setter
  @Embedded
  private Documento documento;

  @Getter @Setter
  @Embedded
  private Contacto contacto;

  @Getter @Setter
  @Embedded
  private Direccion direccion;

  @Getter @Setter
  @Column(name="nombre", nullable = false)
  private String nombre;

  @Getter @Setter
  @Column(name="apellido", nullable = false)
  private String apellido;

  @Getter @Setter
  @Column(name = "fechaNacimiento", columnDefinition = "DATE")
  private LocalDate fechaNacimiento;

  @Getter
  @Enumerated(EnumType.STRING)
  @ElementCollection()
  @CollectionTable(name = "formas_contribucion_humana",
      joinColumns = @JoinColumn(name = "personaHumana_id",
          referencedColumnName = "id"))
  @Column(name = "contribucionesElegidas", nullable = false)
  private Set<FormasContribucionHumanas> contribucionesElegidas;

  @Getter
  @Transient
  private Set<Contribucion> contribuciones;

  @Getter
  @OneToMany
  @JoinColumn(name = "personaHumana_id", referencedColumnName = "id")
  private Set<OfertaCanjeada> ofertasCanjeadas;

  @Getter
  @OneToMany
  @JoinColumn(name = "id_colaborador_repartidor", referencedColumnName = "id")
  private List<Tarjeta> tarjetasSinEntregar;

  @Getter
  @Transient
  private List<Respuesta> formulario;

  @Getter
  @OneToMany
  @JoinColumn(name = "id_colaborador_donador", referencedColumnName = "id")
  private List<Tarjeta> tarjetasColaboracion;

  @Getter
  @OneToOne
  @JoinColumn(name = "tarjeta_id", referencedColumnName = "id")
  private Tarjeta tarjetaEnUso;

  @Setter
  @Column(name="puntajeActual")
  private Float puntajeActual;

  public PersonaHumana() {
    this.contribucionesElegidas = new HashSet<>();
    this.contribuciones = new HashSet<>();
    this.ofertasCanjeadas = new HashSet<>();
    this.tarjetasSinEntregar = new ArrayList<>();
    this.formulario = new ArrayList<>();
  }

  public float puntosGastados() {
    float sum = 0;
    for (OfertaCanjeada ofertaCanjeada : ofertasCanjeadas) {
      sum += ofertaCanjeada.getOferta().getCantidadPuntosNecesarios();
    }
    return sum;
  }

  public float calcularPuntajeNeto() {
    ReconocimientoTrabajoRealizado reconocimientoTrabajoRealizado = ReconocimientoTrabajoRealizado.getInstance();
    return reconocimientoTrabajoRealizado.calcularPuntaje(this.getContribuciones(), this.puntosGastados());
  }

  public void agregarContribucion(Contribucion contribucion) {
    contribuciones.add(contribucion);
  }

  public void agregarOfertaCanjeada(OfertaCanjeada ofertaCanjeada) {
    ofertasCanjeadas.add(ofertaCanjeada);
  }

  public void agregarTarjetaSinEntregar(Tarjeta tarjeta) {
    if (this.direccion == null) {
      throw new DireccionIncompletaException();
    }
    this.tarjetasSinEntregar.add(tarjeta);
  }

  public void agregarRespuestaAlFormulario(Respuesta respuesta) {
    if (!respuesta.getPregunta().esValida(respuesta.getContenido())) {
      throw new RespuestaNoValidaException();
    }
    this.formulario.add(respuesta);
  }

  public void agregarFormaDeContribucion(FormasContribucionHumanas forma) {
    this.contribucionesElegidas.add(forma);
  }

  public void quitarFormaDeContribucion(FormasContribucionHumanas forma) {
    this.contribucionesElegidas.remove(forma);
  }

  public void serNotificadoPor(Mensaje mensaje) {
    try {
      contacto.enviarMensaje(mensaje);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void asignarTarjetaParaColaborar(Tarjeta tarjeta) {
    if (this.direccion == null) {
      throw new DireccionIncompletaException();
    }
    if (this.tarjetasColaboracion == null) {
      this.tarjetasColaboracion = new ArrayList<>();
    }

    darTarjetaDeBaja();
    this.tarjetasColaboracion.add(tarjeta);
    this.tarjetaEnUso = tarjeta;
  }

  public void darTarjetaDeBaja(){
    if (this.tarjetaEnUso != null) {
      this.tarjetaEnUso.setFechaBaja(LocalDate.now());
    }
    this.tarjetaEnUso = null;
  }

  public void usarTarjeta(Heladera heladera){
    if (!heladera.validarApertura(this.tarjetaEnUso)) {
      throw new NoHaySolicitudActivaException();
    }

    this.tarjetaEnUso.agregarUso(new UsoDeTarjeta(LocalDateTime.now(), heladera));
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof PersonaHumana persona)) {
      return false;
    }

    // comparar distribuciones
    boolean mismasContribuciones = this.contribuciones.size() == persona.contribuciones.size();
    if (mismasContribuciones) {
      mismasContribuciones =
          this.contribuciones.stream().allMatch(contribucion1 ->
            persona.contribuciones.stream().anyMatch(contribucion2 ->
                contribucion2.equals(contribucion1)));
    }

    return
        mismasContribuciones
        && this.nombre.equals(persona.nombre)
        && this.apellido.equals(persona.apellido)
        && this.documento.equals(persona.documento)
        && this.fechaNacimiento.isEqual(persona.fechaNacimiento)
        && this.contacto.equals(persona.contacto);
  }

  public static PersonaHumana fromDTO(PersonaHumanaDTO dto) {
    validarCamposObligatorios(dto);
    validarLongitudNombreYApellido(dto);
    validarFechaNacimiento(dto);
    validarFormasContribucion(dto);

    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Contacto contacto = Contacto.fromDTO(dto.getContactoDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO());

    return PersonaHumana.builder()
        .nombre(dto.getNombre())
        .apellido(dto.getApellido())
        .fechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()))
        .direccion(direccion)
        .contacto(contacto)
        .documento(documento)
        .puntajeActual(0f)
        .contribucionesElegidas(dto.getContribucionesElegidas())
        .build();
  }

  private static void validarCamposObligatorios(PersonaHumanaDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre", dto.getNombre()),
        Pair.of("apellido", dto.getApellido()),
        Pair.of("fecha de nacimiento", dto.getFechaNacimiento())
    );
  }

  private static void validarLongitudNombreYApellido(PersonaHumanaDTO dto) {
    if (dto.getNombre().length() < 2 || dto.getNombre().length() > 50) {
      throw new ValidacionFormularioException("El nombre debe tener entre 2 y 50 caracteres.");
    }
    if (dto.getApellido().length() < 2 || dto.getApellido().length() > 50) {
      throw new ValidacionFormularioException("El apellido debe tener entre 2 y 50 caracteres.");
    }
  }

  private static void validarFechaNacimiento(PersonaHumanaDTO dto) {
    try {
      LocalDate.parse(dto.getFechaNacimiento());
    } catch (DateTimeParseException e) {
      throw new ValidacionFormularioException("Formato de fecha incorrecto. Debe ser yyyy-MM-dd.");
    }
  }

  private static void validarFormasContribucion(PersonaHumanaDTO dto) {
    if (!(dto.isDonacionDinero() || dto.isRedistribucionViandas() || dto.isDonacionViandas() || dto.isEntregaTarjetas())) {
      throw new ValidacionFormularioException("Al menos una forma de contribución debe ser seleccionada.");
    }
    if(!dto.isDonacionDinero()){
      dto.getDireccionDTO().setObligatoria(true);
    }
  }

  public void actualizarFromDto(PersonaHumanaDTO dto) {
    validarCamposObligatorios(dto);
    validarLongitudNombreYApellido(dto);
    validarFechaNacimiento(dto);
    validarFormasContribucion(dto);

    this.nombre = dto.getNombre();
    System.out.println(dto.getNombre());
    this.apellido = dto.getApellido();
    this.fechaNacimiento = LocalDate.parse(dto.getFechaNacimiento());

    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO());
    Contacto contacto = Contacto.fromDTO(dto.getContactoDTO());

    if (this.direccion == null || !this.direccion.equals(direccion)) {
      this.setDireccion(direccion);
    }

    if (this.documento == null || !this.documento.equals(documento)) {
      this.setDocumento(documento);
    }

    if (this.contacto == null || !this.contacto.equals(contacto)) {
      this.setContacto(contacto);
    }

    if (this.contribucionesElegidas == null || !this.contribucionesElegidas.equals(dto.getContribucionesElegidas())) {
      contribucionesElegidas.addAll(dto.getContribucionesElegidas());
    }
  }

  public void sumarPuntaje(Float puntaje) {
    if (this.puntajeActual == null) {
      this.puntajeActual = 0f;
    }
    this.puntajeActual += puntaje;
  }
  public Float getPuntajeActual() {
    if (this.puntajeActual == null) {
      return 0F;
    }
    return this.puntajeActual;
  }

  public void agregarTarjetaEntregada(Tarjeta tarjeta) {
    this.tarjetasColaboracion.add(tarjeta);
  }
}
