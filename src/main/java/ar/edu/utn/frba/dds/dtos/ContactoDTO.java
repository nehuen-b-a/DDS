package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.contacto.Contacto;
import io.javalin.http.Context;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContactoDTO implements DTO {
  private String whatsapp;
  private String telegram;
  private String correo;

  public ContactoDTO(Contacto contacto) {
    this.whatsapp = contacto.getWhatsapp();
    this.telegram = contacto.getTelegramChatId() != null ? String.valueOf(contacto.getTelegramChatId()) : "";
    this.correo = contacto.getMail();
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.setWhatsapp(context.formParam("whatsapp"));
    this.setTelegram(context.formParam("telegram"));
    this.setCorreo(context.formParam("correo"));
  }
}
