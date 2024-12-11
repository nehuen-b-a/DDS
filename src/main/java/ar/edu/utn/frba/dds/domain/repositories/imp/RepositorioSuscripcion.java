package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones.TipoSuscripcion;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;
import java.util.Optional;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

public class RepositorioSuscripcion extends Repositorio {
  public Suscripcion buscarPorTipo(Long heladeraId, Long usuarioId, Class<? extends Suscripcion> tipoClase) {
    return entityManager()
        .createQuery("SELECT s "
            + "FROM " + Suscripcion.class.getName() + " s "
            + "JOIN s.suscriptor p JOIN p.usuario u "
            + "WHERE u.id = :usuarioId AND s.heladera.id = :heladeraId "
            + "AND TYPE(s) = :tipoClase", Suscripcion.class)
        .setParameter("heladeraId", heladeraId)
        .setParameter("usuarioId", usuarioId)
        .setParameter("tipoClase", tipoClase)
        .getSingleResult();
  }

  public List<Suscripcion> buscarTodasPorPersonaYHeladera(Long personaId, Long heladeraId) {
    return entityManager()
        .createQuery("SELECT s FROM Suscripcion s WHERE s.suscriptor.id = :personaId AND s.heladera.id = :heladeraId", Suscripcion.class)
        .setParameter("personaId", personaId)
        .setParameter("heladeraId", heladeraId)
        .getResultList();
  }

  public boolean existePorPersonaYHeladera(Long personaId, Long heladeraId) {
    Long count = entityManager()
        .createQuery("SELECT COUNT(s) FROM " + Suscripcion.class.getName() + " s "
            + "WHERE s.suscriptor.id = :personaId AND s.heladera.id = :heladeraId", Long.class)
        .setParameter("personaId", personaId)
        .setParameter("heladeraId", heladeraId)
        .getSingleResult();

    return count > 0;
  }

  public void eliminarPorPersonaYHeladera(Long personaId, Long heladeraId) {
    entityManager()
        .createQuery("DELETE FROM Suscripcion s WHERE s.suscriptor.id = :personaId AND s.heladera.id = :heladeraId")
        .setParameter("personaId", personaId)
        .setParameter("heladeraId", heladeraId)
        .executeUpdate();
  }

}
