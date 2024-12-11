package ar.edu.utn.frba.dds.domain.entities.personasVulnerables;

import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.HeladeraInactivaException;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.UsoDeTarjeta;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.UsoMaximoDeTarjetasPorDiaExcedidoException;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.dtos.PersonaVulnerableDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "persona_vulnerable")
public class PersonaVulnerable {

  @Id @GeneratedValue
  private long id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  @Column(name = "apellido", nullable = false)
  private String apellido;

  @Column(name = "fechaDeNacimiento", columnDefinition = "DATE", nullable = false)
  private LocalDate fechaDeNacimiento;

  @Column(name = "fechaDeRegistro", columnDefinition = "DATE", nullable = false)
  private LocalDate fechaDeRegistro;

  @Embedded
  private Direccion direccion;

  @Column(name="menoresAcargo")
  private Integer menoresAcargo;

  @Embedded
  private Documento documento;

  @ManyToOne
  @JoinColumn(name = "personaQueLoRegistro_id", referencedColumnName = "id", nullable = false)
  private PersonaHumana personaQueLoRegistro;

  @OneToMany
  @JoinColumn(name = "personaVulnerable_id", referencedColumnName = "id")
  private List<Tarjeta> tarjetas;

  @OneToOne
  @JoinColumn(name = "tarjeta_id", referencedColumnName = "id")
  private Tarjeta tarjetaEnUso;

  public void usarTarjeta(Heladera heladera, Vianda vianda) throws UsoMaximoDeTarjetasPorDiaExcedidoException, HeladeraInactivaException {
    // FIXME: Revisar si es correcto que este metodo reciba la vianda
    if (!heladera.estaActiva()) {
      throw new HeladeraInactivaException();
    }

    LocalDate hoy = LocalDate.now();
    int usosHoy = this.tarjetaEnUso.cantidadDeUsos(hoy);
    int maxUsosPermitidos = 4 + (2 * menoresAcargo);

    if (usosHoy < maxUsosPermitidos) {
      heladera.quitarVianda(vianda);
      tarjetaEnUso.agregarUso(new UsoDeTarjeta(LocalDateTime.now(), heladera));
    } else {
      throw new UsoMaximoDeTarjetasPorDiaExcedidoException();
    }
  }

  public void asignarTarjeta(Tarjeta tarjeta){
    darTarjetaDeBaja();
    this.tarjetas.add(tarjeta);
    this.tarjetaEnUso = tarjeta;
  }

  public void darTarjetaDeBaja(){
    if (this.tarjetaEnUso != null) {
      this.tarjetaEnUso.setFechaBaja(LocalDate.now());
    }
    this.tarjetaEnUso = null;
  }

  public static PersonaVulnerable fromDTO(PersonaVulnerableDTO dto) {
    validarCamposObligatorios(dto);
    validarFechaDeNacimiento(dto);

    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO(), false);

    return PersonaVulnerable.builder()
        .nombre(dto.getNombre())
        .apellido(dto.getApellido())
        .fechaDeNacimiento(LocalDate.parse(dto.getFechaDeNacimiento()))
        .menoresAcargo(obtenerMenoresACargo(dto))
        .documento(documento)
        .direccion(direccion)
        .build();
  }

  private static void validarCamposObligatorios(PersonaVulnerableDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre", dto.getNombre()),
        Pair.of("apellido", dto.getApellido()),
        Pair.of("fecha de nacimiento", dto.getFechaDeNacimiento())
    );

    if (dto.getNombre().isEmpty() || dto.getApellido().isEmpty() || dto.getFechaDeNacimiento() == null) {
      throw new ValidacionFormularioException("Ciertos campos que son obligatorios se encuentran vacíos");
    }
  }

  private static void validarFechaDeNacimiento(PersonaVulnerableDTO dto) {
    LocalDate fechaNacimiento = LocalDate.parse(dto.getFechaDeNacimiento());
    if (fechaNacimiento.isAfter(LocalDate.now())) {
      throw new ValidacionFormularioException("Fecha de nacimiento inválida");
    }
  }

  private static int obtenerMenoresACargo(PersonaVulnerableDTO dto) {
    if (dto.getMenoresAcargo() == null || dto.getMenoresAcargo().isEmpty()) {
      return 0; // Si no hay valor, se asume que no hay menores a cargo
    }

    int menores;
    try {
      menores = Integer.parseInt(dto.getMenoresAcargo());
    } catch (NumberFormatException e) {
      throw new ValidacionFormularioException("El campo de menores a cargo debe ser un número válido");
    }

    if (menores < 0) {
      throw new ValidacionFormularioException("La cantidad de menores a cargo no puede ser negativa");
    }

    // Aquí puedes establecer un límite máximo, por ejemplo, 10.
    final int MAX_MENORES_ACARGO = 10;
    if (menores > MAX_MENORES_ACARGO) {
      throw new ValidacionFormularioException("La cantidad de menores a cargo no puede exceder " + MAX_MENORES_ACARGO);
    }

    return menores;
  }

  public void actualizarFromDto(PersonaVulnerableDTO dto) {
    validarCamposObligatorios(dto);
    validarFechaDeNacimiento(dto);

    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO(), false);

    if (!this.nombre.equals(dto.getNombre())) {
      this.setNombre(dto.getNombre());
    }

    if (!this.apellido.equals(dto.getApellido())) {
      this.setApellido(dto.getApellido());
    }

    if (this.direccion!=null && !this.direccion.equals(direccion)) {
      this.setDireccion(direccion);
    }

    if (this.documento!=null && !this.documento.equals(documento)) {
      this.setDocumento(documento);
    }

    LocalDate nuevaFechaDeNacimiento = LocalDate.parse(dto.getFechaDeNacimiento());
    if (!this.fechaDeNacimiento.equals(nuevaFechaDeNacimiento)) {
      this.setFechaDeNacimiento(nuevaFechaDeNacimiento);
    }

    int nuevosMenoresACargo = Integer.parseInt(dto.getMenoresAcargo());
    if (this.menoresAcargo != nuevosMenoresACargo) {
      this.setMenoresAcargo(nuevosMenoresACargo);
    }
  }

  public void agregarTarjeta(Tarjeta tarjeta) {
    if (this.tarjetas == null) {
      this.tarjetas = new ArrayList<>();
    }
    this.tarjetas.add(tarjeta);
    this.setTarjetaEnUso(tarjeta);
  }
}
