package ar.edu.utn.frba.dds.domain.entities.contacto;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaJavaXMail;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaTelegramBot;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaWhatsAppTwillio;
import ar.edu.utn.frba.dds.dtos.ContactoDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import javax.mail.MessagingException;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Contacto {
  @Transient
  private Set<MedioDeContacto> mediosDeContacto;

  @Column(name = "whatsapp")
  private String whatsapp;

  @Column(name = "mail")
  private String mail;

  @Column(name = "userTelegram")
  private Long telegramChatId;

  public Contacto() {
    this.mediosDeContacto = new HashSet<>();
  }

  public void agregarMedioDeContacto(MedioDeContacto medioDeContacto) {
    mediosDeContacto.add(medioDeContacto);
  }

  public void quitarMedioDeContacto(MedioDeContacto medioDeContacto) {
    mediosDeContacto.remove(medioDeContacto);
  }

  public void enviarMensaje(Mensaje mensaje) throws MessagingException, UnsupportedEncodingException {
    // FIXME: Solución temporal
    // En el futuro, se utilizará el listado de medios de contacto, lo que requerirá
    // modificaciones en la persistencia de los contactos y el uso de ContactoDTO
    // en los controladores actualmente en desarrollo.

    Map<String, MedioDeContacto> mediosDeContacto = new HashMap<>();

    if (this.mail != null && !this.mail.isEmpty()) {
      mediosDeContacto.put("mail", new Mail(ServiceLocator.instanceOf(AdaptadaJavaXMail.class)));
    }
    if (this.whatsapp != null && !this.whatsapp.isEmpty()) {
      mediosDeContacto.put("whatsapp", new WhatsApp(ServiceLocator.instanceOf(AdaptadaWhatsAppTwillio.class)));
    }
    if (this.telegramChatId != null) {
      mediosDeContacto.put("telegram", new Telegram(ServiceLocator.instanceOf(AdaptadaTelegramBot.class)));
    }

    for (MedioDeContacto medio : mediosDeContacto.values()) {
      medio.enviar(mensaje, this);
    }
  }

  public static Contacto fromDTO(ContactoDTO dto) {
    validarContacto(dto);

    Contacto contacto = new Contacto();
    contacto.setWhatsapp(dto.getWhatsapp());
    contacto.setMail(dto.getCorreo());

    if (dto.getTelegram() != null && !dto.getTelegram().isEmpty()) {
      contacto.setTelegramChatId(Long.valueOf(dto.getTelegram()));
    }

    return contacto;
  }

  private static void validarContacto(ContactoDTO dto) {
    if ((dto.getWhatsapp() == null || dto.getWhatsapp().isEmpty())
        && (dto.getTelegram() == null || dto.getTelegram().isEmpty())
        && (dto.getCorreo() == null || dto.getCorreo().isEmpty())) {
      throw new ValidacionFormularioException("Por favor, indique al menos un medio de contacto.");
    }

    if (dto.getCorreo() != null && !dto.getCorreo().isEmpty()
        && !dto.getCorreo().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      throw new ValidacionFormularioException("El formato del correo electrónico es inválido.");
    }

    if (dto.getWhatsapp() != null && !dto.getWhatsapp().isEmpty()
        && !dto.getWhatsapp().matches("^[0-9]{10,15}$")) {
      throw new ValidacionFormularioException("El formato del número de WhatsApp es inválido.");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }

    if (!(o instanceof Contacto)) {
      return false;
    }

    Contacto contacto = (Contacto) o;

    return (Objects.equals(this.whatsapp, contacto.whatsapp)) &&
        (Objects.equals(this.mail, contacto.mail)) &&
        (Objects.equals(this.telegramChatId, contacto.telegramChatId));
  }

}