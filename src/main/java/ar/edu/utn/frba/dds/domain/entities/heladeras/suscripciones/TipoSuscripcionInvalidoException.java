package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

public class TipoSuscripcionInvalidoException extends RuntimeException {
    public TipoSuscripcionInvalidoException() {
        super("Se ha indicado un tipo de suscripcion invalido.");
    }
}
