package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.usuarios.Permiso;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;
import java.util.Optional;

public class RepositorioPermisos extends Repositorio {
  public Optional<Permiso> buscarPorAccion(String accion) {
    return entityManager().createQuery("from" + Permiso.class.getName() + "where nombre = " + accion, Permiso.class).getResultList().stream().findFirst();
  }
}
