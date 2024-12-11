package ar.edu.utn.frba.dds.utils.manejos;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.Coordenada;

public class ManejoDistancias {
  private static final double RADIO_TIERRA = 6371.01;

  public static double distanciaHaversineEnKm(double lat1, double lon1, double lat2, double lon2) {
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return RADIO_TIERRA * c;
  }

  public static double distanciaHaversineConCoordenadasEnKm(Coordenada coordenada, Coordenada otraCoordenada) {
    double lat1 = Double.parseDouble(coordenada.getLatitud());
    double lon1 = Double.parseDouble(coordenada.getLongitud());
    double lat2 = Double.parseDouble(otraCoordenada.getLatitud());
    double lon2 = Double.parseDouble(otraCoordenada.getLongitud());

    return distanciaHaversineEnKm(lat1, lon1, lat2, lon2);
  }
}
