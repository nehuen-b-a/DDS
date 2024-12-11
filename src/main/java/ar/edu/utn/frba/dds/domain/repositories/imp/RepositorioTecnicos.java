package ar.edu.utn.frba.dds.domain.repositories.imp;

import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Municipio;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import java.util.Optional;

public class RepositorioTecnicos extends Repositorio {
    public Optional<Incidente> buscarIncidente(Long idHeladera) {
        return entityManager()
            .createQuery("SELECT i FROM " + Incidente.class.getName()
                + " i WHERE i.heladera.id = :id AND i.solucionado = 0", Incidente.class)
            .setParameter("id", idHeladera)
            .getResultList().stream().findFirst();
    }
}
