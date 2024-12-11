package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.usuarios.Rol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.Optional;
import javax.persistence.NoResultException;

public class RepositorioRol extends Repositorio {
  public Optional<Rol> buscarPorTipo(TipoRol tipo) {
    try {
      Rol rol = (Rol) entityManager()
          .createQuery("SELECT r FROM " + Rol.class.getName() + " r WHERE r.tipoRol = :tipo")
          .setParameter("tipo", tipo)
          .getSingleResult();
      return Optional.of(rol);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
