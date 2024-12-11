package ar.edu.utn.frba.dds.domain.entities.tecnicos;

import ar.edu.utn.frba.dds.domain.entities.contacto.Contacto;

import ar.edu.utn.frba.dds.domain.entities.documento.Documento;
import ar.edu.utn.frba.dds.domain.entities.documento.TipoDocumento;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Direccion;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.dtos.DocumentoDTO;
import ar.edu.utn.frba.dds.dtos.TecnicoDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter @Setter
@Entity @Table(name="tecnico")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Tecnico {
  @Id
  @GeneratedValue
  private Long id;

  @Column(name="nombre", nullable = false)
  private String nombre;

  @Column(name="apellido", nullable = false)
  private String apellido;

  @OneToOne
  @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false)
  private Usuario usuario;

  @Embedded
  private Documento documento;

  @Column(name="cuil", nullable = false, unique = true)
  private String cuil;

  @Embedded
  private Contacto contacto;

  @Embedded
  private Direccion direccion;

  @Column(name="distanciaMaximaEnKMParaSerAvisado")
  private double distanciaMaximaEnKmParaSerAvisado;


  public static Tecnico fromDTO(TecnicoDTO dto) {
    validarCamposObligatorios(dto);
    validarLongitudNombreYApellido(dto);
    validarFormatoCUIL(dto);
    validarRadioMaximo(dto);

    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Contacto contacto = Contacto.fromDTO(dto.getContactoDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO());

    return Tecnico.builder()
        .nombre(dto.getNombre())
        .apellido(dto.getApellido())
        .cuil(dto.getCuil())
        .documento(documento)
        .distanciaMaximaEnKmParaSerAvisado(dto.getRadioMaximoParaSerAvisado())
        .direccion(direccion)
        .contacto(contacto)
        .build();
  }

  private static void validarCamposObligatorios(TecnicoDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("nombre", dto.getNombre()),
        Pair.of("apellido", dto.getApellido()),
        Pair.of("CUIL", dto.getCuil()),
        Pair.of("radio máximo para ser avisado", String.valueOf(dto.getRadioMaximoParaSerAvisado()))
    );
  }

  private static void validarLongitudNombreYApellido(TecnicoDTO dto) {
    if (dto.getNombre().length() < 2 || dto.getNombre().length() > 50) {
      throw new ValidacionFormularioException("El nombre debe tener entre 2 y 50 caracteres.");
    }
    if (dto.getApellido().length() < 2 || dto.getApellido().length() > 50) {
      throw new ValidacionFormularioException("El apellido debe tener entre 2 y 50 caracteres.");
    }
  }

  private static void validarFormatoCUIL(TecnicoDTO dto) {
    if (!dto.getCuil().matches("^\\d{2}-\\d{8}-\\d{1}$")) {
      throw new ValidacionFormularioException("El formato del CUIL es incorrecto. Debe ser XX-XXXXXXXX-X.");
    }
  }

  private static void validarRadioMaximo(TecnicoDTO dto) {
    if (dto.getRadioMaximoParaSerAvisado() <= 0) {
      throw new ValidacionFormularioException("El radio máximo debe ser un número positivo.");
    }
  }

  public void actualizarFromDto(TecnicoDTO dto) {
    validarCamposObligatorios(dto);
    validarFormatoCUIL(dto);
    validarRadioMaximo(dto);
    validarLongitudNombreYApellido(dto);

    this.nombre = dto.getNombre();
    this.apellido = dto.getApellido();
    this.cuil = dto.getCuil();
    this.distanciaMaximaEnKmParaSerAvisado = dto.getRadioMaximoParaSerAvisado();
    Direccion direccion = Direccion.fromDTO(dto.getDireccionDTO());
    Documento documento = Documento.fromDTO(dto.getDocumentoDTO());
    Contacto contacto = Contacto.fromDTO(dto.getContactoDTO());

    if (!this.direccion.equals(direccion)) {
      this.setDireccion(direccion);
    }

    if (!this.documento.equals(documento)) {
      this.setDocumento(documento);
    }

    if (!this.contacto.equals(contacto)) {
      this.setContacto(contacto);
    }
  }
}