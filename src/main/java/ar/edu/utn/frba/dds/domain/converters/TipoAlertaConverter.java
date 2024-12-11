package ar.edu.utn.frba.dds.domain.converters;

import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.TipoAlerta;
import javax.persistence.AttributeConverter;

public class TipoAlertaConverter implements AttributeConverter<TipoAlerta, String> {
  @Override
  public String convertToDatabaseColumn(TipoAlerta tipo) {
    switch (tipo) {
      case FRAUDE -> {
        return "FRAUDE";
      }

      case FALLA_CONEXION -> {
        return "FALLA_CONEXION";
      }

      case FALLA_TEMPERATURA -> {
        return "FALLA_TEMPERATURA";
      }
    }

    return null;
  }

  @Override
  public TipoAlerta convertToEntityAttribute(String dbData) {
    if ("FRAUDE".equals(dbData)) {
      return TipoAlerta.FRAUDE;
    } else if ("FALLA_CONEXION".equals(dbData)) {
      return TipoAlerta.FALLA_CONEXION;
    } else if ("FALLA_TEMPERATURA".equals(dbData)) {
      return TipoAlerta.FALLA_TEMPERATURA;
    }
    return null;
  }

}