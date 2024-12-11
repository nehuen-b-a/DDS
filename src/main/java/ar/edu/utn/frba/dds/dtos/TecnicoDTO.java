package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import io.javalin.http.Context;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TecnicoDTO implements DTO {
  private String nombre;
  private String apellido;
  private String cuil;
  private Double radioMaximoParaSerAvisado;
  private DireccionDTO direccionDTO;
  private ContactoDTO contactoDTO;
  private DocumentoDTO documentoDTO;
  private Long id;

  public TecnicoDTO(Tecnico tecnico) {
    this.nombre = tecnico.getNombre();
    this.apellido = tecnico.getApellido();
    this.cuil = tecnico.getCuil();
    this.radioMaximoParaSerAvisado = tecnico.getDistanciaMaximaEnKmParaSerAvisado();
    this.direccionDTO = new DireccionDTO(tecnico.getDireccion());
    this.documentoDTO = new DocumentoDTO(tecnico.getDocumento());
    this.contactoDTO = new ContactoDTO(tecnico.getContacto());
    this.id = tecnico.getId();
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.setNombre(context.formParam("nombre"));
    this.setApellido(context.formParam("apellido"));
    this.setCuil(context.formParam("cuil"));
    this.setRadioMaximoParaSerAvisado(Double.parseDouble(Objects.requireNonNull(context.formParam("radio"))));
    this.direccionDTO = new DireccionDTO();
    this.direccionDTO.obtenerFormulario(context);
    this.contactoDTO = new ContactoDTO();
    this.contactoDTO.obtenerFormulario(context);
    this.documentoDTO = new DocumentoDTO();
    this.documentoDTO.obtenerFormulario(context);
  }
}

