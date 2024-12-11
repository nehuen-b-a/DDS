package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.exceptions.MensajeAmigableException;
import io.javalin.http.Context;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import java.io.File;
import java.util.ArrayList;

public class ControladorReportes {
  private final String rutaReportes = "admin/adminReportes.hbs";
  private final String rutaBaseReportes = "src/main/resources/reportes";

  public void index(Context context) {
    List<Reporte> reportes = new ArrayList<>();

    File baseDir = new File(rutaBaseReportes);
    File[] carpetas = baseDir.listFiles(File::isDirectory);

    if (carpetas != null) {
      for (File carpeta : carpetas) {
        Reporte reporte = new Reporte();
        reporte.setFecha(carpeta.getName());

        reporte.setRutaFallas("/reportes/" + carpeta.getName() + "/cantidad-fallas-heladera.pdf");
        reporte.setRutaViandasRetiradas("/reportes/" + carpeta.getName() + "/cantidad-viandas-heladera.pdf");
        reporte.setRutaViandasDonadas("/reportes/" + carpeta.getName() + "/cantidad-viandas-colaborador.pdf");

        reportes.add(reporte);
      }
    }

    Map<String, Object> model = new HashMap<>();
    model.put("reportes", reportes);

    context.render(rutaReportes, model);
  }

  public void verReporte(Context context) {
    String carpeta = context.pathParam("carpeta");
    String archivo = context.pathParam("archivo");
    File file = new File(rutaBaseReportes + "/" + carpeta + "/" + archivo);

    if (!file.exists()) {
      throw new MensajeAmigableException("No existe un archivo en el sistema con ese nombre.", 400);
    }

    context.contentType("application/pdf");
    try {
      context.result(new FileInputStream(file));
    } catch (Exception e) {
      throw new MensajeAmigableException("No se pudo mostrar el archivo.", 400);
    }
  }

  @Data
  public static class Reporte {
    private String fecha;
    private String rutaFallas;
    private String rutaViandasRetiradas;
    private String rutaViandasDonadas;
  }
}
