package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import io.javalin.http.Context;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class HeladeraDTO {
  private Long id;
  private String nombre;
  private String latitud;
  private String longitud;
  private int capacidadMaximaViandas;
  private String modelo;
  private float temperaturaMinima;
  private float temperaturaMaxima;
  private Float temperaturaEsperada;

  public HeladeraDTO(Heladera heladera) {
    this.id = heladera.getId();
    this.nombre = heladera.getNombre();
    this.capacidadMaximaViandas = heladera.getCapacidadMaximaViandas();
    this.temperaturaEsperada = heladera.getTemperaturaEsperada();
    this.latitud = heladera.getDireccion().getCoordenada().getLatitud();
    this.longitud = heladera.getDireccion().getCoordenada().getLongitud();
  }

  public void obtenerFormulario(Context context) {
    this.nombre = context.formParam("nombre");
    this.latitud = context.formParam("latitud");
    this.longitud = context.formParam("longitud");

    String capacidadParam = context.formParam("capacidadMaximaViandas");
    this.capacidadMaximaViandas = (capacidadParam != null && !capacidadParam.isEmpty())
        ? Integer.parseInt(capacidadParam)
        : 0;

    String temperaturaEsperadaParam = context.formParam("temperaturaEsperada");
    this.temperaturaEsperada = (temperaturaEsperadaParam != null && !temperaturaEsperadaParam.isEmpty())
        ? Float.parseFloat(temperaturaEsperadaParam)
        : null;
  }
}

