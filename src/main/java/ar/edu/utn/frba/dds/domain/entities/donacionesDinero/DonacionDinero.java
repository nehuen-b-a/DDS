package ar.edu.utn.frba.dds.domain.entities.donacionesDinero;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.dtos.DonacionDineroDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Null;
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
@Table(name = "donacion_dinero")
public class DonacionDinero implements Contribucion {

  @Id  @GeneratedValue
  private Long id;

  @Column(name = "monto", nullable = false)
  private float monto;

  @Enumerated(EnumType.STRING)
  @Column(name = "unidadFrecuencia", nullable = false)
  private UnidadFrecuencia unidadFrecuencia;

  @Column(name = "fecha", columnDefinition = "DATE", nullable = false)
  private LocalDate fecha;

  @ManyToOne
  @JoinColumn(name = "id_personaHumana", referencedColumnName = "id")
  private PersonaHumana personaHumana;

  @ManyToOne
  @JoinColumn(name = "id_personaJuridica", referencedColumnName = "id")
  private PersonaJuridica personaJuridica;

  public float calcularPuntaje() {
    float coeficiente = ReconocimientoTrabajoRealizado.getInstance()
        .obtenerCoeficientes("coeficientePesosDonados");
    return monto * coeficiente;
  }

  public TipoContribucion obtenerTipoContribucion() {
    return TipoContribucion.DONACION_DINERO;
  }

  public LocalDate obtenerFechaRegistro() {
    return this.fecha;
  }

  public static DonacionDinero fromDTO(DonacionDineroDTO dto) {
    validarCamposObligatorios(dto);
    validarMonto(dto);

    return DonacionDinero.builder()
        .monto(Float.parseFloat(dto.getMonto()))
        .unidadFrecuencia(UnidadFrecuencia.valueOf(dto.getUnidadFrecuencia()))
        .fecha(dto.getFecha())
        .build();
  }

  public void actualizarFromDto(DonacionDineroDTO dto) {
    validarCamposObligatorios(dto);
    validarMonto(dto);

    this.monto = Float.parseFloat(dto.getMonto());
    this.unidadFrecuencia = UnidadFrecuencia.valueOf(dto.getUnidadFrecuencia());
    this.fecha = dto.getFecha();

  }

  private static void validarCamposObligatorios(DonacionDineroDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("monto", dto.getMonto()),
        Pair.of("unidad de frecuencia", dto.getUnidadFrecuencia())
    );
  }

  private static void validarMonto(DonacionDineroDTO dto) {
    if (Float.parseFloat(dto.getMonto()) <= 0) {
      throw new ValidacionFormularioException("El monto debe ser mayor que cero.");
    }
  }
}
