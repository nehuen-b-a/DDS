package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.Rubro;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Rol;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.Optional;
import javax.persistence.NoResultException;

public class RepositorioRubro extends Repositorio {
  public Optional<Rubro> buscarPorNombre(String nombre) {
    return entityManager()
        .createQuery("from " + Rubro.class.getName() + " WHERE nombre = :nombre")
        .setParameter("nombre", nombre)
        .getResultList()
        .stream()
        .findFirst();

  }
}
