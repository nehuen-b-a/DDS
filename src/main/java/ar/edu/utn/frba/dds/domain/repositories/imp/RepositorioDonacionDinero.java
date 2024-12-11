package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.donacionesDinero.DonacionDinero;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.List;

public class RepositorioDonacionDinero extends Repositorio {
  public List<DonacionDinero> buscarDonacionesDeDineroDe(Long idUsuario, TipoRol tipoRol) {
    String joinField = tipoRol == TipoRol.PERSONA_HUMANA ? "personaHumana" : "personaJuridica";

    return entityManager()
        .createQuery("SELECT v FROM " + DonacionDinero.class.getName() + " v JOIN v." + joinField + " p WHERE p.usuario.id = :id", DonacionDinero.class)
        .setParameter("id", idUsuario)
        .getResultList();
  }
}
