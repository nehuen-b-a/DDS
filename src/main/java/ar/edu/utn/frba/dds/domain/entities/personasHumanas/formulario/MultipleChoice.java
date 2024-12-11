package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@DiscriminatorValue("multipleChoice")
public class MultipleChoice extends Pregunta {
  @ManyToMany
  @JoinTable(name = "opcion_pregunta",
      joinColumns = @JoinColumn(name = "pregunta_id",
          referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "opcion_id", referencedColumnName = "id")
  )
  private Set<Opcion> opciones;

  public MultipleChoice(/*String campo, boolean activa*/) {
    //this.campo = campo;
    //this.activa = activa;
    this.opciones = new HashSet<>();
  }

  public void agregarOpciones(Opcion ...opciones) {
    Collections.addAll(this.opciones, opciones);
  }

  @Override
  public boolean esValida(String respuesta) {
    return this.opciones.contains(respuesta);
  }
}
