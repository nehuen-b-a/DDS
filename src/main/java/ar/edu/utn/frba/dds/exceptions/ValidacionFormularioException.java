package ar.edu.utn.frba.dds.exceptions;

import ar.edu.utn.frba.dds.dtos.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidacionFormularioException extends RuntimeException {
  public ValidacionFormularioException(String mensaje) {
    super(mensaje);
  }
}
