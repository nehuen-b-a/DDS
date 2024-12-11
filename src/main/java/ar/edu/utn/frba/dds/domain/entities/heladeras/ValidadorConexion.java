package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import java.util.List;

public class ValidadorConexion {
  RepositorioHeladera repositorioHeladera;
  public ValidadorConexion(RepositorioHeladera repositorioHeladera){this.repositorioHeladera = repositorioHeladera;}
  public void validarConexiones() {
    List<Heladera> heladeras = repositorioHeladera.buscarTodos(Heladera.class);
    for(Heladera heladera: heladeras){
      heladera.detectarFallaDeConexion();
    }
  }
}
