package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorMovimiento;
import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorTemperatura;
import java.util.concurrent.CountDownLatch;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
/*
public class BrokerTest {
  private ReceptorMovimiento receptorMovimiento;
  private ReceptorTemperatura receptorTemperatura;
  String topicMovimiento = "topicMovimiento";
  String topicTemperatura = "test/topíc";
  String content      = "Mensaje de prueba";
  int qos             = 2;
  String broker       = "tcp://localhost:1883";
  String clientId     = "JavaSample";
  MemoryPersistence persistence = new MemoryPersistence();
  MqttClient sampleClient;
  MqttConnectOptions connOpts;

  private EntityManagerFactory entityManagerFactory;
  private EntityManager entityManager;

//  @BeforeEach
//  public void antesDeTesteo() throws MqttException {
//
//    sampleClient = new MqttClient(broker, clientId, persistence);
//    connOpts = new MqttConnectOptions();
//    this.receptorMovimiento = new ReceptorMovimiento(broker, clientId);
//    this.receptorTemperatura = new ReceptorTemperatura(broker);
//
//  }

  @Test
  void recibeTemperatura() throws MqttException {
    connOpts.setCleanSession(true);
    System.out.println("Connecting to broker: " + broker);
    sampleClient.connect(connOpts);
    System.out.println("Connected");
    System.out.println("Build our receptor");

    System.out.println("Now we subscribe to the topic");
    sampleClient.subscribe(topicTemperatura, receptorMovimiento);
    System.out.println("Right! We are subscribed");


//    MqttMessage message = new MqttMessage(content.getBytes());
//    message.setQos(qos);
//    sampleClient.publish(topic, message);
//    System.out.println("Message published: " + content);

  }
  @AfterEach
  public void afterTest() throws MqttException {
    if (sampleClient.isConnected()) {
      sampleClient.disconnect();
      System.out.println("Disconnected");
    }
    if (entityManager != null) {
      entityManager.close();
    }
    if (entityManagerFactory != null) {
      entityManagerFactory.close();
    }
  }
  @Test
  void receptorRecibeTemperatura(){
    String topic = "test/topic";
    String message = "Hello MQTT";

    /*String testTopic = "test/topic";
    String testMessage = "Test Temperature Message";

    // Crear y publicar un mensaje
    MqttMessage message = new MqttMessage(testMessage.getBytes());
    message.setQos(qos);
    sampleClient.suscribe(testTopic, message);
    System.out.println("Message published: " + testMessage);

    // Esperar un breve momento para la recepción del mensaje
    Thread.sleep(1000);

    // Verificar si el mensaje fue recibido correctamente por el receptorMovimiento
    String receivedMessage = receptorMovimiento.getLastMessage();
    Assertions.assertEquals(testMessage, receivedMessage, "El mensaje recibido no coincide con el mensaje publicado.");*/
  /*}
  //@Test
//  void seRegistraLaFallaDeConexion() throws MqttException, InterruptedException {
//    CountDownLatch latch = new CountDownLatch(2);
//    receptorTemperatura.setTopic("test/topic");
//    receptorTemperatura.setLatch(latch);
//    receptorTemperatura.run();
//    latch.await();
//   // Assertions.assertEquals(receptorTemperatura.getUltimasRecibidas().get("1"), 12L);
//  }
}*/
