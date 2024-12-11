package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

import ar.edu.utn.frba.dds.domain.entities.contacto.IObserverNotificacion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestorSuscripciones {
  @Getter
  private Map<TipoSuscripcion, List<Suscripcion>> suscripcionesPorTipo;

  public GestorSuscripciones() {
    this.suscripcionesPorTipo = new HashMap<>();
  }

  public void notificar(TipoSuscripcion tipo, Heladera heladera) {
    List<Suscripcion> suscripciones = suscripcionesPorTipo.get(tipo);
    if (suscripciones != null) {
      suscripciones.forEach(suscripcion -> suscripcion.notificar(heladera));
    }
  }
  
  public void agregarSuscripcionPorTipo(TipoSuscripcion tipo, Suscripcion suscripcion, Heladera heladera) {
    if (suscripcionValida(suscripcion.suscriptor, heladera)) {
      throw new SuscripcionNoCercanaException();
    }

    List<Suscripcion> suscripciones = this.suscripcionesPorTipo.get(tipo);
    suscripciones.add(suscripcion);
    this.suscripcionesPorTipo.put(tipo, suscripciones);
  }

  public boolean suscripcionValida(IObserverNotificacion suscriptor, Heladera heladera) {
    return suscriptor.getDireccion().getMunicipio().equals(heladera.getDireccion().getMunicipio())
            && suscriptor.getDireccion().getProvincia().equals(heladera.getDireccion().getProvincia());
  }


}