package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;


public class Personas {
    public List<Persona> personas;

    public Personas() {
        this.personas = new ArrayList<>();
    }

    @Data
    public static class Persona {
        public String nombre;
        public String apellido;
        public RecomendacionDireccion direccion;
        public List<Persona> hijos;

        public Persona() {
            this.hijos = new ArrayList<>();
        }
    }

    public void setPersonas(Persona ...personasAGrabar) {
        Collections.addAll(personas, personasAGrabar);
    }
}
