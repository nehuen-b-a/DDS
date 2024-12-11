package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import io.javalin.http.Context;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
public class IncidenteDTO implements DTO {
  private Long id;
  private Long heladeraId;
  private Long usuarioId;
  private String tipoAlerta;
  private String descripcionDelColaborador;

  public IncidenteDTO(Incidente incidente) {
    this.id = incidente.getId();
    this.heladeraId = incidente.getHeladera().getId();
    this.usuarioId = incidente.getColaborador().getId();
    this.tipoAlerta = incidente.getTipoAlerta() != null ? incidente.getTipoAlerta().name() : null;
    this.descripcionDelColaborador = incidente.getDescripcionDelColaborador();
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.heladeraId = Long.parseLong(context.pathParam("heladeraId"));
    this.tipoAlerta = context.formParam("tipoFalla");
    this.usuarioId = context.sessionAttribute("id");
    this.descripcionDelColaborador = context.formParam("descripcion");
  }

  @Override
  public boolean equals(Object obj) {
    IncidenteDTO that = (IncidenteDTO) obj;
    return Objects.equals(heladeraId, that.heladeraId)
        && Objects.equals(usuarioId, that.usuarioId)
        && Objects.equals(tipoAlerta, that.tipoAlerta)
        && Objects.equals(descripcionDelColaborador, that.descripcionDelColaborador);
  }

  @Override
  public int hashCode() {
    return Objects.hash(heladeraId, usuarioId, tipoAlerta, descripcionDelColaborador);
  }
}