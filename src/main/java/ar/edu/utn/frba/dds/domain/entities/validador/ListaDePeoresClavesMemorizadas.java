package ar.edu.utn.frba.dds.domain.entities.validador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ListaDePeoresClavesMemorizadas implements TipoValidacion {
  @Override
  public boolean validar(String clave) {
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("properties/listaClavesComunes.txt");
    if (inputStream != null) {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        return reader.lines().noneMatch(linea -> linea.trim().equals(clave.trim()));
      } catch (IOException e) {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public String getMensajeError() {
    return "La clave no debe estar entre las 10,000 más comunes y vulnerables.";
  }
}
