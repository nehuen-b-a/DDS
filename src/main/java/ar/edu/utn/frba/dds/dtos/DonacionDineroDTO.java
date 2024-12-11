package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.donacionesDinero.DonacionDinero;
import io.javalin.http.Context;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class DonacionDineroDTO implements DTO {
  private Long id;
  private String monto;
  private String unidadFrecuencia;
  private LocalDate fecha;
  private Long personaHumanaId;
  private Long personaJuridicaId;

  public DonacionDineroDTO(DonacionDinero donacion) {
    this.id = donacion.getId();
    this.monto = String.valueOf(donacion.getMonto());
    this.unidadFrecuencia = donacion.getUnidadFrecuencia().name();
    this.fecha = donacion.getFecha();
    this.personaHumanaId = donacion.getPersonaHumana() != null ? donacion.getPersonaHumana().getId() : null;
    this.personaJuridicaId = donacion.getPersonaJuridica() != null ? donacion.getPersonaJuridica().getId() : null;
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.setMonto(context.formParam("monto"));
    this.setUnidadFrecuencia(context.formParam("unidadFrecuencia"));
    this.setFecha(LocalDate.now()); /*.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))*/
  }

  @Override
  public boolean equals(Object obj) {
    DonacionDineroDTO that = (DonacionDineroDTO) obj;
    return Objects.equals(monto, that.monto) &&
        Objects.equals(unidadFrecuencia, that.unidadFrecuencia) &&
        Objects.equals(personaHumanaId, that.personaHumanaId) &&
        Objects.equals(personaJuridicaId, that.personaJuridicaId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(monto, unidadFrecuencia, fecha, personaHumanaId, personaJuridicaId);
  }
}
