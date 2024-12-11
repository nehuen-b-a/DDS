package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;

public class RespuestaNoValidaException extends RuntimeException  {
  public RespuestaNoValidaException() {
    super("Se ha ingresado una respuesta no valida.");
  }
}
