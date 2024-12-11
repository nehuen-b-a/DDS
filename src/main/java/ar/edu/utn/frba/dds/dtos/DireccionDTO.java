package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import io.javalin.http.Context;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DireccionDTO {
  private String calle;
  private String altura;
  private String provincia;
  private String municipio;
  private boolean obligatoria;

  public DireccionDTO(Direccion direccion) {
    if (direccion != null) {
      this.setCalle(direccion.getCalle());
      this.setAltura(direccion.getAltura());
      this.setProvincia(direccion.getProvincia());
      this.setMunicipio(direccion.getMunicipio());
    }
  }

  public void obtenerFormulario(Context context) {
    this.setCalle(context.formParam("calle"));
    this.setAltura(context.formParam("altura"));
    this.setProvincia(context.formParam("provincia"));
    this.setMunicipio(context.formParam("municipio"));
  }
}
