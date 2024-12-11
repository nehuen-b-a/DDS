package ar.edu.utn.frba.dds.domain.entities.tarjetas;

import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.ReconocimientoTrabajoRealizado;
import ar.edu.utn.frba.dds.domain.entities.TipoContribucion;
import ar.edu.utn.frba.dds.domain.entities.viandas.DistribucionVianda;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@Entity
@Table(name = "tarjeta")
@Getter
public class Tarjeta implements Contribucion {

  @Id @GeneratedValue
  private long id;

  @Setter
  @Column(name = "codigo", nullable = false)
  private String codigo;

  @Setter
  @Column(name = "fechaRecepcionPersonaVulnerable", columnDefinition = "DATE")
  private LocalDate fechaRecepcionPersonaVulnerable;

  @Setter
  @Column(name = "fechaRecepcionColaborador", columnDefinition = "DATE")
  private LocalDate fechaRecepcionColaborador;

  @Setter
  @Column(name = "fechaDeBaja", columnDefinition = "DATE")
  private LocalDate fechaBaja;

  @OneToMany
  @JoinColumn(name = "tarjeta_id", referencedColumnName = "id")
  private List<UsoDeTarjeta> historialUsos;

  @Transient
  private static final int HASH_LENGTH = 11;

  public Tarjeta() {
    this.historialUsos = new ArrayList<>();
    this.codigo = generarCodigo();
  }

  public float calcularPuntaje() {
    float coeficiente = ReconocimientoTrabajoRealizado.getInstance().obtenerCoeficientes("coeficienteTarjetasRepartidas");
    return coeficiente;
  }

  public TipoContribucion obtenerTipoContribucion() {
    return TipoContribucion.ENTREGA_TARJETAS;
  }

  public LocalDate obtenerFechaRegistro() {
    return null;
  }

  public void agregarUso(UsoDeTarjeta uso) {
    this.historialUsos.add(uso);
  }

  public static String generarCodigo() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[8];
    secureRandom.nextBytes(randomBytes);
    String hash = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    return hash.length() > HASH_LENGTH ? hash.substring(0, HASH_LENGTH) : hash;
  }

  public int cantidadDeUsos(LocalDate dia){
    LocalDate hoy = LocalDate.now();
    return (int) this.historialUsos.stream()
        .filter(uso -> uso.getFecha().isEqual(dia))
        .count();
  }
}
