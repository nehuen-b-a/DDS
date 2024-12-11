package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.domain.converters.LocalDateTimeAttributeConverter;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @Table(name = "cambio_temperatura")
@NoArgsConstructor
public class CambioTemperatura {
  @Id
  @GeneratedValue
  private long id;
  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @Column(name = "fecha", nullable = false)
  private LocalDateTime fecha;
  @Column(name = "temperaturaEnCelsius", nullable = false)
  private float temperaturaCelsius;

  public CambioTemperatura(LocalDateTime fecha, float temperaturaCelsius) {
    this.fecha = fecha;
    this.temperaturaCelsius = temperaturaCelsius;

  }
}
