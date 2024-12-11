package ar.edu.utn.frba.dds.domain.entities.validador;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ValidadorDeClave {
  private Set<TipoValidacion> validadores;
  private String erroresFinales;

  public ValidadorDeClave(TipoValidacion ...tipoValidacion) {
    validadores = new HashSet<>();
    Collections.addAll(validadores, tipoValidacion);
  }

  public boolean validar(String clave) {
    StringBuilder errores = new StringBuilder();
    boolean esValido = true;

    for (TipoValidacion validador : validadores) {
      if (!validador.validar(clave)) {
        errores.append(validador.getMensajeError()).append(' ');
        esValido = false;
      }
    }

    erroresFinales = errores.toString();
    return esValido;
  }
}
