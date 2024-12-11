package ar.edu.utn.frba.dds.domain.entities;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.util.Properties;

public class ReconocimientoTrabajoRealizado {
  private static ReconocimientoTrabajoRealizado instancia;
  private static Map<String, Float> coeficientes;
  private static final String path = "src/main/resources/properties/coeficientesPuntaje.properties";
  private ReconocimientoTrabajoRealizado() {}

  public static ReconocimientoTrabajoRealizado getInstance() {
    if (instancia == null) {
      instancia = new ReconocimientoTrabajoRealizado();
      coeficientes = new HashMap<>();
      instancia.cargarCoeficientesDesdeArchivo();
    }
    return instancia;
  }

  public static void cargarCoeficientesDesdeArchivo() {
    Properties propiedades = new Properties();
    try (InputStream input = Files.newInputStream(Paths.get(path))) {
      propiedades.load(input);
      for (String nombreClave : propiedades.stringPropertyNames()) {
        String valor = propiedades.getProperty(nombreClave);
        coeficientes.put(nombreClave, Float.parseFloat(valor));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static float obtenerCoeficientes(String nombreCoeficiente) {
    return coeficientes.get(nombreCoeficiente);
  }

  public float calcularPuntaje(Set<Contribucion> contribuciones, float puntajeGastado) {
    float puntajeBruto = (float) 0;
    for (Contribucion contribucion : contribuciones) {
      puntajeBruto += contribucion.calcularPuntaje();
    }
    return puntajeBruto - puntajeGastado;
  }
}

