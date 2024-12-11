package ar.edu.utn.frba.dds.domain.entities.validador;

public class AusenciaDeCredencialesPorDefecto implements TipoValidacion {
  private String usuario;

  public AusenciaDeCredencialesPorDefecto(String usuario) {
    this.usuario = usuario;
  }

  @Override
  public boolean validar(String clave) {
    return !usuario.equals(clave);
  }

  @Override
  public String getMensajeError() {
    return "La clave no debe coincidir con el nombre de usuario.";
  }
}
