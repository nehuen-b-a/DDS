package ar.edu.utn.frba.dds.domain.entities.documento;

import ar.edu.utn.frba.dds.dtos.DocumentoDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class Documento {
  @Column(name = "nroDocumento")
  private String numero;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipoDocumento")
  private TipoDocumento tipo;

  public Documento(TipoDocumento tipo, String numero){
    this.tipo = tipo;
    this.numero = numero;
  }
  
  public static Documento fromDTO(DocumentoDTO dto) {
    return fromDTO(dto, true);
  }

  public static Documento fromDTO(DocumentoDTO dto, boolean validarObligatorio) {
    if(validarObligatorio) {
      validarDocumento(dto);
    }

    Documento documento = new Documento();
    documento.setNumero(dto.getNroDocumento());
    documento.setTipo(TipoDocumento.valueOf(dto.getTipoDocumento()));
    return documento;
  }

  private static void validarDocumento(DocumentoDTO dto) {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("numero de documento", dto.getNroDocumento()),
        Pair.of("tipo de documento", dto.getTipoDocumento())
    );

    if (!verificarDocumento(dto.getNroDocumento(), TipoDocumento.valueOf(dto.getTipoDocumento()))) {
      throw new ValidacionFormularioException("Número de documento inválido");
    }
  }

  public static boolean verificarDocumento(String nroDocumento, TipoDocumento tipoDocumento) {
    switch (tipoDocumento) {
      case DNI:
        return validarDNI(nroDocumento);
      case LC:
        return validarLibretaCivica(nroDocumento);
      case LE:
        return validarLibretaEnrolamiento(nroDocumento);
      case PASAPORTE:
        return validarPasaporte(nroDocumento);
      default:
        throw new IllegalArgumentException("Tipo de documento no soportado");
    }
  }

  private static boolean validarDNI(String nroDocumento) {
    return nroDocumento.matches("^\\d{7,8}$");
  }

  private static boolean validarLibretaCivica(String nroDocumento) {
    return nroDocumento.matches("^\\d{7,8}$");
  }

  private static boolean validarLibretaEnrolamiento(String nroDocumento) {
    return nroDocumento.matches("^\\d{7,8}$");
  }

  private static boolean validarPasaporte(String nroDocumento) {
    return nroDocumento.matches("^[a-zA-Z0-9]{6,9}$");
  }
}
