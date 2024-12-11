package ar.edu.utn.frba.dds.utils.manejos;

import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import org.apache.commons.lang3.tuple.Pair;

public class CamposObligatoriosVacios {
  @SafeVarargs
  public static <T> void validarCampos(Pair<String, T>... campos) {
    for (Pair<String, T> campo : campos) {
      if (campo.getValue() == null || (campo.getValue() instanceof String && ((String) campo.getValue()).isEmpty())) {
        throw new ValidacionFormularioException("El campo " + campo.getKey() + " es obligatorio.");
      }
    }
  }
}
