package ar.edu.utn.frba.dds.domain.entities.validador;


public interface TipoValidacion {
  boolean validar(String clave);
  String getMensajeError();
}

