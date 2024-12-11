package ar.edu.utn.frba.dds.domain.entities.heladeras;

public class HeladeraVirtualmenteLlenaException extends RuntimeException {
  public HeladeraVirtualmenteLlenaException() {
    super("La heladera esta virtualmente llena. Las solicitudes de apertura indican que luego quedarian inconsistencias.");
  }
}