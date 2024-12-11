package ar.edu.utn.frba.dds.domain.entities.viandas;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name="distribucion_vianda")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class DistribucionVianda implements Contribucion {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "id_heladeraOrigen", referencedColumnName = "id")
  private Heladera heladeraOrigen;

  @ManyToOne
  @JoinColumn(name = "id_heladeraDestino", referencedColumnName = "id")
  private Heladera heladeraDestino;

  @ManyToOne
  @JoinColumn(name = "id_personaHumana", referencedColumnName = "id", nullable = false)
  private PersonaHumana colaborador;

  @Column(name="cantidadViandas", nullable = false)
  private int cantidadViandas;

  @Column(name="motivo", columnDefinition = "TEXT", nullable = false)
  private String motivo;

  @Column(name="fecha", columnDefinition = "DATE", nullable = false)
  private LocalDate fecha;

  @Column(name="terminada", columnDefinition = "BIT(1)", nullable = false)
  private boolean terminada;

  @ManyToMany
  @JoinTable(
      name = "vianda_por_distribucion",
      joinColumns = @JoinColumn(name = "id_distribucion_vianda",
          referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "id_vianda", referencedColumnName = "id")
  )
  private List<Vianda> viandasAMover;

  public DistribucionVianda(LocalDate fecha, int cantidadViandas){
    this.fecha = fecha;
    this.cantidadViandas = cantidadViandas;
  }

  public void distribuir(List<Vianda> viandas) {
    heladeraOrigen.quitarViandas(viandas);
    heladeraDestino.ingresarViandas(viandas);
    // FIXME: Esto sigue siendo asi? Los metodos estos de quitar viandas en plural no deberiamos cambiarlos?
  }

  public float calcularPuntaje() {
    float coeficiente = ReconocimientoTrabajoRealizado.getInstance().obtenerCoeficientes("coeficienteViandasDistribuidas");
    return cantidadViandas * coeficiente;
  }

  public TipoContribucion obtenerTipoContribucion() {
    return TipoContribucion.DISTRIBUCION_VIANDAS;
  }

  public LocalDate obtenerFechaRegistro() {
    return this.fecha;
  }

  @Override
  public boolean equals(Object o){
    if (o == this) {
      return true;
    }

    if (!(o instanceof DistribucionVianda)) {
      return false;
    }

    DistribucionVianda distribucion = (DistribucionVianda) o;

    return this.fecha.equals(distribucion.fecha)
        && this.cantidadViandas == distribucion.cantidadViandas;
  }
}
