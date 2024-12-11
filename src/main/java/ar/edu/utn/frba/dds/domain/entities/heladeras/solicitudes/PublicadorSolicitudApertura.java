package ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes;

import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class PublicadorSolicitudApertura {
  private static PublicadorSolicitudApertura instancia;
  private MqttClient client;
  private final static String broker = "tcp://your-broker-address:1883";

  public static PublicadorSolicitudApertura getInstance() {
    if (instancia == null) {
      instancia = new PublicadorSolicitudApertura();
    }
    return instancia;
  }

  // Método para inyectar un MqttClient mockeado para pruebas
  public void setMqttClient(MqttClient client) {
    this.client = client;
  }

  public static void setInstance(PublicadorSolicitudApertura publicador) {
    instancia = publicador;
  }

  public void publicarSolicitudApertura(String codigoTarjeta, LocalDateTime fecha, Long idHeladera) {
    // FIXME: La ruta del broker debe compartirse con los receptores y utilizar distintos topicos?
    String topic = "mqqt/heladeras/" + idHeladera;
    int cantidadHoras = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("horas_para_ejecutar_accion"));
    String contenido = codigoTarjeta + " " + fecha.plusHours(cantidadHoras);
    int qos = 2;

    try {
      client.connect();

      MqttMessage message = new MqttMessage(contenido.getBytes());
      message.setQos(qos);
      client.publish(topic, message);

      client.disconnect();
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}

