package ar.edu.utn.frba.dds.domain.entities.heladeras.receptores;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.ControladorIncidenteHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import java.util.Optional;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class ReceptorMovimiento implements IMqttMessageListener, Runnable {
  private MqttClient client;
  private String brokerUrl;
  private String topic;
  public ReceptorMovimiento(String brokerUrl, String topic) throws MqttException {
    this.brokerUrl = brokerUrl;
    this.topic = topic;
  }
  @Override
  public void messageArrived(String topic, MqttMessage mensaje){
        try{
            Long idHeladera = Long.parseLong(mensaje.toString());
            System.out.println("se recibió el mensaje correctamente");
            procesarMensaje(idHeladera);
        } catch (Exception e) {
          System.err.println("Error al procesar el mensaje: " + e.getMessage());
        }
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
  private void procesarMensaje(Long idHeladera) {
        ControladorIncidenteHeladera controlador = ServiceLocator.instanceOf(ControladorIncidenteHeladera.class);
        controlador.procesarFraude(idHeladera);
  }

}
