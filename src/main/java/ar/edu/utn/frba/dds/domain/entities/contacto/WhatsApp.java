package ar.edu.utn.frba.dds.domain.entities.contacto;

import ar.edu.utn.frba.dds.domain.adapters.AdapterWhatsApp;
import lombok.Setter;

@Setter
public class WhatsApp implements MedioDeContacto {
  private AdapterWhatsApp adaptador;

  public WhatsApp(AdapterWhatsApp adaptador) {
    this.adaptador = adaptador;
  }

  @Override
  public void enviar(Mensaje mensaje, Contacto contacto) {
    adaptador.enviar(contacto.getWhatsapp(), mensaje.getCuerpo());
  }
}
