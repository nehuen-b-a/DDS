package ar.edu.utn.frba.dds.domain.entities.validador;

import lombok.Getter;
import lombok.Setter;


@Getter
public class LongitudEstipulada implements TipoValidacion {
  @Setter
  private int longitudMaxima;
  private final int longitudMinima = 8;

  public LongitudEstipulada(int longitudMaxima) {
    this.longitudMaxima = longitudMaxima;
  }

  @Override
  public boolean validar(String clave) {
    int longitud = clave.length();
    return (longitud >= longitudMinima && longitud <= longitudMaxima);
  }

  public String getMensajeError() {
    return String.format("La clave debe tener entre 8 y %d caracteres.", longitudMaxima);
  }
}
