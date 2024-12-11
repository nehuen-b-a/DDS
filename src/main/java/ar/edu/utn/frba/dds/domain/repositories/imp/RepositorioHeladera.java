package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Municipio;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;
import java.util.Optional;
import javax.persistence.TypedQuery;

public class RepositorioHeladera extends Repositorio {

  public List<Heladera> recomendarHeladeras(Direccion direccion) {
    return entityManager().createQuery("from Heladera", Heladera.class)
            .getResultList().stream()
            .filter(heladera -> heladera.getDireccion().estaCercaDe(direccion))
            .toList();
  }

  public Optional<Heladera> buscarPorId(Long id) {
    return buscarPorId(id, Heladera.class);
  }

  public List<Heladera> buscarPorUsuario(Long idUsuario) {
    return entityManager()
        .createQuery("SELECT DISTINCT h FROM " + Heladera.class.getName() + " h " +
            "WHERE h.id IN (SELECT ph.id FROM PersonaJuridica pj JOIN pj.heladerasAcargo ph WHERE pj.usuario.id = :id)", Heladera.class)
        .setParameter("id", idUsuario)
        .getResultList();
  }

  public List<Heladera> buscarHeladerasConAlertaPorUsuario(Long idUsuario) {
    return entityManager()
        .createQuery("SELECT DISTINCT h FROM " + Heladera.class.getName() + " h " +
            "JOIN Incidente i ON h.id = i.heladera " +
            "WHERE h.id IN (SELECT ph.id FROM PersonaJuridica pj JOIN pj.heladerasAcargo ph WHERE pj.usuario.id = :id) AND i.solucionado = :solucionado", Heladera.class)
        .setParameter("id", idUsuario)
        .setParameter("solucionado", false)
        .getResultList();
  }
}