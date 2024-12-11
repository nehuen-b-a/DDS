package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Suscripcion;
import io.javalin.http.Context;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter @Setter
@Data
@NoArgsConstructor
public class SuscripcionDTO implements DTO {
  private Long id;
  private Boolean desperfecto;
  private Integer cantidadViandasFaltantes;
  private Integer cantidadViandasQueQuedan;

  public SuscripcionDTO(Suscripcion suscripcion) {
    this.id = suscripcion.getId();
    // this.desperfecto = suscripcion.get
    this.cantidadViandasFaltantes = suscripcion.getHeladera().getCapacidadMaximaViandas() - suscripcion.getHeladera().cantidadViandasVirtuales();
    this.cantidadViandasQueQuedan = suscripcion.getHeladera().cantidadViandas();
  }

  @Override
  public void obtenerFormulario(Context context) {
    // Comprobar el parámetro "desperfecto" y asignar a la variable booleana
    String desperfectoParam = context.formParam("desperfecto");
    this.desperfecto = "true".equals(desperfectoParam);

    // Manejo de cantidadViandasQueQuedan
    String quedanNViandasParam = context.formParam("quedanNViandas");
    this.cantidadViandasQueQuedan = parseCantidad(quedanNViandasParam);

    // Manejo de cantidadViandasFaltantes
    String faltanNViandasParam = context.formParam("faltanNViandas");
    this.cantidadViandasFaltantes = parseCantidad(faltanNViandasParam);
  }

  private Integer parseCantidad(String param) {
    if (param == null || param.isEmpty()) {
      return null;
    }
    try {
      return Integer.parseInt(param);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SuscripcionDTO that = (SuscripcionDTO) obj;
    return Objects.equals(id, that.id)
        && Objects.equals(cantidadViandasFaltantes, that.cantidadViandasFaltantes)
        && Objects.equals(cantidadViandasQueQuedan, that.cantidadViandasQueQuedan);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, cantidadViandasFaltantes, cantidadViandasQueQuedan);
  }
}