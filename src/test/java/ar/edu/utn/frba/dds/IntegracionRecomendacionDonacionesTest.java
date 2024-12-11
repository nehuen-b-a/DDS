package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class IntegracionRecomendacionDonacionesTest {/*

    private static RecomendacionDireccion direccion = null;

    @BeforeEach
    public void antesDeTestear() {
        direccion = new RecomendacionDireccion();
        direccion.setAltura(4321);
        direccion.setProvincia("Ciudad Autónoma de Buenos Aires");
        direccion.setCalle("AV HIPOLITO YRIGOYEN");
    }

    @Test
    @DisplayName("El alta y busqueda de personas vulnerables funciona")
    public void altaPersonasVulnerables() throws IOException {
        Personas personas = new Personas();

        Personas.Persona persona = new Personas.Persona();
        persona.setNombre("Martin");
        persona.setApellido("Martinez");
        persona.setDireccion(direccion);

        personas.setPersonas(persona);
        List<PersonaGrabada> personasGrabadas = RecomendacionDonacion.getInstancia().guardarPersonas(personas);
        System.out.println(personasGrabadas.get(0).nombre);

        List<RecomendacionPersonasVulnerables> recomendacionPersonasVulnerables = RecomendacionDonacion.getInstancia().recomendarPersonasVulnerables(3.14F, "AV HIPOLITO YRIGOYEN", "4321", "CABA");

        System.out.println(recomendacionPersonasVulnerables.get(0).nombre);
        Assertions.assertEquals(recomendacionPersonasVulnerables.get(0).nombre, persona.getNombre());
        Assertions.assertEquals(recomendacionPersonasVulnerables.get(0).apellido, persona.getApellido());
        Assertions.assertEquals(recomendacionPersonasVulnerables.get(0).direccion.direccion.calle, persona.getDireccion().getCalle());
        Assertions.assertEquals(recomendacionPersonasVulnerables.get(0).direccion.direccion.provincia, persona.getDireccion().getProvincia());
    }

    @Test
    @DisplayName("El alta y busqueda de heladeras funciona")
    public void altaHeladeras() throws IOException {
        Heladeras heladeras = new Heladeras();

        Heladeras.HeladeraGrabada heladeraGrabada = new Heladeras.HeladeraGrabada();
        heladeraGrabada.setDireccion(direccion);
        heladeraGrabada.setCantidad_viandas(1000);

        heladeras.setHeladeras(heladeraGrabada);
        RecomendacionDonacion.getInstancia().guardarHeladeras(heladeras);

        List<RecomendacionHeladeras> recomendacionHeladeras = RecomendacionDonacion.getInstancia().recomendarHeladeras(3.14F, 44, "HIPOLITO YRIGOYEN", "4321", "CABA");

        System.out.println(recomendacionHeladeras.get(0).cantidad_recomendada);
        Assertions.assertEquals(recomendacionHeladeras.get(0).ubicacion.direccion.altura, heladeraGrabada.getDireccion().getAltura());
        Assertions.assertEquals(recomendacionHeladeras.get(0).ubicacion.direccion.calle, heladeraGrabada.getDireccion().getCalle());
        Assertions.assertEquals(recomendacionHeladeras.get(0).ubicacion.direccion.provincia, heladeraGrabada.getDireccion().getProvincia());
        Assertions.assertEquals(recomendacionHeladeras.get(0).cantidad_recomendada, heladeraGrabada.cantidad_viandas);
    }*/
}
