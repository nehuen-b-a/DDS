package ar.edu.utn.frba.dds.controllers;

import static ar.edu.utn.frba.dds.utils.manejos.GeneradorHashRandom.generateRandomString;
import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;
import ar.edu.utn.frba.dds.domain.entities.heladeras.EstadoHeladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.heladeras.incidentes.Incidente;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Tecnico;
import ar.edu.utn.frba.dds.domain.entities.tecnicos.Visita;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Rol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.main.GeneradorReportesMain;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioRol;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTecnicos;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioUsuario;
import ar.edu.utn.frba.dds.dtos.TecnicoDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.ICrudViewsHandler;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;

public class ControladorTecnicos implements ICrudViewsHandler, WithSimplePersistenceUnit {
  private RepositorioTecnicos repositorioTecnicos;
  private RepositorioUsuario repositorioUsuario;
  private RepositorioRol repositorioRol;
  private final String rutaAltaHbs = "/admin/adminAltaTecnicos.hbs";
  private final String rutaListadoHbs = "admin/adminListadoTecnicos.hbs";

  public ControladorTecnicos(RepositorioTecnicos repositorioTecnico, RepositorioUsuario repositorioUsuario, RepositorioRol repositorioRol) {
    this.repositorioTecnicos = repositorioTecnico;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioRol = repositorioRol;
  }

  @Override
  public void index(Context context) {
    List<Tecnico> tecnicos = this.repositorioTecnicos.buscarTodos(Tecnico.class);

    Map<String, Object> model = new HashMap<>();
    model.put("tecnicos", tecnicos);

    context.render(rutaListadoHbs, model);
  }

  @Override
  public void show(Context context) {
    // En este caso, no tiene sentido hacer este metodo.
  }

  @Override
  public void create(Context context) {
    context.render(rutaAltaHbs);
  }

  @Override
  public void save(Context context) {
    TecnicoDTO dto = new TecnicoDTO();
    dto.obtenerFormulario(context);
    Tecnico nuevoTecnico;

    try {
      nuevoTecnico = Tecnico.fromDTO(dto);
      if (nuevoTecnico == null) {
        throw new ValidacionFormularioException("Los datos del técnico son inválidos.");
      }

      Usuario usuario = cargarUsuario(nuevoTecnico);
      nuevoTecnico.setUsuario(usuario);
      withTransaction(() -> {
        repositorioUsuario.guardar(usuario);
        repositorioTecnicos.guardar(nuevoTecnico);
      });
      notificarAltaTecnico(nuevoTecnico);

      context.redirect("/tecnicos");
    } catch (ValidacionFormularioException e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("dto", dto);
      context.render(rutaAltaHbs, model);
    }
  }

  @Override
  public void edit(Context context) {
    Map<String, Object> model = new HashMap<>();
    try {
      Optional<Tecnico> tecnico = this.repositorioTecnicos.buscarPorId(Long.valueOf(context.pathParam("id")), Tecnico.class);

      if (tecnico.isEmpty()) {
        throw new ValidacionFormularioException("No existe un técnico con este id.");
      }

      TecnicoDTO dto = new TecnicoDTO(tecnico.get());
      model.put("dto", dto);
      model.put("edicion", true);
      model.put("id", context.pathParam("id"));
      context.render(rutaAltaHbs, model);
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      context.render(rutaListadoHbs, model);
    }
  }

  @Override
  public void update(Context context) {
    Map<String, Object> model = new HashMap<>();
    TecnicoDTO dtoNuevo = new TecnicoDTO();
    dtoNuevo.obtenerFormulario(context);

    try {
      Optional<Tecnico> tecnicoExistente = repositorioTecnicos.buscarPorId(
          Long.valueOf(context.pathParam("id")), Tecnico.class);

      if (tecnicoExistente.isEmpty()) {
        throw new ValidacionFormularioException("Tecnico no encontrada.");
      }

      TecnicoDTO dtoExistente = new TecnicoDTO(tecnicoExistente.get());
      if (dtoExistente.equals(dtoNuevo)) {
        throw new ValidacionFormularioException("No se detectaron cambios en el formulario.");
      }

      tecnicoExistente.get().actualizarFromDto(dtoNuevo);
      withTransaction(() -> repositorioTecnicos.actualizar(tecnicoExistente.get()));
      context.redirect("/tecnicos");
    } catch (ValidacionFormularioException e) {
      model.put("error", e.getMessage());
      model.put("dto", dtoNuevo);
      model.put("edicion", true);
      model.put("id", context.pathParam("id"));
      context.render(rutaAltaHbs, model);
    }
  }

  @Override
  public void delete(Context context) {
    Long id = Long.valueOf(context.pathParam("id"));
    Optional<Tecnico> persona = this.repositorioTecnicos.buscarPorId(id, Tecnico.class);
    if (persona.isPresent()) {
      withTransaction(() -> this.repositorioTecnicos.eliminarFisico(Tecnico.class, id));
      context.redirect("/tecnicos");
    } else {
      context.status(400).result("No se puede eliminar, el técnico no cumple con las condiciones para ser eliminada.");
    }
  }

  public void notificarAltaTecnico(Tecnico tecnico) {
    Mensaje mensaje = new Mensaje(
        "Acceso al Sistema de Mejora del Acceso Alimentario",
        "Estimado/a Técnico " + tecnico.getNombre() + " " + tecnico.getApellido() + ",\n\n"
            + "Le agradecemos su colaboración como técnico en el Sistema para la Mejora del Acceso Alimentario.\n"
            + "A continuación, le proporcionamos las credenciales de acceso a su cuenta:\n\n"
            + " - Usuario: " + tecnico.getUsuario().getNombre() + "\n"
            + " - Clave: " + tecnico.getUsuario().getClave() + "\n\n"
            + "Para su comodidad, puede cambiar la contraseña en cualquier momento desde su perfil en el sistema.\n"
            + "¡Gracias nuevamente por su compromiso y apoyo!",
        LocalDateTime.now()
    );
    try {
      tecnico.getContacto().enviarMensaje(mensaje);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new ValidacionFormularioException("No se ha podido notificar a " + tecnico.getNombre() + " " + tecnico.getApellido());
    }

    // Encripto la clave del usuario
    tecnico.getUsuario().setClaveEncriptada(tecnico.getUsuario().getClave());
    withTransaction(() -> repositorioUsuario.actualizar(tecnico.getUsuario()));
    mensaje.setDestinatario(tecnico.getUsuario());

    Optional<Usuario> emisor = repositorioUsuario.buscarPorId(1L, Usuario.class);
    if (emisor.isEmpty()) {
      throw new ValidacionFormularioException("No existe el usuario emisor de este mensaje.");
    }
    mensaje.setEmisor(emisor.get());
    withTransaction(() -> repositorioUsuario.guardar(mensaje));
  }

  public Usuario cargarUsuario(Tecnico tecnico) {
    String baseNombre = tecnico.getNombre().toLowerCase() + "." + tecnico.getApellido().toLowerCase();
    String nombreUsuario = baseNombre;
    int contador = 1;

    while (repositorioUsuario.buscarPorNombre(nombreUsuario).isPresent()) {
      nombreUsuario = baseNombre + contador++;
    }

    Usuario usuario = new Usuario();
    usuario.setNombre(nombreUsuario);
    usuario.setClave(generateRandomString(12));

    Optional<Rol> rol = repositorioRol.buscarPorTipo(TipoRol.TECNICO);
    if (rol.isEmpty()) {
      throw new ValidacionFormularioException("No existe el rol indicado.");
    }
    usuario.setRol(rol.get());

    return usuario;
  }

  public void registrarVisita(Context context) {
    Long heladeraId = Long.valueOf(context.pathParam("heladeraId"));

    // Buscar la heladera
    Heladera heladera = this.repositorioTecnicos.buscarPorId(heladeraId, Heladera.class).orElse(null);
    if (heladera == null) {
      context.status(404).result("No se encontró una heladera con el ID proporcionado.");
      return;
    }
    System.out.println(heladera.getEstado());
    System.out.println(heladera.getNombre());

    // Verificar si la heladera está activa
    if (heladera.estaActiva()) {
      context.status(400).result("La heladera ya está activa. No es necesaria una nueva visita.");
      return;
    }

    // Buscar incidente relacionado con la heladera
    Incidente incidente = this.repositorioTecnicos.buscarIncidente(heladeraId).orElse(null);
    if (incidente == null) {
      context.status(404).result("No se ha registrado un incidente para la heladera: " + heladera.getNombre());
      return;
    }

    // Crear y registrar la visita
    Visita visita = new Visita();
    visita.setDescripcion("Se ha solucionado el incidente en " + heladera.getNombre());

    // Actualizar estado de la heladera y registrar la visita en el incidente
    heladera.setEstado(EstadoHeladera.ACTIVA);
    incidente.registrarVisita(visita, true);

    // Persistir cambios en la base de datos
    withTransaction(() -> {
      this.repositorioTecnicos.actualizar(heladera);
      this.repositorioTecnicos.guardar(visita);
      this.repositorioTecnicos.actualizar(incidente);
    });

    // Según la consigna los reportes se ejecutan según una temporalidad, por ejemplo semanalmente,
    // pero en el flujo no dan a entender eso, por eso pongo esto aquí.
    try {
      GeneradorReportesMain.main(null);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    context.status(200).result("Visita registrada y heladera activada correctamente.");
  }
}