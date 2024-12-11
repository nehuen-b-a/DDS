package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IRecomendacionPuntosServicio {
  @GET("puntos")
  Call<ListadoPuntosRecomendados> puntos(@Query("radio") float radio, @Query("latitud") String latitud, @Query("longitud") String longitud);
}
