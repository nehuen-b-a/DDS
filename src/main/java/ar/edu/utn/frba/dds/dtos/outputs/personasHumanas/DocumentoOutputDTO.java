package ar.edu.utn.frba.dds.dtos.outputs.personasHumanas;

import lombok.Data;

@Data
public class DocumentoOutputDTO {
  Long id;
  String tipoDocumento;
  String nroDocumento;
}
