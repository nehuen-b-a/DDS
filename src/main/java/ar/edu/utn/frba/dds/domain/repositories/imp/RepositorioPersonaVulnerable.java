package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.personasVulnerables.PersonaVulnerable;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;
import java.util.Optional;

public class RepositorioPersonaVulnerable extends Repositorio {
  public Optional<List<PersonaVulnerable>> buscarPersonasDe(Long idUsuario) {
    List<PersonaVulnerable> resultados = entityManager().createQuery(
            "SELECT pv FROM PersonaVulnerable pv "
                + "JOIN pv.personaQueLoRegistro ph "
                + "JOIN ph.usuario u "
                + "WHERE u.id = :idUsuario", PersonaVulnerable.class)
        .setParameter("idUsuario", idUsuario)
        .getResultList();
    return Optional.of(resultados);
  }
}
