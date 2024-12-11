package ar.edu.utn.frba.dds.utils.permisos;

public class SinPermisoSuficienteException extends RuntimeException  {
  public SinPermisoSuficienteException() {}
  public SinPermisoSuficienteException(String s) { super(s); }
}
