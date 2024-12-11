package ar.edu.utn.frba.dds.domain.entities.heladeras.receptores;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.ControladorDonacionVianda;
import ar.edu.utn.frba.dds.controllers.ControladorHeladera;
import ar.edu.utn.frba.dds.controllers.ControladorIncidenteHeladera;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ReceptorApertura implements IMqttMessageListener, Runnable {
  private MqttClient client;
  private String brokerUrl;
  private String topic;
  public ReceptorApertura(String brokerUrl, String topic) throws MqttException {
    this.brokerUrl = brokerUrl;
    this.topic = topic;
  }
  @Override
  public void messageArrived(String topic, MqttMessage mensaje){
    String payload = new String(mensaje.getPayload());
    System.out.println("Mensaje recibido: " + payload);
    try {
      String[] partes = dividirPayload(payload);
      if (partes != null) {
        String idHeladera = partes[0];
        String idColaborador = partes[1];
        String idVianda = partes[2];
        System.out.println("Se recibió exitosamente el mensaje");
        procesarMensaje(Long.parseLong(idHeladera), Long.parseLong(idColaborador), Long.parseLong(idVianda));
      }
    } catch (NumberFormatException e) {
      System.err.println("Error al convertir el valor a entero: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Error al procesar el mensaje: " + e.getMessage());
    }
    
  }

  private String[] dividirPayload(String payload) {
    String[] partes = payload.split(",");
    return partes;
  }

  @Override
  public void run() {
    try {
      client = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
      MqttConnectOptions connOpts = new MqttConnectOptions();
      connOpts.setCleanSession(true);

      client.connect(connOpts);
      System.out.println("Connected to broker: " + brokerUrl);
      // Suscribirse al topic usando esta clase como listener
      client.subscribe(topic, this);
      System.out.println("MQTT Receiver is running and listening to topic: " + topic);
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
  private void procesarMensaje(Long idHeladera, Long idColaborador, Long idVianda) {
    ControladorDonacionVianda controlador = ServiceLocator.instanceOf(ControladorDonacionVianda.class);
    controlador.procesarIngresoDeViandaEnHeladera(idHeladera, idColaborador, idVianda);
  }

}