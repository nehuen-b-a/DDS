package ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface IRecomendacionDonacion {
    @GET("personas_vulnerables")
    Call<List<RecomendacionPersonasVulnerables>> recomendacionPersonasVulnerables(@Query("radio_max") float radio_max, @Query("calle") String calle, @Query("altura") String altura, @Query("provincia") String provincia);

    @GET("heladeras")
    Call<List<RecomendacionHeladeras>> recomendacionHeladeras(@Query("radio_max") float radio_max, @Query("stock_minimo") int stock_minimo, @Query("calle") String calle, @Query("altura") String altura, @Query("provincia") String provincia);

    @POST("personas_vulnerables")
    Call<List<PersonaGrabada>> grabarPersonas(@Body Personas personas);

    @POST("heladeras")
    Call<List<HeladeraGrabada>> grabarHeladeras(@Body Heladeras heladeras);
}
