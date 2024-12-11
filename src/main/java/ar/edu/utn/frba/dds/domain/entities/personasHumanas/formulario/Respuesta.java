package ar.edu.utn.frba.dds.domain.entities.personasHumanas.formulario;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "respuesta")
public class Respuesta {
  @Id
  @GeneratedValue
  private long id;

  @ManyToOne
  @JoinColumn(name = "pregunta_id", referencedColumnName = "id", nullable = false)
  private Pregunta pregunta;

  @Column(name = "respuestaLibre", columnDefinition = "TEXT")
  private String respuestaLibre;
  @ManyToOne
  @JoinColumn(name = "personaHumana_id", referencedColumnName= "id", nullable = false)
  private PersonaHumana personaHumana;

  @ManyToMany
  @JoinTable(name = "opcion_respuesta",
      joinColumns = @JoinColumn(name = "respuesta_id",
          referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "opcion_id", referencedColumnName = "id"))
  private Set<Opcion> opcionesElegidas;

  public Respuesta(){
    this.opcionesElegidas = new HashSet<>();
  }
  public String getContenido(){return this.respuestaLibre;}
}
