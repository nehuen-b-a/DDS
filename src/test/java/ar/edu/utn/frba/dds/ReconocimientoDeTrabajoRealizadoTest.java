package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.oferta.Oferta;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.Rubro;
import ar.edu.utn.frba.dds.domain.entities.viandas.DistribucionVianda;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ReconocimientoDeTrabajoRealizadoTest {/*
  @Test
  @DisplayName("Una persona que dona dos viandas tiene puntaje tres")
  public void unaPersonaQueDonaDosViandasTienePuntajeTres() {
    PersonaHumana persona = new PersonaHumana();
    Contribucion donacionVianda1 = crearVianda("2024-10-29", "Milanesa", 300, 52);
    Contribucion donacionVianda2 = crearVianda("2024-11-20", "Pizza", 400, 30);
    persona.agregarContribucion(donacionVianda1);
    persona.agregarContribucion(donacionVianda2);

    float puntaje = ReconocimientoTrabajoRealizado.getInstance().calcularPuntaje(persona.getContribuciones(), persona.puntosGastados());
    Assertions.assertEquals(3.0f, puntaje);
  }

  @Test
  @DisplayName("Una persona humana distribuye 3 viandas. Su puntaje es 3")
  public void unaPersonaHumanaDistribuye3ViandasYObtienePuntaje3(){
    PersonaHumana persona = new PersonaHumana();

    Heladera heladeraOrigen = new Heladera();
    Heladera heladeraDestino = new Heladera();

    Vianda vianda1 = crearVianda("2024-10-20", "viandaSaludable", 500, 1050);
    Vianda vianda2 = crearVianda("2024-10-20", "viandaLigera", 300, 900);
    Vianda vianda3 = crearVianda("2024-10-20", "viandaPesada", 900, 2000);

    List<Vianda> lista = new ArrayList<>();
    lista.add(vianda1);
    lista.add(vianda2);
    lista.add(vianda3);

    heladeraOrigen.ingresarViandas(lista);

    DistribucionVianda distribucionVianda = crearDistribucion(3, heladeraOrigen, heladeraDestino);
    distribucionVianda.distribuir(lista);

    persona.agregarContribucion(distribucionVianda);
    float puntaje = ReconocimientoTrabajoRealizado.getInstance().calcularPuntaje(persona.getContribuciones(),
        persona.puntosGastados());

    Assertions.assertEquals(puntaje, 3.0f);


  }
//@Test
//@DisplayName("Una persona jurídica se hace cargo de dos heladeras por 1 mes cada una. Su puntaje es 10")
//public void unaPersonaJuridicaSeEncargaDeDosHeladerasPor1MesySuPuntajeEs10(){
//    PersonaJuridica personaJuridica = new PersonaJuridica();
//    Heladera heladera1 = new Heladera();
//    Heladera heladera2 = new Heladera();
//    heladera1.setFechaRegistro(LocalDate.parse("2024-04-28").atStartOfDay());
//    heladera2.setFechaRegistro(LocalDate.parse("2024-04-28").atStartOfDay());
//
//    personaJuridica.hacerseCargoDeHeladera(heladera1);
//    personaJuridica.hacerseCargoDeHeladera(heladera2);
//
//    float puntaje = ReconocimientoTrabajoRealizado.getInstance().calcularPuntaje(personaJuridica.getContribuciones(),
//        personaJuridica.puntosGastados());
//    Assertions.assertEquals(puntaje, 10.0f);
//
//}
@Test
@DisplayName("Una persona humana con 3 puntos puede canjear una oferta que requiere 2 puntos")
public void unaPersonaHumanaCon3PuntosPuedeCanjearUnaOfertaDe2Puntos(){
    PersonaHumana persona = new PersonaHumana();
    Contribucion donacionVianda1 = crearVianda("2024-10-29", "Milanesa", 300, 52);
    Contribucion donacionVianda2 = crearVianda("2024-11-20", "Pizza", 400, 30);
    persona.agregarContribucion(donacionVianda1);
    persona.agregarContribucion(donacionVianda2);

    Oferta oferta = new Oferta(1L, "Producto1", 2.0f, new Rubro("gastronomia"),
        new PersonaJuridica());

    oferta.canjear(persona);
    Assertions.assertTrue(!persona.getOfertasCanjeadas().isEmpty());
}

  private Vianda crearVianda(String fecha, String nombre, int calorias, int peso) {
    return new Vianda(LocalDate.parse(fecha), true, nombre, calorias, peso, LocalDate.now());
  }

  private DistribucionVianda crearDistribucion(int cantidad, Heladera origen, Heladera destino){
    DistribucionVianda distribucionVianda = new DistribucionVianda(LocalDate.now(), cantidad);
    distribucionVianda.setHeladeraOrigen(origen);
    distribucionVianda.setHeladeraDestino(destino);
    return distribucionVianda;
  }*/
}

