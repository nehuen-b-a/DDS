package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import io.javalin.http.Context;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DocumentoDTO implements DTO {
  private String tipoDocumento;
  private String nroDocumento;

  public DocumentoDTO(Documento documento) {
    this.tipoDocumento = documento.getTipo().name();
    this.nroDocumento = documento.getNumero();
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.setNroDocumento(context.formParam("nroDocumento"));
    this.setTipoDocumento(context.formParam("tipoDocumento"));
  }
}
