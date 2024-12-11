package ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Alerta implements TipoIncidente {
    @Override
    public String obtenerDescripcionIncidente(Incidente incidente) {
        String tipoAlerta = incidente.getTipoAlerta().toString();
        String descripcionColaborador = incidente.getDescripcionDelColaborador();

        return String.format("Se ha detectado una alerta de tipo '%s'. Descripción del incidente: %s.",
            tipoAlerta,
            descripcionColaborador);
    }
}

