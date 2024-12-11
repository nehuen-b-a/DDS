package ar.edu.utn.frba.dds.domain.entities.personasHumanas;
public class DireccionIncompletaException extends RuntimeException{
  public DireccionIncompletaException() {
    super("No se ha indicado una direccion.");
  }
}