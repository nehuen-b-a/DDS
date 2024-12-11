package ar.edu.utn.frba.dds.dtos;

import io.javalin.http.Context;
import lombok.Data;

@Data
public class UsuarioDTO implements DTO {
  private String nombre;
  private String clave;
  private String claveRepetida;
  private String rol;

  @Override
  public void obtenerFormulario(Context context) {
    this.setNombre(context.formParam("usuario"));
    this.setClave(context.formParam("clave"));
    this.setClaveRepetida(context.formParam("claveRepetida"));
    this.setRol(context.sessionAttribute("tipoCuenta"));
  }
}
