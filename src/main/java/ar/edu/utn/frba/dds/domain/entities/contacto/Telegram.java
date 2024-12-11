package ar.edu.utn.frba.dds.domain.entities.contacto;

import ar.edu.utn.frba.dds.domain.adapters.AdapterTelegram;
import lombok.Setter;

@Setter
public class Telegram implements MedioDeContacto {
  private AdapterTelegram adaptador;

  public Telegram(AdapterTelegram adaptador) {
    this.adaptador = adaptador;
  }

  @Override
  public void enviar(Mensaje mensaje, Contacto contacto) {
    adaptador.enviar(mensaje.getCuerpo(), contacto.getTelegramChatId());
  }
}