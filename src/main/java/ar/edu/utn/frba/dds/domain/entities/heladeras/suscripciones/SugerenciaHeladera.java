package ar.edu.utn.frba.dds.domain.entities.heladeras.suscripciones;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="sugerencia_heladeras")
public class SugerenciaHeladera {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name="fechaRealizacion", nullable = false)
  private LocalDate fechaRealizacion;
  
  @OneToOne
  @JoinColumn(name = "incidente_id", referencedColumnName = "id")
  private Incidente incidente;

  @ManyToMany
  @JoinTable(
      name = "sugerencia_distribucion",
      joinColumns = @JoinColumn(name = "id_sugerencia_heladeras",
          referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "id_heladera", referencedColumnName = "id")
  )
  private List<Heladera> heladerasSugeridas;

  public void agregarHeladera(Heladera heladera) {
    heladerasSugeridas.add(heladera);
  }
}
