package ar.edu.utn.frba.dds.domain.entities.ubicacion;

import com.google.gson.annotations.Expose;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Coordenada {
  @Column(name = "latitud") @Expose
  public String latitud;
  @Column(name = "longitud") @Expose
  public String longitud;

  public Coordenada(String latitud, String longitud) {
    this.latitud = latitud;
    this.longitud = longitud;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coordenada that = (Coordenada) o;
    return Objects.equals(latitud, that.latitud) &&
        Objects.equals(longitud, that.longitud);
  }
}

