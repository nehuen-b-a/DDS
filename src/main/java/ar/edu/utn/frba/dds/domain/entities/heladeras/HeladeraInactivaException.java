package ar.edu.utn.frba.dds.domain.entities.heladeras;

public class HeladeraInactivaException extends RuntimeException {
  public HeladeraInactivaException() {
    super("La heladera que se quiere usar se halla inactiva.");
  }

}
