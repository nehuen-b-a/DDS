package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.domain.converters.LocalDateAttributeConverter;
import java.time.LocalDate;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Enumerated;

@Getter @Setter
@Entity @Table (name = "cambio_estado")
@NoArgsConstructor
public class CambioEstado {
  @Id @GeneratedValue
  private long id;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoHeladera estado;

  @Convert(converter = LocalDateAttributeConverter.class)
  @Column(name = "fechaCambio")
  private LocalDate fechaCambio;

  public CambioEstado(EstadoHeladera estado, LocalDate fechaCambio) {
    this.estado = estado;
    this.fechaCambio = fechaCambio;
  }

  public boolean esUnaFalla() {
    return !this.estado.equals(EstadoHeladera.ACTIVA);
  }
}
