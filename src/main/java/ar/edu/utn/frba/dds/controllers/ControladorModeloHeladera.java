package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.domain.entities.heladeras.Modelo;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.dtos.ModeloDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class ControladorModeloHeladera implements WithSimplePersistenceUnit {
  private final String rutaAltaHbs = "/colaboraciones/modeloHeladera.hbs";
  private final String rutaHeladera = "/heladeras/nuevo";
  private Repositorio repositorioModelo;

  public ControladorModeloHeladera(Repositorio repositorioModelo) {
    this.repositorioModelo = repositorioModelo;
  }

  public void create(Context context) {
    context.render(rutaAltaHbs);
  }

  public void save(Context context) {
    ModeloDTO dto = new ModeloDTO();
    dto.obtenerFormulario(context);

    try {
      Modelo nuevoModelo = Modelo.fromDTO(dto);

      withTransaction(() -> repositorioModelo.guardar(nuevoModelo));
      context.redirect(rutaHeladera);
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      context.render(rutaAltaHbs, model);
    }
  }
}
