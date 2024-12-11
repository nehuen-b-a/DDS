package ar.edu.utn.frba.dds.domain.entities.ubicacion;

import java.util.StringTokenizer;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@Embeddable
public class Calle {
    private String calle;

    public Calle(String calle) {
        this.calle = calle;
    }
}
