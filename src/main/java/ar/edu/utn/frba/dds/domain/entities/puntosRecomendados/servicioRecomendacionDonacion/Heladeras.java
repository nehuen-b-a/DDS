package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Data;

import java.util.List;

public class Heladeras {
    public List<HeladeraGrabada> heladeras;

    public Heladeras() {
        this.heladeras = new ArrayList<>();
    }

    @Data
    public static class HeladeraGrabada {
        public int cantidad_viandas;
        public RecomendacionDireccion direccion;
    }

    public void setHeladeras(HeladeraGrabada ...heladerasGrabadas) {
        Collections.addAll(heladeras, heladerasGrabadas);
    }
}
