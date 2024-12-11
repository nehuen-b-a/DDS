package ar.edu.utn.frba.dds.controllers;

import static java.time.LocalTime.now;
import static javax.ws.rs.client.Entity.json;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.domain.entities.Contribucion;
import ar.edu.utn.frba.dds.domain.entities.donacionesDinero.DonacionDinero;
import ar.edu.utn.frba.dds.domain.entities.oferta.Oferta;
import ar.edu.utn.frba.dds.domain.entities.oferta.OfertaCanjeada;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.PersonaJuridica;
import ar.edu.utn.frba.dds.domain.entities.personasJuridicas.Rubro;
import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.servicioRecomendacionDonacion.Personas;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.entities.viandas.DistribucionVianda;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioOferta;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioOfertaCanjeada;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaJuridica;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioRubro;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTecnicos;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioUsuario;
import ar.edu.utn.frba.dds.dtos.OfertaDTO;
import ar.edu.utn.frba.dds.dtos.TecnicoDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.jetty.http.HttpStatus;
import com.google.gson.Gson;

public class ControladorOferta implements WithSimplePersistenceUnit, ICrudViewsHandler {
  private RepositorioOferta repositorioOferta;
  private RepositorioRubro repositorioRubro;
  private RepositorioPersonaJuridica repositorioJuridica;
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private RepositorioOfertaCanjeada repositorioOfertaCanjeada;
  private final String rutaForm = "colaboraciones/ofertas-agregarOferta.hbs";
  private static final Map<String, String> RUTAS = new HashMap<>();

  public ControladorOferta(RepositorioOferta repositorioOferta, RepositorioRubro repositorioRubro
                           ,RepositorioPersonaJuridica repositorioJuridica,
                           RepositorioPersonaHumana repositorioPersonaHumana,
                           RepositorioOfertaCanjeada repositorioOfertaCanjeada) {
    this.repositorioOferta = repositorioOferta;
    this.repositorioRubro = repositorioRubro;
    this.repositorioJuridica = repositorioJuridica;
    this.repositorioPersonaHumana = repositorioPersonaHumana;
    this.repositorioOfertaCanjeada = repositorioOfertaCanjeada;
  }

  static {
    RUTAS.put(TipoRol.PERSONA_HUMANA.name(), "colaboraciones/ofertas-personaHumana.hbs");
    RUTAS.put(TipoRol.PERSONA_JURIDICA.name(), "colaboraciones/ofertas-personaJuridica.hbs");
  }

  @Override
  public void index(Context context) { // validar el usuario.
    //context.sessionAttribute("id");
    String rol = context.sessionAttribute("rol");

    String rutahbs = RUTAS.get(rol);
    Map<String, Object> model = new HashMap<>();
    List<Oferta> ofertas = new ArrayList<>();
    Float puntaje=0F;
    Long id_usuario = context.sessionAttribute("id");
    if(rol.equals(TipoRol.PERSONA_HUMANA.name())){
      Optional<PersonaHumana> personaHumana = repositorioPersonaHumana.buscarPorUsuario(id_usuario);
      puntaje = personaHumana.get().getPuntajeActual();
      model.put("puntos", puntaje);

      ofertas = this.repositorioOferta.buscarTodos(Oferta.class);
      //ofertas.removeIf(oferta -> !oferta.puedeCanjear(personaHumana.get()));

    }
    else{
      Optional<PersonaJuridica> idJuridica = this.repositorioJuridica.buscarPorUsuario(id_usuario);

      if (idJuridica.isPresent()) {
        ofertas = this.repositorioOferta.buscarPorPersonaJuridica(idJuridica.get().getId());
      } else {
        context.status(404).result("Error al ver las ofertas.");
      }
    }

    List<Rubro> rubros = repositorioRubro.buscarTodos(Rubro.class);

    model.put("ofertas", ofertas);
    model.put("rubros", rubros);
    model.put("titulo", "Listado de ofertas");
    context.render(rutahbs, model);
  }

  @Override
  public void show(Context context) {
    // TODO
  }

  @Override
  public void create(Context context) {
    List <Rubro> rubros = this.repositorioRubro.buscarTodos(Rubro.class);
    Map<String, Object> model = new HashMap<>();
    model.put("rubros", rubros);
    context.render(rutaForm, model);

  }

  @Override
  public void save(Context context) {
    String rol = context.sessionAttribute("rol");
    String pathImagen = null;
    if(TipoRol.PERSONA_HUMANA.name().equals(rol)){
      OfertaDTO ofertaDTO = context.bodyAsClass(OfertaDTO.class);
      Long idOferta = ofertaDTO.getIdOferta();
      Optional<Oferta> ofertaOptional = repositorioOferta.buscarPorId(idOferta, Oferta.class);

      if (ofertaOptional.isPresent()) {
        Long id_usuario = context.sessionAttribute("id");
        Optional<PersonaHumana> canjeador = repositorioPersonaHumana.buscarPorUsuario(id_usuario);
        Oferta oferta = ofertaOptional.get();
        if(oferta.puedeCanjear(canjeador.get())) {
          OfertaCanjeada ofertaCanjeada = OfertaCanjeada
              .builder()
              .oferta(oferta)
              .fechaCanje(LocalDateTime.now())
              .build();
          canjeador.get().agregarOfertaCanjeada(ofertaCanjeada);
          canjeador.get().sumarPuntaje(-oferta.getCantidadPuntosNecesarios());
          withTransaction(() -> {
            repositorioOfertaCanjeada.guardar(ofertaCanjeada);
            repositorioPersonaHumana.actualizar(canjeador.get());
          });

          context.status(200).contentType("text/plain").result("Canje exitoso");
        }
        else{
          context.status(403).contentType("text/plain").result("Puntos insuficientes para canjear oferta.");
        }
      } else {
        // Manejar el caso cuando no se encuentra la oferta
        context.status(403).result("Oferta no encontrada");
      }

    }
    else{
      UploadedFile uploadedFile = context.uploadedFile("imagen");
      System.out.println("Uploaded file: " + uploadedFile);
      System.out.println("TAMAÑO DEL ARCHIVO SUBIDO " + uploadedFile.size());
      if (uploadedFile != null && uploadedFile.size()>0) {
        String fileName = uploadedFile.filename();
        pathImagen = "img/" + fileName; // Ruta donde se guardará la imagen

        // Crear la carpeta si no existe
        File uploadsDir = new File("src/main/resources/public/img");
        /*if (!uploadsDir.exists()) {
          uploadsDir.mkdirs();
        }*/

        // Guardar el archivo
        try (InputStream inputStream = uploadedFile.content();
             FileOutputStream outputStream = new FileOutputStream(new File(uploadsDir, fileName))) {
          byte[] buffer = new byte[1024];
          int bytesRead;
          while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
          }
        } catch (IOException e) {
          context.status(HttpStatus.INTERNAL_SERVER_ERROR_500).result("Error al guardar la imagen");
          return;
        }
      }
      // Crear la oferta y guardar en la base de datos
      Optional<PersonaJuridica> personaJuridica = repositorioJuridica.buscarPorUsuario(context.sessionAttribute("id"));

      if(personaJuridica.isEmpty()){
        context.status(404).result("Oferta no encontrada");
        return;
      }
      Oferta oferta = Oferta
          .builder()
          .nombre(context.formParam("nombre"))
          .rubro(repositorioRubro.buscarPorNombre(context.formParam("categoria")).get())
          .imagen(pathImagen)
          .cantidadPuntosNecesarios(Float.parseFloat(context.formParam("puntos")))
          .organizacion(personaJuridica.get())
          .build();

      withTransaction(()-> repositorioOferta.guardar(oferta));
    }

    context.status(HttpStatus.CREATED_201).result("Oferta creada");
    context.redirect("/ofertas");

  }

  @Override
  public void edit(Context context) {
    // permitir editar una oferta o no?
    // TODO
  }

  @Override
  public void update(Context context) {
    // TODO
  }

  @Override
  public void delete(Context context) {
    // TODO eliminar oferta

  }

  public void verOfertasCanjeadas(Context context) {
    Long idUsuario = context.sessionAttribute("id");
    Optional<PersonaHumana> personaHumana = repositorioPersonaHumana.buscarPorUsuario(idUsuario);
    List<OfertaCanjeada> ofertasCanjeadas = repositorioOfertaCanjeada.buscarPorPersonaHumana(personaHumana.get().getId());

    Map<String, Object> model = new HashMap<>();
    model.put("titulo", "Mis ofertas canjeadas");
    model.put("ofertasCanjeadas", ofertasCanjeadas);
    model.put("puntos", personaHumana.get().getPuntajeActual());

    // Asumiendo que tienes un método para renderizar la vista de ofertas canjeadas
    context.render("colaboraciones/ofertasCanjeadas.hbs", model);
  }

}
