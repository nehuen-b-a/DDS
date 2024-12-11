package ar.edu.utn.frba.dds;


import ar.edu.utn.frba.dds.domain.entities.heladeras.*;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.PublicadorSolicitudApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.SolicitudApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.solicitudes.AccionApertura;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class ManejoDeSolicitudesTest {/*
    private Heladera heladeraLlena;
    private Heladera heladeraCasiLLena;
    private Heladera heladeraVacia;
    private Heladera heladera;
    private SolicitudApertura solicitudA;
    private SolicitudApertura solicitudB;
    private SolicitudApertura solicitudC;
    private MqttClient mqttClientMock = mock(MqttClient.class);

    @BeforeEach
    public void antesDeTestear() throws Exception {
        Vianda vianda1 = crearVianda("2024-05-20", "vianda1", 500, 1200);
        Vianda vianda2 = crearVianda("2024-05-20", "vianda2", 600, 1300);
        Vianda vianda3 = crearVianda("2024-05-20", "vianda3", 600, 1300);
        Vianda vianda4 = crearVianda("2024-05-20", "vianda4", 600, 1300);
        Vianda vianda5 = crearVianda("2024-05-20", "vianda5", 600, 1300);

        heladeraLlena = new Heladera();
        heladeraLlena.setEstado(EstadoHeladera.ACTIVA);
        heladeraLlena.setNombre("Llena");
        heladeraLlena.setCapacidadMaximaViandas(5);
        heladeraLlena.ingresarVianda(vianda1);
        heladeraLlena.ingresarVianda(vianda2);
        heladeraLlena.ingresarVianda(vianda3);
        heladeraLlena.ingresarVianda(vianda4);
        heladeraLlena.ingresarVianda(vianda5);

        heladeraCasiLLena = new Heladera();
        heladeraCasiLLena.setEstado(EstadoHeladera.ACTIVA);
        heladeraCasiLLena.setNombre("CasiLlena");
        heladeraCasiLLena.setCapacidadMaximaViandas(5);
        heladeraCasiLLena.ingresarVianda(vianda1);
        heladeraCasiLLena.ingresarVianda(vianda2);
        heladeraCasiLLena.ingresarVianda(vianda3);

        heladeraVacia = new Heladera();
        heladeraVacia.setEstado(EstadoHeladera.ACTIVA);
        heladeraVacia.setNombre("Vacia");
        heladeraVacia.setCapacidadMaximaViandas(5);

        Tarjeta tarjetaA = new Tarjeta();
        tarjetaA.setCodigo("1111");
        tarjetaA.setFechaRecepcionColaborador(LocalDate.now());

        Tarjeta tarjetaB = new Tarjeta();
        tarjetaB.setCodigo("0000");
        tarjetaB.setFechaRecepcionColaborador(LocalDate.now());

        solicitudA = new SolicitudApertura();
        solicitudA.setAccion(AccionApertura.INGRESAR_VIANDA);
        solicitudA.setCantidadViandas(4);
        solicitudA.setCodigoTarjeta("1111");
        solicitudA.setAperturaConcretada(false);
        solicitudA.setFechaConcrecion(LocalDateTime.now());

        solicitudB = new SolicitudApertura();
        solicitudB.setAccion(AccionApertura.QUITAR_VIANDA);
        solicitudB.setCantidadViandas(1);
        solicitudB.setCodigoTarjeta("1111");
        solicitudB.setAperturaConcretada(false);
        solicitudB.setFechaConcrecion(LocalDateTime.now());
        solicitudC = new SolicitudApertura();
        solicitudC.setAccion(AccionApertura.INGRESAR_VIANDA);
        solicitudC.setCantidadViandas(1);
        solicitudC.setCodigoTarjeta("0000");
        solicitudC.setAperturaConcretada(false);
        solicitudC.setFechaConcrecion(LocalDateTime.now());

        heladera = new Heladera();
        heladera.setId(1L);

        RepositorioHeladera repositorioHeladeraMock = mock(RepositorioHeladera.class);
        when(repositorioHeladeraMock.buscarPorId(1L, Heladera.class)).thenReturn(Optional.of(heladera));

        PublicadorSolicitudApertura publicador = PublicadorSolicitudApertura.getInstance();
        publicador.setMqttClient(mqttClientMock);
    }

    @Test
    @DisplayName("Cuando un Colaborador realiza una Solicitud para agregar 4 Viandas, pero la heladera está casi llena, se genera una excepción de heladera llena")
    void testHeladeraCasiLLena() {
        Exception exception = Assertions.assertThrows(HeladeraVirtualmenteLlenaException.class, () -> {
            heladeraCasiLLena.agregarSolicitudApertura(solicitudA);
        });
        assertEquals("La heladera esta virtualmente llena. Las solicitudes de apertura indican que luego quedarian inconsistencias.", exception.getMessage());
    }

    @Test
    @DisplayName("Cuando un Colaborador realiza una Solicitud para quitar Viandas, pero la heladera está vacía, se genera una excepción de heladera vacía")
    void testHeladeraVacia() {
        Exception exception = Assertions.assertThrows(HeladeraVirtualmenteVaciaException.class, () -> {
            heladeraVacia.agregarSolicitudApertura(solicitudB);
        });
        assertEquals("La heladera esta virtualmente vacia. Las solicitudes de apertura indican que luego quedarian inconsistencias.", exception.getMessage());
    }

    @Test
    @DisplayName("Cuando una Solicitud llena una Heladera y otra Solicitud de agregar Vianda es enviada, se genera una excepción de heladera llena")
    void testSolicitudesEnConflicto() {
        // Simular el llenado de la heladera con solicitudA
        heladeraCasiLLena.agregarSolicitudApertura(solicitudC);

        // Luego intentar agregar una vianda más, lo cual debería fallar
        Exception exception = Assertions.assertThrows(HeladeraVirtualmenteLlenaException.class, () -> {
            heladeraCasiLLena.agregarSolicitudApertura(solicitudC);
        });
        assertEquals("La heladera esta virtualmente llena. Las solicitudes de apertura indican que luego quedarian inconsistencias.", exception.getMessage());
    }

    @Test
    @DisplayName("Cuando una Heladera está Inactiva por alguna razon no se aceptan solicitudes, y se genera una excepción de heladera inactiva ")
    void testHeladeraInactiva() {
        heladeraLlena.setEstado(EstadoHeladera.FALLA_TECNICA);
        Exception exception = Assertions.assertThrows(HeladeraInactivaException.class, () -> {
            heladeraLlena.agregarSolicitudApertura(solicitudA);
        });
        assertEquals("La heladera que se quiere usar se halla inactiva.", exception.getMessage());
    }

    @Test
    @DisplayName("Verifica la publicación de un mensaje MQTT al agregar una solicitud de apertura valida")
    public void testPublicarSolicitudApertura() throws MqttException {
        SolicitudApertura solicitud = new SolicitudApertura();
        solicitud.setCodigoTarjeta("12345");
        solicitud.setFechaSolicitud(LocalDateTime.now());
        solicitud.setAccion(AccionApertura.INGRESAR_VIANDA);
        solicitud.setCantidadViandas(2);

        heladera.setCapacidadMaximaViandas(40);
        heladera.agregarSolicitudApertura(solicitud);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MqttMessage> messageCaptor = ArgumentCaptor.forClass(MqttMessage.class);

        verify(mqttClientMock, times(1)).publish(topicCaptor.capture(), messageCaptor.capture());

        String expectedTopic = "mqqt/heladeras/1";
        int expectedHoras = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("horas_para_ejecutar_accion"));
        String expectedMessageContent = "12345 " + solicitud.getFechaSolicitud().plusHours(expectedHoras);

        assertEquals(expectedTopic, topicCaptor.getValue());
        assertEquals(expectedMessageContent, new String(messageCaptor.getValue().getPayload()));
        assertEquals(2, messageCaptor.getValue().getQos());
    }

    private Vianda crearVianda(String fecha, String nombre, int calorias, int peso) {
        return new Vianda(LocalDate.parse(fecha), true, nombre, calorias, peso, LocalDate.now());
    }*/
}
