package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import io.javalin.http.Context;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
public class ViandaDTO implements DTO {
  private Long id;
  private String comida;
  private String caloriasEnKcal;
  private String pesoEnGramos;
  private LocalDate fechaCaducidad;
  private LocalDate fechaDonacion;
  private boolean entregada;
  private Long personaHumanaId;
  private String heladeraNombre;
  private Long heladeraId;

  public ViandaDTO(Vianda vianda) {
    this.id = vianda.getId();
    this.comida = vianda.getComida();
    this.caloriasEnKcal = String.valueOf(vianda.getCaloriasEnKcal());
    this.pesoEnGramos = String.valueOf(vianda.getPesoEnGramos());
    this.fechaCaducidad = vianda.getFechaCaducidad() != null ? vianda.getFechaCaducidad() : null;
    this.entregada = vianda.isEntregada();
    this.fechaDonacion = vianda.getFechaDonacion();
    this.personaHumanaId = vianda.getPersonaHumana() != null ? vianda.getPersonaHumana().getId() : null;
    this.heladeraNombre = vianda.getHeladera().getNombre();
    this.heladeraId = vianda.getHeladera().getId();
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.comida = context.formParam("comida");
    this.entregada = false;
    this.caloriasEnKcal = context.formParam("caloriasEnKcal");
    this.pesoEnGramos = context.formParam("pesoEnGramos");
    this.fechaCaducidad = LocalDate.parse(context.formParam("fechaCaducidad"));
    this.fechaDonacion = LocalDate.now();
    this.heladeraNombre = context.formParam("heladeraNombre");
    this.heladeraId = context.formParam("heladeraId") != null ? Long.valueOf(context.formParam("heladeraId")) : 0L;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    ViandaDTO that = (ViandaDTO) obj;
    return that.caloriasEnKcal.equals(caloriasEnKcal) &&
        that.pesoEnGramos.equals(pesoEnGramos) &&
        entregada == that.entregada &&
        Objects.equals(comida, that.comida) &&
        Objects.equals(fechaCaducidad, that.fechaCaducidad) &&
        Objects.equals(fechaDonacion, that.fechaDonacion) &&
        Objects.equals(personaHumanaId, that.personaHumanaId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(comida, caloriasEnKcal, pesoEnGramos, fechaCaducidad, entregada, fechaDonacion, personaHumanaId);
  }
}
