package ar.edu.utn.frba.dds.domain.adapters;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

public class AdaptadaWhatsAppTwillio implements AdapterWhatsApp {
  private final String ACCOUNT_SID;
  private final String AUTH_TOKEN;
  private final String telefonoDeEnvio;

  public AdaptadaWhatsAppTwillio() {
    Dotenv dotenv = Dotenv.load();
    this.ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    this.AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    this.telefonoDeEnvio = dotenv.get("TWILIO_PHONE_NUMBER");
  }

  // TODO: Deshardcodear mi numero de telefono
  @Override
  public void enviar(String telefonoUsuario, String texto) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    Message.creator(
        new com.twilio.type.PhoneNumber("whatsapp:+5491162226007"),
        new com.twilio.type.PhoneNumber(telefonoDeEnvio),
        texto
    ).create();
  }
}