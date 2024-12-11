package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.documento.TipoDocumento;
import ar.edu.utn.frba.dds.domain.entities.personasVulnerables.PersonaVulnerable;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.UsoMaximoDeTarjetasPorDiaExcedidoException;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class UsoDeTarjetaTest {
  /*
  @Test
  @DisplayName("Si no tengo menores a cargo y quiero usar una tarjeta por quinta vez, no puedo")
  public void noSePuedeUsarPorQuintaVezUnaTarjeta() {
    PersonaVulnerable personaVulnerable = crearPersonaVulnerable(0);
    Tarjeta tarjeta = new Tarjeta();
    personaVulnerable.setTarjetaEnUso(tarjeta);
    Heladera heladera = new Heladera();

    Vianda vianda1 = crearVianda("2024-05-20", "vianda1", 500, 1200);
    Vianda vianda2 = crearVianda("2024-05-20", "vianda2", 600, 1300);
    Vianda vianda3 = crearVianda("2024-05-20", "vianda3", 600, 1300);
    Vianda vianda4 = crearVianda("2024-05-20", "vianda4", 600, 1300);
    Vianda vianda5 = crearVianda("2024-05-20", "vianda5", 600, 1300);

    heladera.ingresarVianda(vianda1);
    heladera.ingresarVianda(vianda2);
    heladera.ingresarVianda(vianda3);
    heladera.ingresarVianda(vianda4);
    heladera.ingresarVianda(vianda5);

    personaVulnerable.usarTarjeta(heladera, vianda1);
    personaVulnerable.usarTarjeta(heladera, vianda2);
    personaVulnerable.usarTarjeta(heladera, vianda3);
    personaVulnerable.usarTarjeta(heladera, vianda4);

    Assertions.assertThrows(UsoMaximoDeTarjetasPorDiaExcedidoException.class,
        ()->{personaVulnerable.usarTarjeta(heladera, vianda5);});
  }

  @Test
  @DisplayName("El codigo de las tarjetas funciona correctamente")
  public void elCodigoDeLaTarjetaFuncionaCorrectamente() {
    int longitudEsperada = 11;
    int numeroDeHashes = 100;
    Set<String> hashes = new HashSet<>();
    for (int i = 0; i < numeroDeHashes; i++) {
      Tarjeta tarjeta = new Tarjeta();
      hashes.add(tarjeta.getCodigo());
      Assertions.assertEquals(longitudEsperada, tarjeta.getCodigo().length());
    }
    Assertions.assertEquals(numeroDeHashes, hashes.size());
  }

  private PersonaVulnerable crearPersonaVulnerable(int menoresACargo){
    return PersonaVulnerable.builder()
        .nombre("Eduardo").apellido("Lopez").fechaDeNacimiento(LocalDate.parse("1995-05-04"))
        .fechaDeRegistro(LocalDate.parse("2024-05-20"))
        .menoresAcargo(menoresACargo)
        .documento(new Documento(TipoDocumento.DNI, "46644007"))
        .personaQueLoRegistro(mock(PersonaHumana.class))
        .build();
  }
  private Vianda crearVianda(String fecha, String nombre, int calorias, int peso) {
    return new Vianda(LocalDate.parse(fecha), true, nombre, calorias, peso, LocalDate.now());
  }
  */
}
