package ar.edu.utn.frba.dds.domain.entities.personasHumanas;

public class NoHaySolicitudActivaException extends RuntimeException {
  public NoHaySolicitudActivaException() {
    super("No hay solicitud de apertura activa para esta tarjeta. "
          + "\nDeben hacerse con 3 horas de antelación.");
  }
}
