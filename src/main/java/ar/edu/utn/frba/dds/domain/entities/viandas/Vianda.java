package ar.edu.utn.frba.dds.domain.entities.viandas;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;

import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.dtos.ViandaDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vianda")
public class Vianda implements Contribucion {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "fechaCaducidad", columnDefinition = "DATE")
  private LocalDate fechaCaducidad;

  @Column(name = "entregada", columnDefinition = "BIT(1)")
  private boolean entregada;

  @Column(name = "comida", nullable = false)
  private String comida;

  @Column(name = "caloriasEnKcal", columnDefinition = "DECIMAL(5,2)")
  private float caloriasEnKcal;

  @Column(name = "pesoEnGramos", columnDefinition = "DECIMAL(5,2)")
  private float pesoEnGramos;

  @Column(name = "fechaDonacion", columnDefinition = "DATE", nullable = false)
  private LocalDate fechaDonacion;

  @ManyToOne
  @JoinColumn(name = "personaHumana_id", referencedColumnName = "id", nullable = false)
  private PersonaHumana personaHumana;

  @ManyToOne
  @JoinColumn(name = "heladera_id", referencedColumnName = "id", nullable = false)
  private Heladera heladera;


  public Vianda(LocalDate fechaCaducidad, boolean entregada, String comida, float calorias, float pesoEnGramos, LocalDate fechaDonacion) {
    this.fechaCaducidad = fechaCaducidad;
    this.entregada = entregada;
    this.comida = comida;
    this.caloriasEnKcal = calorias;
    this.pesoEnGramos = pesoEnGramos;
    this.fechaDonacion = fechaDonacion;
  }

  public Vianda(LocalDate fechaDonacion) {
    this.fechaDonacion = fechaDonacion;
    this.entregada = true;
  }

  public float calcularPuntaje() {
    return ReconocimientoTrabajoRealizado.getInstance().obtenerCoeficientes("coeficienteViandasDonadas");
  }

  public TipoContribucion obtenerTipoContribucion() {
    return TipoContribucion.DONACION_VIANDA;
  }

  public LocalDate obtenerFechaRegistro() {
    return this.fechaDonacion;
  }

  public boolean estaVencida() {
    return fechaCaducidad.isBefore(LocalDate.now());
  }

  // TODO: This class overrides "equals()" and should therefore also override "hashCode()".
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Vianda)) {
      return false;
    }

    Vianda vianda = (Vianda) o;

    return this.fechaDonacion.equals(vianda.fechaDonacion);
  }

  public static Vianda fromDTO(ViandaDTO dto) {
    validarCamposObligatorios(dto);
    validarCalorias(dto);
    validarPeso(dto);

    Vianda.ViandaBuilder builder = Vianda.builder()
        .fechaCaducidad(dto.getFechaCaducidad())
        .entregada(dto.isEntregada())
        .comida(dto.getComida())
        .fechaDonacion(dto.getFechaDonacion());

    if (dto.getCaloriasEnKcal() != null && !dto.getCaloriasEnKcal().isEmpty()) {
      builder.caloriasEnKcal(Float.parseFloat(dto.getCaloriasEnKcal()));
    }

    if (dto.getPesoEnGramos() != null && !dto.getPesoEnGramos().isEmpty()) {
      builder.pesoEnGramos(Float.parseFloat(dto.getPesoEnGramos()));
    }

    return builder.build();
  }

  public void actualizarFromDto(ViandaDTO dto) {
    validarCamposObligatorios(dto);
    validarCalorias(dto);
    validarPeso(dto);

    this.fechaCaducidad = dto.getFechaCaducidad();
    this.entregada = dto.isEntregada();
    this.comida = dto.getComida();
    if (dto.getCaloriasEnKcal() != null && !dto.getCaloriasEnKcal().isEmpty()) {
      this.caloriasEnKcal = (Float.parseFloat(dto.getCaloriasEnKcal()));
    }
    if (dto.getPesoEnGramos() != null && !dto.getPesoEnGramos().isEmpty()) {
      this.pesoEnGramos = (Float.parseFloat(dto.getPesoEnGramos()));
    }
    this.fechaDonacion = dto.getFechaDonacion();
  }

  private static void validarCamposObligatorios(ViandaDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("comida", dto.getComida()),
        Pair.of("fechaCaducidad", dto.getFechaCaducidad()),
        Pair.of("heladera", dto.getHeladeraId())
    );
  }

  private static void validarCalorias(ViandaDTO dto) {
    if (dto.getCaloriasEnKcal() != null && Float.parseFloat(dto.getCaloriasEnKcal()) <= 0) {
      throw new ValidacionFormularioException("Las calorías deben ser mayores que cero.");
    }
  }

  private static void validarPeso(ViandaDTO dto) {
    if (dto.getCaloriasEnKcal() != null &&  Float.parseFloat(dto.getPesoEnGramos()) <= 0) {
      throw new ValidacionFormularioException("El peso debe ser mayor que cero.");
    }
  }
}
