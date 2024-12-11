package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion.Personas;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.Optional;

public class RepositorioPersonaJuridica extends Repositorio {
  public Optional<PersonaJuridica> buscarPorUsuario(Long idUsuario){
    return entityManager()
          .createQuery("from " + PersonaJuridica.class.getName() + " WHERE usuario_id = :dato")
          .setParameter("dato", idUsuario)
          .getResultList()
          .stream()
          .findFirst();
  }
}
