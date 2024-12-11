package ar.edu.utn.frba.dds.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensajeAmigableException extends RuntimeException {
  private int statusCode;
  public MensajeAmigableException(String mensaje, int statusCode) {
    super(mensaje);
    this.statusCode = statusCode;
  }
}
