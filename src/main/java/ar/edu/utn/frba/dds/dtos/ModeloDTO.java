package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Modelo;
import io.javalin.http.Context;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModeloDTO implements DTO {
  private String nombre;
  private String temperaturaMinima;
  private String temperaturaMaxima;

  public ModeloDTO(Modelo modelo) {
    this.setNombre(modelo.getModelo());
    this.setTemperaturaMinima(String.valueOf(modelo.getTemperaturaMinima()));
    this.setTemperaturaMaxima(String.valueOf(modelo.getTemperaturaMaxima()));
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.setNombre(context.formParam("nombre"));
    this.setTemperaturaMinima(context.formParam("temperaturaMinima"));
    this.setTemperaturaMaxima(context.formParam("temperaturaMaxima"));
  }
}
