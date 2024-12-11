package ar.edu.utn.frba.dds.domain.adapters;

import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AdaptadaJavaXMail implements AdapterMail {

  // TODO: Pasar a sacar las variables de entorno del .env
  private static final String username = System.getenv("USER_DDS_TPA");
  private static final String password = System.getenv("PASSWORD_DDS_TPA");

  @Override
  public void enviar(Mensaje mensaje, String correo) throws MessagingException, UnsupportedEncodingException {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.starttls.enable", "true");
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");

    Session session = Session.getInstance(properties, new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(username, password);
      }
    });

    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress(username, "Mejora Acceso Alimentario"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
    message.setSubject(mensaje.getAsunto());
    message.setText(mensaje.getCuerpo());

    Transport.send(message);
  }
}

