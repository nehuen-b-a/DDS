package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.Optional;

public class RepositorioUsuario extends Repositorio {

  public Optional<Usuario> buscarPorNombre(String nombre) {
    return entityManager()
        .createQuery("FROM " + Usuario.class.getName() + " u WHERE u.nombre = :nombre")
        .setParameter("nombre", nombre)
        .getResultList().stream().findFirst();
  }

  public boolean existeUsuarioPorNombre(String nombre) {
    Long count = (Long) entityManager()
        .createQuery("SELECT COUNT(u) FROM " + Usuario.class.getName() + " u WHERE u.nombre = :nombre")
        .setParameter("nombre", nombre)
        .getSingleResult();
    return count > 0;
  }
}