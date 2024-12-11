package ar.edu.utn.frba.dds.domain.entities.contacto;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;

public abstract class IObserverNotificacion {
 public abstract void serNotificadoPor(Mensaje mensaje);
 public abstract Direccion getDireccion();
}
