package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.oferta.Oferta;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.time.LocalDateTime;
import java.util.List;

public class RepositorioOferta extends Repositorio {
  public List<Oferta> buscarPorFecha(LocalDateTime fecha) {
    return entityManager().createQuery("from" + Oferta.class.getName() + "fechaCanje = " + fecha).getResultList();
  }
  public List<Oferta> buscarPorPersonaJuridica(Long idJuridica){
    return entityManager().createQuery("from " + Oferta.class.getName() + " WHERE persona_juridica_id = :dato")
        .setParameter("dato", idJuridica)
        .getResultList();
  }
}
