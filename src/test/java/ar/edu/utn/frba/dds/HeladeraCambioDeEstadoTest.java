//package ar.edu.utn.frba.dds;
//
//import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
//import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
//import ar.edu.utn.frba.dds.domain.entities.heladeras.Modelo;
//import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorMovimiento;
//import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorTemperatura;
//import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedConstruction;
//
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//public class HeladeraCambioDeEstadoTest {
//  private Heladera heladera;
//  private ReceptorTemperatura receptorTemperatura;
//  private ReceptorMovimiento receptorMovimiento;
//  private final Modelo modelo = new Modelo("ModeloTest", 2.0f, 8.0f);
//
//  private MqttClient mqttClientMock = mock(MqttClient.class);
//
//  @BeforeEach
//  public void antesDeTestear() throws Exception {
//    heladera = new Heladera();
//    heladera.setModelo(modelo);
//    heladera.setId(1L);
//
//    try (MockedConstruction<MqttClient> mocked = mockConstruction(MqttClient.class, (mock, context) -> {
//      doNothing().when(mock).connect();
//      doNothing().when(mock).subscribe(any(String.class));
//    })) {
//      receptorTemperatura = new ReceptorTemperatura("tcp://broker.hivemq.com:1883", "client-id");
//    }
//  }
//
//  @Test
//  @DisplayName("Cuando el Sensor de Movimiento detecta un posible Fraude el Estado cambia a FRAUDE")
//  void testRecalcularEstadoFraude() throws Exception {
//    MqttMessage mqttMessage = new MqttMessage("1,Fraude:true".getBytes());
//    receptorMovimiento.messageArrived("test/topic", mqttMessage);
//
//    Assertions.assertEquals(EstadoHeladera.FRAUDE, heladera.getEstado());
//  }
//
//  @Test
//  @DisplayName("Cuando el Sensor de Temperatura detecta que la Heladera supera su Temperatura Maxima el Estado cambia a FALLA_TEMPERATURA")
//  void testRecalcularEstadoDesperfectoPorTemperaturaMaxima() {
//    MqttMessage mqttMessage = new MqttMessage("1,Temperatura:10".getBytes());
//    receptorTemperatura.messageArrived("test/topic", mqttMessage);
//
//    Assertions.assertEquals(EstadoHeladera.FALLA_TEMPERATURA, heladera.getEstado());
//  }
//
//  @Test
//  @DisplayName("Cuando el Sensor de Temperatura detecta que la Heladera disminuye por debajo su Temperatura Minima el Estado cambia a FALLA_TEMPERATURA")
//  void testRecalcularEstadoDesperfectoPorTemperaturaMinima() {
//    MqttMessage mqttMessage = new MqttMessage("1,Temperatura:1".getBytes());
//    receptorTemperatura.messageArrived("test/topic", mqttMessage);
//
//    Assertions.assertEquals(EstadoHeladera.FALLA_TEMPERATURA, heladera.getEstado());
//  }
//
//  @Test
//  @DisplayName("Cuando No se Detectaron posibles Fraudes y La Temperatura esta en el Rango optimo el estado es ACTIVA")
//  void testRecalcularEstadoActiva() {
//    MqttMessage mqttMessage = new MqttMessage("1,Temperatura:5".getBytes());
//    receptorTemperatura.messageArrived("test/topic", mqttMessage);
//
//    Assertions.assertEquals(EstadoHeladera.ACTIVA, heladera.getEstado());
//  }
//}
//
