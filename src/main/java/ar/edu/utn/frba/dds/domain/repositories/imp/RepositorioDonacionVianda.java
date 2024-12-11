package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;

public class RepositorioDonacionVianda extends Repositorio {
  public List<Vianda> buscarViandasDe(Long id) {
    return entityManager()
        .createQuery("SELECT v FROM " + Vianda.class.getName() + " v "
            +" WHERE v.personaHumana.usuario.id = :id ")
        .setParameter("id", id)
        .getResultList();
  }
}
