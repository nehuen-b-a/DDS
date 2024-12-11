package ar.edu.utn.frba.dds.domain;

import ar.edu.utn.frba.dds.domain.adapters.LocalDateAdapter;
import ar.edu.utn.frba.dds.domain.adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GsonFactory {
    public static Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .excludeFieldsWithoutExposeAnnotation() // Solo incluirá los campos con @Expose
            .create();
    }
}
