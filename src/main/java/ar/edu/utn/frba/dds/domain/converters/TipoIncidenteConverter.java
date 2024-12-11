package ar.edu.utn.frba.dds.domain.converters;

import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Alerta;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.FallaTecnica;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.TipoIncidente;
import javax.persistence.AttributeConverter;

public class TipoIncidenteConverter implements AttributeConverter<TipoIncidente, String> {
  @Override
  public String convertToDatabaseColumn(TipoIncidente tipo) {
    if (tipo instanceof Alerta) {
      return "ALERTA";
    } else if (tipo instanceof FallaTecnica) {
      return "FALLA_TECNICA";
    }
    return null;
  }

  @Override
  public TipoIncidente convertToEntityAttribute(String dbData) {
    if ("ALERTA".equals(dbData)) {
      return new Alerta();
    } else if ("FALLA_TECNICA".equals(dbData)) {
      return new FallaTecnica();
    }
    return null;
  }

}
