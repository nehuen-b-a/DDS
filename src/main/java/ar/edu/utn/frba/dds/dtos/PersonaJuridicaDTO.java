package ar.edu.utn.frba.dds.dtos;

import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.FormasContribucionJuridicas;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import io.javalin.http.Context;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonaJuridicaDTO implements DTO {
  private Long id;
  private String razonSocial;
  private String tipo;
  private String rubro;
  private ContactoDTO contactoDTO;
  private DireccionDTO direccionDTO;
  private Set<FormasContribucionJuridicas> contribucionesElegidas = new HashSet<>();
  private boolean donacionDinero;
  private boolean encargarseHeladera;
  private boolean ofrecerOferta;

  public PersonaJuridicaDTO(PersonaJuridica personaJuridica) {
    this.razonSocial = personaJuridica.getRazonSocial();
    this.tipo = personaJuridica.getTipo().name();
    this.rubro = personaJuridica.getRubro().getNombre();
    this.direccionDTO = new DireccionDTO(personaJuridica.getDireccion());
    this.contactoDTO = new ContactoDTO(personaJuridica.getContacto());
    this.id = personaJuridica.getId();

    // Completar el valor de donacionDinero, encargarseHeladera y ofrecerOferta
    for (FormasContribucionJuridicas contribucion : personaJuridica.getContribucionesElegidas()) {
      contribucionesElegidas.add(contribucion);
      switch (contribucion) {
        case DONACION_DINERO:
          this.donacionDinero = true;
          break;
        case ENCARGARSE_DE_HELADERA:
          this.encargarseHeladera = true;
          break;
        case OFRECER_OFERTA:
          this.ofrecerOferta = true;
          break;
        default:
          break; // No se necesita manejar otros casos
      }
    }
  }

  @Override
  public void obtenerFormulario(Context context) {
    this.razonSocial = context.formParam("razonSocial");
    this.tipo = context.formParam("tipoPersonaJuridica");
    this.rubro = context.formParam("rubro");
    this.contactoDTO = new ContactoDTO();
    this.contactoDTO.obtenerFormulario(context);
    this.direccionDTO = new DireccionDTO();
    this.direccionDTO.obtenerFormulario(context);

    String[] contribucionesSeleccionadas = context.formParams("formaColaboracion").toArray(new String[0]);
    for (String contribucion : contribucionesSeleccionadas) {
      contribucionesElegidas.add(FormasContribucionJuridicas.valueOf(contribucion));
      switch (contribucion) {
        case "DONACION_DINERO":
          this.donacionDinero = true;
          break;
        case "ENCARGARSE_DE_HELADERA":
          this.encargarseHeladera = true;
          break;
        case "OFRECER_OFERTA":
          this.ofrecerOferta = true;
          break;
        default:
          break; // No se necesita manejar otros casos
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof PersonaJuridicaDTO)) return false;
    PersonaJuridicaDTO that = (PersonaJuridicaDTO) obj;
    return Objects.equals(razonSocial, that.razonSocial) &&
        Objects.equals(tipo, that.tipo) &&
        Objects.equals(rubro, that.rubro) &&
        Objects.equals(contactoDTO, that.contactoDTO) &&
        Objects.equals(direccionDTO, that.direccionDTO);
  }

  @Override
  public int hashCode() {
    return Objects.hash(razonSocial, tipo, rubro, contactoDTO, direccionDTO);
  }
}