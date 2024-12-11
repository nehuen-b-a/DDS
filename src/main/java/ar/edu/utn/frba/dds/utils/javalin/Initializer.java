package ar.edu.utn.frba.dds.utils.javalin;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Municipio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Provincia;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefMunicipios;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefProvincias;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefServicio;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import org.hibernate.service.spi.ServiceException;

public class Initializer implements WithSimplePersistenceUnit {
  public void init() {
    // Este método asegura que la base de datos esté correctamente inicializada.
    // Si la base de datos está vacía, se realiza la hidratación inicial ejecutando el script.
    // Si la base ya contiene datos, el script no se vuelve a ejecutar.
    // ACLARACION: ES NECESARIO COMENZAR CON LA BASE DE DATOS VACIA
    hidratarBaseConArchivo();
  }

  private void hidratarBaseConArchivo() {
    EntityManager em = null;
    try {
      em = entityManager();
    } catch (ServiceException e) {
      // e.printStackTrace();
      System.exit(-1);
    }

    EntityTransaction transaction = em.getTransaction();
    try {
      transaction.begin();

      crearTablaMetadata();
      boolean datosExistentes = verificarDatosExistentes();

      if (!datosExistentes) {
        ejecutarScriptSQL();
        inicializarDatosGeoRef();
        marcarScriptEjecutado();
        System.out.println("\u001B[32mDatos insertados correctamente.\u001B[0m");
      } else {
        System.out.println("\u001B[32mLos datos ya han sido insertados previamente.\u001B[0m");
      }

      transaction.commit();
    } catch (Exception e) {
      if (transaction.isActive()) {
        transaction.rollback();
      }
      e.printStackTrace();
    } finally {
      entityManager().close();
    }
  }

  private void crearTablaMetadata() {
    entityManager().createNativeQuery(
        "CREATE TABLE IF NOT EXISTS metadata ("
            + "id INT PRIMARY KEY, "
            + "script_executado BOOLEAN DEFAULT FALSE)"
    ).executeUpdate();
  }

  private boolean verificarDatosExistentes() {
    try {
      Boolean scriptEjecutado = (Boolean) entityManager().createNativeQuery(
          "SELECT script_executado FROM metadata WHERE id = 1"
      ).getSingleResult();
      return scriptEjecutado != null && scriptEjecutado; // Devuelve true si ya se ejecutó
    } catch (NoResultException e) {
      return false; // No hay registro, por lo tanto no se ha ejecutado
    }
  }

  private void ejecutarScriptSQL() throws Exception {
    String sql = new String(Files.readAllBytes(Paths.get("sql/inicializacion_tablas.sql")));
    String[] statements = sql.split(";");

    for (String statement : statements) {
      if (!statement.trim().isEmpty()) {
        try {
          entityManager().createNativeQuery(statement.trim()).executeUpdate();
        } catch (Exception e) {
          throw new RuntimeException("Error al ejecutar la sentencia SQL: " + statement, e);
        }
      }
    }
  }

  private void marcarScriptEjecutado() {
    entityManager().createNativeQuery(
        "INSERT INTO metadata (id, script_executado) "
            + "VALUES (1, TRUE)"
    ).executeUpdate();
  }

  private void inicializarDatosGeoRef() throws IOException {
    List<GeoRefProvincias.Provincia> provincias = GeoRefServicio.getInstancia().obtenerProvincias().getProvincias();
    List<GeoRefMunicipios.Municipio> municipios;

    for (GeoRefProvincias.Provincia provincia : provincias) {
      // Crea y persiste la provincia en la transacción principal
      Provincia provinciaAPersistir = new Provincia(provincia.getNombre());
      entityManager().persist(provinciaAPersistir);

      // Ahora obtiene y persiste los municipios de esta provincia
      municipios = GeoRefServicio.getInstancia().obtenerMunicipios(provincia.getNombre()).getMunicipios();
      for (GeoRefMunicipios.Municipio municipio : municipios) {
        entityManager().persist(new Municipio(municipio.getNombre(), provinciaAPersistir));
      }
    }
  }
}
