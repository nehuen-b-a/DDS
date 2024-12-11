package ar.edu.utn.frba.dds.dtos.inputs.personasHumanas;

import java.time.LocalDate;
import lombok.Data;

@Data
public class PersonaHumanaInputDTO {
  private String documentoId;
  private String nombre;
  private String apellido;
  private String mail;
}
