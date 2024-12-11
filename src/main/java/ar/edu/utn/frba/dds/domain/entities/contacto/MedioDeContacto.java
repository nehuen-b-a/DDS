package ar.edu.utn.frba.dds.domain.entities.contacto;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;

public interface MedioDeContacto {
  void enviar(Mensaje mensaje, Contacto contacto) throws MessagingException, UnsupportedEncodingException;
}
