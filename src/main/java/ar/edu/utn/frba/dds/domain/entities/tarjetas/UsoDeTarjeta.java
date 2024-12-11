package ar.edu.utn.frba.dds.domain.entities.tarjetas;

import ar.edu.utn.frba.dds.domain.converters.LocalDateTimeAttributeConverter;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "uso_de_tarjeta")
@NoArgsConstructor
@Data
public class UsoDeTarjeta {

  @Id
  @GeneratedValue
  private long id;

  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @Column(name = "fechaDeUso", nullable = false)
  private LocalDateTime fechaDeUso;

  @ManyToOne
  @JoinColumn(name = "heladera_id", referencedColumnName = "id", nullable = false)
  private Heladera heladera;

  public UsoDeTarjeta(LocalDateTime fecha, Heladera heladera) {
    this.fechaDeUso = fecha;
    this.heladera = heladera;
  }

  public LocalDate getFecha(){
    return fechaDeUso.toLocalDate();
  }
}
