package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.dtos.ModeloDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter @Setter
@Entity @Table(name = "modelo")
@NoArgsConstructor
public class Modelo {
  @Id @GeneratedValue
  private Long id;

  @Column(name="modelo", nullable = false)
  private String modelo;

  @Column(name="temperaturaMinima", nullable = false)
  private float temperaturaMinima;

  @Column(name="temperaturaMaxima", nullable = false)
  private float temperaturaMaxima;

  public Modelo(String modelo, float temperaturaMinima, float temperaturaMaxima) {
    this.modelo = modelo;
    this.temperaturaMinima = temperaturaMinima;
    this.temperaturaMaxima = temperaturaMaxima;
  }

  public static Modelo fromDTO(ModeloDTO dto) {
    validarModelo(dto);

    Modelo modelo = new Modelo();
    modelo.setModelo(dto.getNombre());
    modelo.setTemperaturaMinima(Float.parseFloat(dto.getTemperaturaMinima()));
    modelo.setTemperaturaMaxima(Float.parseFloat(dto.getTemperaturaMaxima()));
    return modelo;
  }

  private static void validarModelo(ModeloDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre del modelo", dto.getNombre()),
        Pair.of("temperatura mínima", dto.getTemperaturaMinima()),
        Pair.of("temperatura máxima", dto.getTemperaturaMaxima())
    );

    try {
      float tempMin = Float.parseFloat(dto.getTemperaturaMinima());
      float tempMax = Float.parseFloat(dto.getTemperaturaMaxima());

      if (tempMin > tempMax) {
        throw new ValidacionFormularioException("La temperatura mínima no puede ser mayor que la máxima.");
      }
    } catch (NumberFormatException e) {
      throw new ValidacionFormularioException("Las temperaturas deben ser valores numéricos válidos.");
    }
  }
}
