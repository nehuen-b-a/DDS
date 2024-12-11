package db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.documento.TipoDocumento;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
/*
public class InteraccionDBTest {
  private RepositorioPersonaHumana repo = new RepositorioPersonaHumana();
  PersonaHumana pe;

  @BeforeEach
  public void antesDeTestear() {
    pe = PersonaHumana
        .builder()
        .nombre("Juan Arnaldo")
        .apellido("Rainalo")
        .documento(new Documento(TipoDocumento.DNI, "440384955"))
        .build();
  }

  @Test
  @DisplayName("La creacion de una persona humana se traslada correctamente mediante el ORM a la DB")
  public void crearPersonaHumana() {
    ContextTest t = new ContextTest();
    t.withTransaction(()->{
      repo.guardar(pe);
    });
    Optional<PersonaHumana> posiblePe = repo.buscarPorDocumento("440384955");
    if (posiblePe.isPresent()) {
      PersonaHumana peDB = posiblePe.get();
      assertEquals(peDB, pe);
    } else {
      fail();
    }
  }

  @Test
  @DisplayName("La actualizaciom de una persona humana se traslada correctamente mediante el ORM a la DB")
  public void actualizarPersonaHumana() {
    pe.setNombre("Marge");
    pe.setApellido("Memorizada");
    ContextTest t = new ContextTest();
    t.withTransaction(()->{
      repo.actualizar(pe);
    });
    Optional<PersonaHumana> posiblePe = repo.buscarPorDocumento("440384955");
    if (posiblePe.isPresent()) {
      PersonaHumana peDB = posiblePe.get();
      assertEquals(peDB.getNombre(), "Marge");
      assertEquals(peDB.getApellido(), "Memorizada");
    } else {
      fail();
    }
  }

  @Test
  @DisplayName("La eliminacion fisica de una persona humana se traslada correctamente mediante el ORM a la DB")
  public void eliminarFisicamentePersonaHumana() {ContextTest t = new ContextTest();
    t.withTransaction(()->{
      repo.guardar(pe);
      repo.eliminarFisico(pe.getClass(), pe.getId());
    });

    Optional<PersonaHumana> posiblePe = repo.buscarPorDocumento("440384955");
    assertTrue(posiblePe.isEmpty());
  }
}
*/