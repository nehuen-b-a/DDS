package ar.edu.utn.frba.dds.domain.entities.heladeras;

public class HeladeraVirtualmenteVaciaException extends RuntimeException {
  public HeladeraVirtualmenteVaciaException() {
    super("La heladera esta virtualmente vacia. Las solicitudes de apertura indican que luego quedarian inconsistencias.");
  }
}