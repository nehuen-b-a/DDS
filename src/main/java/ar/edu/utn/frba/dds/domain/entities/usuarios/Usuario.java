package ar.edu.utn.frba.dds.domain.entities.usuarios;

import ar.edu.utn.frba.dds.domain.entities.validador.AusenciaDeCredencialesPorDefecto;
import ar.edu.utn.frba.dds.domain.entities.validador.ListaDePeoresClavesMemorizadas;
import ar.edu.utn.frba.dds.domain.entities.validador.LongitudEstipulada;
import ar.edu.utn.frba.dds.domain.entities.validador.ValidadorDeClave;
import ar.edu.utn.frba.dds.dtos.UsuarioDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;
import org.mindrot.jbcrypt.BCrypt;

@Getter @Setter
@Entity @Table(name = "usuario")
@NoArgsConstructor
public class Usuario {
  @Id
  @GeneratedValue
  private Long id;
  @Column(name = "nombre", nullable = false, unique = true)
  private String nombre;
  @Column(name = "clave", nullable = false)
  private String clave;
  @ManyToOne
  @JoinColumn(name = "rol_id", referencedColumnName = "id", nullable = false)
  private Rol rol;

  public Usuario(String nombre) {
    this.nombre = nombre;
  }

  public void setClaveEncriptada(String clave) {
    this.clave = BCrypt.hashpw(clave, BCrypt.gensalt());
  }

  public boolean verificarClave(String claveIngresada) {
    return BCrypt.checkpw(claveIngresada, this.clave);
  }

  public static Usuario fromDTO(UsuarioDTO dto) {
    validarCamposObligatorios(dto);
    validarLongitudNombre(dto);
    validarCoincidenciaClaves(dto);
    validarComplejidadClave(dto);

    Usuario usuario = new Usuario();
    usuario.setNombre(dto.getNombre());
    usuario.setClaveEncriptada(dto.getClave());

    return usuario;
  }

  private static void validarCamposObligatorios(UsuarioDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre", dto.getNombre()),
        Pair.of("clave", dto.getClave()),
        Pair.of("clave repetida", dto.getClaveRepetida()),
        Pair.of("rol", dto.getRol())
    );
  }

  private static void validarLongitudNombre(UsuarioDTO dto) {
    if (dto.getNombre().length() < 5 || dto.getNombre().length() > 50) {
      throw new ValidacionFormularioException(String.format(
          "La longitud del nombre de usuario debe estar entre 5 y 50 caracteres. Longitud actual: %d caracteres.",
          dto.getNombre().length()
      ));
    }
  }

  private static void validarCoincidenciaClaves(UsuarioDTO dto) {
    if (!dto.getClave().equals(dto.getClaveRepetida())) {
      throw new ValidacionFormularioException("La clave y la confirmación de clave no coinciden. Asegúrate de ingresar la misma clave en ambos campos.");
    }
  }

  private static void validarComplejidadClave(UsuarioDTO dto) {
    int longitud = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("longitud_maxima_clave"));
    ValidadorDeClave validador = new ValidadorDeClave(
        new LongitudEstipulada(longitud),
        new ListaDePeoresClavesMemorizadas(),
        new AusenciaDeCredencialesPorDefecto(dto.getNombre())
    );
    if (!validador.validar(dto.getClave())) {
      throw new ValidacionFormularioException(validador.getErroresFinales());
    }
  }
}


