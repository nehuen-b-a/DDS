package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Municipio;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;

public class RepositorioGeoRef extends Repositorio {
  public List<Municipio> buscarMunicipiosDe(String provincia) {
    return entityManager()
        .createQuery("SELECT m FROM " + Municipio.class.getName() + " m JOIN m.provincia p WHERE p.provincia = :provincia", Municipio.class)
        .setParameter("provincia", provincia)
        .getResultList();
  }
}
