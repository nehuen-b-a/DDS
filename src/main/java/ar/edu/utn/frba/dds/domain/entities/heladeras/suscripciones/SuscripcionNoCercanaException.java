package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

public class SuscripcionNoCercanaException extends RuntimeException {
    public SuscripcionNoCercanaException() {
        super("Se ha intentado suscribir a una heladera que no pertenece a su municipio y provincia.");
    }
}
