package ar.edu.utn.frba.dds.controllers;

import static ar.edu.utn.frba.dds.utils.manejos.GeneradorHashRandom.generateRandomString;
import ar.edu.utn.frba.dds.domain.adapters.AdapterMail;
import ar.edu.utn.frba.dds.domain.entities.cargaMasiva.CargaMasivaColaboraciones;
import ar.edu.utn.frba.dds.domain.entities.contacto.Mensaje;
import ar.edu.utn.frba.dds.domain.entities.donacionesDinero.DonacionDinero;
import ar.edu.utn.frba.dds.domain.entities.donacionesDinero.UnidadFrecuencia;
import ar.edu.utn.frba.dds.domain.entities.heladeras.Heladera;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.FormasContribucionHumanas;
import ar.edu.utn.frba.dds.domain.entities.personasHumanas.PersonaHumana;
import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Rol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.TipoRol;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.entities.viandas.DistribucionVianda;
import ar.edu.utn.frba.dds.domain.entities.viandas.Vianda;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioPersonaHumana;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioRol;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioUsuario;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ControladorCargaMasiva implements WithSimplePersistenceUnit {
  private final String rutaCargaHbs = "/admin/adminCargaCSV.hbs";
  private RepositorioPersonaHumana repositorioPersonaHumana;
  private RepositorioUsuario repositorioUsuario;
  private RepositorioRol repositorioRol;
  private Repositorio repositorio;
  private AdapterMail adapterMail;

  public ControladorCargaMasiva(RepositorioPersonaHumana repositorioPersonaHumana, AdapterMail adapterMail, RepositorioUsuario repositorioUsuario, RepositorioRol repositorioRol, Repositorio repositorio) {
    this.repositorioPersonaHumana = repositorioPersonaHumana;
    this.adapterMail = adapterMail;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioRol = repositorioRol;
    this.repositorio = repositorio;
  }

  public void create(Context context) {
    context.render(rutaCargaHbs);
  }

  public void save(Context context) {
    UploadedFile archivo = context.uploadedFile("files");
    String rutaDestino = null;

    try {
      // Valida y obtiene el archivo de CSV
      CargaMasivaColaboraciones carga = procesarArchivoCSV(archivo);
      rutaDestino = carga.obtenerRutaDestino(archivo);

      // Obtiene el usuario responsable
      Usuario usuarioEmisor = obtenerUsuarioEmisorResponsable(context);
      try (Reader reader = new InputStreamReader(new FileInputStream(rutaDestino))) {
        CSVParser parser = carga.crearParserDeCsv(reader);
        for (CSVRecord record : parser) {
          procesarRegistroCSV(record, usuarioEmisor, carga);
        }
      }

      // Persistencia final de la carga masiva
      this.persistirCargaMasiva(carga, usuarioEmisor);

      // Renderiza éxito
      context.render(rutaCargaHbs, Map.of("success", "La carga masiva se realizó con éxito."));
    } catch (Exception e) {
      context.render(rutaCargaHbs, Map.of("error", e.getMessage()));
      e.printStackTrace();
    } finally {
      // Elimina el archivo temporal al final
      if (rutaDestino != null) {
        File archivoAEliminar = new File(rutaDestino);
        if (archivoAEliminar.exists()) {
          archivoAEliminar.delete();
        }
      }
    }
  }

  private CargaMasivaColaboraciones procesarArchivoCSV(UploadedFile archivo) throws IOException {
    if (archivo == null) {
      throw new ValidacionFormularioException("No se ha subido un archivo.");
    }
    CargaMasivaColaboraciones carga = new CargaMasivaColaboraciones(adapterMail);
    carga.obtenerRutaDestino(archivo);
    return carga;
  }

  private Usuario obtenerUsuarioEmisorResponsable(Context context) {
    Long id = context.sessionAttribute("id");
    Optional<Usuario> usuarioEmisor = repositorioUsuario.buscarPorId(id, Usuario.class);
    return usuarioEmisor.orElseThrow(() -> new ValidacionFormularioException("No se ha encontrado al usuario responsable."));
  }

  private void procesarRegistroCSV(CSVRecord record, Usuario usuarioEmisor, CargaMasivaColaboraciones carga) throws MessagingException, UnsupportedEncodingException {
    Optional<PersonaHumana> posibleHumano = repositorioPersonaHumana.buscarPorDocumento(record.get(1));
    if (posibleHumano.isPresent()) {
      // La persona ya existe, se agrega la contribución
      withTransaction(() -> persistirContribucion(record, posibleHumano.get()));
    } else {
      // La persona no existe, se crea un nuevo humano y su contribución
      Rol rolPersonaHumana = repositorioRol.buscarPorTipo(TipoRol.PERSONA_HUMANA).orElseThrow(() -> new ValidacionFormularioException("Rol de persona humana no encontrado."));
      Usuario usuario = cargarUsuario(record, rolPersonaHumana);

      PersonaHumana nuevoHumano = carga.cargarPersonaHumana(record);
      nuevoHumano.setUsuario(usuario);

      withTransaction(() -> {
        repositorioUsuario.guardar(usuario);
        repositorioPersonaHumana.guardar(nuevoHumano);
        persistirContribucion(record, nuevoHumano);


        // Enviar mensaje de alta
        Mensaje mensaje = crearMensajeAltaPersona(carga, nuevoHumano, usuarioEmisor);
        repositorio.guardar(mensaje);
      });
    }
  }

  private Mensaje crearMensajeAltaPersona(CargaMasivaColaboraciones carga, PersonaHumana destinatario, Usuario emisor) {
    // Mando el mensaje sin encriptar la clave
    carga.notificarAltaPersona(destinatario);

    // Encripto la clave del usuario
    destinatario.getUsuario().setClaveEncriptada(destinatario.getUsuario().getClave());
    repositorioUsuario.actualizar(destinatario.getUsuario());

    Mensaje nuevoMensaje = carga.armarMensajeCargaMasiva(destinatario);
    nuevoMensaje.setDestinatario(destinatario.getUsuario());
    nuevoMensaje.setEmisor(emisor);
    return nuevoMensaje;
  }

  private Usuario persistirCargaMasiva(CargaMasivaColaboraciones carga, Usuario usuario) {
    withTransaction(() -> {
      carga.setResponsable(usuario);
      carga.setFechaRegistro(LocalDateTime.now());
      repositorio.guardar(carga);
    });

    return usuario;
  }

  private void persistirContribucion(CSVRecord record, PersonaHumana humano) {
    int cantidad = Integer.parseInt(record.get(7));
    LocalDate fecha = LocalDate.parse(record.get(5), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

    switch (FormasContribucionHumanas.valueOf(record.get(6))) {
      case DONACION_DINERO:
        DonacionDinero donacionDinero = new DonacionDinero();
        donacionDinero.setMonto(cantidad);
        donacionDinero.setFecha(fecha);
        donacionDinero.setPersonaHumana(humano);
        donacionDinero.setUnidadFrecuencia(UnidadFrecuencia.NINGUNA);
        humano.sumarPuntaje(donacionDinero.calcularPuntaje());
        repositorio.guardar(donacionDinero);
        break;
      case DONACION_VIANDAS:
        for (int i = 0; i < cantidad; i++) {
          Vianda viandaDonada = new Vianda(fecha);
          viandaDonada.setPersonaHumana(humano);
          viandaDonada.setComida("Correspondiente a carga masiva");
          // le pongo cualquier heladera, debido a que está como atributo no nulleable en la
          // heladera y en el archivo de la carga masiva no la especifican.
          Heladera heladera = this.repositorio.buscarPorId(1L, Heladera.class).orElseThrow(() ->
              new ValidacionFormularioException("No se pudo encontrar la heladera.")
          );
          viandaDonada.setHeladera(heladera);
          humano.sumarPuntaje(viandaDonada.calcularPuntaje());
          repositorio.guardar(viandaDonada);
        }
        break;
      case REDISTRIBUCION_VIANDAS:
        DistribucionVianda distribucion = new DistribucionVianda(fecha, cantidad);
        distribucion.setColaborador(humano);
        distribucion.setTerminada(true);
        distribucion.setMotivo("Carga masiva de colaboraciones");
        Heladera heladeraOrigen = this.repositorio.buscarPorId(1L, Heladera.class).orElseThrow(() ->
            new ValidacionFormularioException("No se pudo encontrar la heladera de origen.")
        );
        Heladera heladeraDestino = this.repositorio.buscarPorId(2L, Heladera.class).orElseThrow(() ->
            new ValidacionFormularioException("No se pudo encontrar la heladera de destino.")
        );
        distribucion.setHeladeraDestino(heladeraDestino);
        distribucion.setHeladeraOrigen(heladeraOrigen);
        distribucion.setTerminada(true);
        humano.sumarPuntaje(distribucion.calcularPuntaje());
        repositorio.guardar(distribucion);
        break;
      case ENTREGA_TARJETAS:
        for (int i = 0; i < cantidad; i++) {
          Tarjeta tarjetaRepartida = new Tarjeta();
          tarjetaRepartida.setFechaRecepcionPersonaVulnerable(fecha);
          humano.sumarPuntaje(tarjetaRepartida.calcularPuntaje());
          repositorio.guardar(tarjetaRepartida);
        }
        break;
      default:
        throw new ValidacionFormularioException("Tipo de contribución no válida: " + record.get(6));
    }
    repositorio.actualizar(humano);
  }

  public Usuario cargarUsuario(CSVRecord record, Rol rol) {
    String baseNombre = (record.get(2).toLowerCase().replace(" ", "-") + "."
        + record.get(3).toLowerCase().replace(" ", "-"));
    String nombreUsuario = baseNombre;
    int contador = 1;

    while (repositorioUsuario.buscarPorNombre(nombreUsuario).isPresent()) {
      nombreUsuario = baseNombre + contador++;
    }

    Usuario usuario = new Usuario();
    usuario.setNombre(nombreUsuario);
    usuario.setClave(generateRandomString(12));
    usuario.setRol(rol);

    return usuario;
  }
}
