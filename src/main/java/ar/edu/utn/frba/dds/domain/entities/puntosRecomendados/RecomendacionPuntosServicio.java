package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados;

import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecomendacionPuntosServicio {
  private static RecomendacionPuntosServicio instancia = null;
  private static final String urlAPI = "https://52d49f8e-4e4d-4233-8610-c727a6cc27e9.mock.pstmn.io/api/";
  private Retrofit retrofit;

  private RecomendacionPuntosServicio() {
    this.retrofit = new Retrofit.Builder()
        .baseUrl(urlAPI)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
  }

  public static RecomendacionPuntosServicio getInstancia() {
    if (instancia == null) {
      instancia = new RecomendacionPuntosServicio();
    }
    return instancia;
  }

  public ListadoPuntosRecomendados listadoPuntosRecomendados(float radio, String latitud, String longitud) throws IOException {
    IRecomendacionPuntosServicio servicioPuntos = this.retrofit.create(IRecomendacionPuntosServicio.class);
    Call<ListadoPuntosRecomendados> requestPuntos = servicioPuntos.puntos(radio, latitud, longitud);
    Response<ListadoPuntosRecomendados> responsePuntos = requestPuntos.execute();
    return responsePuntos.body();
  }
}
