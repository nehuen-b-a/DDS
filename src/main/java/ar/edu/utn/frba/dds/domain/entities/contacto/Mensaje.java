package ar.edu.utn.frba.dds.domain.entities.contacto;

import ar.edu.utn.frba.dds.domain.converters.LocalDateTimeAttributeConverter;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Entity @Table(name="mensaje")
@NoArgsConstructor
public class Mensaje {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name = "asunto", nullable = false)
  private String asunto;

  @Column(name = "cuerpo", columnDefinition = "TEXT", nullable = false)
  private String cuerpo;

  @Convert(converter = LocalDateTimeAttributeConverter.class)
  @Column(name = "fechaEmision", nullable = false)
  private LocalDateTime fechaEmision;

  @ManyToOne
  @JoinColumn(name="usuarioDestinatario_id", referencedColumnName="id", nullable = false)
  private Usuario destinatario;

  @ManyToOne
  @JoinColumn(name="usuarioEmisor_id", referencedColumnName="id", nullable = false)
  private Usuario emisor;

  public Mensaje(String asunto, String cuerpo, LocalDateTime fecha) {
    this.asunto = asunto;
    this.cuerpo = cuerpo;
    this.fechaEmision = fecha;
  }
}