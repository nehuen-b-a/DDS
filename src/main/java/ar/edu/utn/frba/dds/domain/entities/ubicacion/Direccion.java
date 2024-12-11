package ar.edu.utn.frba.dds.domain.entities.ubicacion;

import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefDirecciones;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefServicio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.geoRef.GeoRefUbicacion;
import ar.edu.utn.frba.dds.dtos.DireccionDTO;
import ar.edu.utn.frba.dds.exceptions.ValidacionFormularioException;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import ar.edu.utn.frba.dds.utils.manejos.CamposObligatoriosVacios;
import ar.edu.utn.frba.dds.utils.manejos.ManejoDistancias;
import com.google.gson.annotations.Expose;
import java.io.IOException;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

@Getter @Setter
@Embeddable
@NoArgsConstructor
public class Direccion {
  @Column(name = "direccion") @Expose
  private String nomenclatura;
  @Embedded @Expose
  private Coordenada coordenada;

  public boolean estaCercaDe(Direccion direccion) {
    int umbralKm = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("umbral_de_cercania_en_km"));
    return ManejoDistancias.distanciaHaversineConCoordenadasEnKm(direccion.getCoordenada(), this.coordenada) <= umbralKm;
  }

  public String getDireccion() {
    return nomenclatura.split(",")[0];
  }

  public String getCalle() {
    return getDireccion().replaceAll("\\d+", "").trim();
  }

  public String getAltura() {
    return getDireccion().replaceAll("\\D+", "").trim();
  }

  public String getMunicipio() {
    String[] partes = nomenclatura.split(",");
    if (partes.length > 1) {
      return partes[1].trim();
    }
    return "";
  }

  public String getProvincia() {
    String[] partes = nomenclatura.split(",");
    if (partes.length > 2) {
      return partes[2].trim();
    }
    return "";
  }

  public static Direccion fromDTO(DireccionDTO dto) throws ValidacionFormularioException {
    if (dto.isObligatoria()) {
      validarCamposObligatorios(dto);
    } else {
      return validarYCrearSiNoEsObligatoria(dto);
    }

    return normalizarDireccion(dto);
  }

  private static Direccion normalizarDireccion(DireccionDTO dto) {
    try {
      GeoRefDirecciones body = GeoRefServicio.getInstancia().coordenadaDeDireccion(dto);
      if (body.cantidad < 1) {
        throw new ValidacionFormularioException("Dirección inexistente. Por favor, revise los datos.");
      }

      GeoRefDirecciones.GeoRefUbicacion ubicacion = body.direcciones.get(0).ubicacion;
      Coordenada coordenada = new Coordenada(ubicacion.lat, ubicacion.lon);

      Direccion direccion = new Direccion();
      direccion.setNomenclatura(body.direcciones.get(0).nomenclatura);
      direccion.setCoordenada(coordenada);

      return direccion;
    } catch (IOException e) {
      throw new ValidacionFormularioException("Ocurrió un error en la búsqueda de la dirección, revise los datos.");
    }
  }

  private static void validarCamposObligatorios(DireccionDTO dto) throws ValidacionFormularioException {
    CamposObligatoriosVacios.validarCampos(
        Pair.of("calle", dto.getCalle()),
        Pair.of("altura", dto.getAltura()),
        Pair.of("municipio", dto.getMunicipio()),
        Pair.of("provincia", dto.getProvincia())
    );
  }

  private static Direccion validarYCrearSiNoEsObligatoria(DireccionDTO dto) throws ValidacionFormularioException {
    if (todosLosCamposEstanVacios(dto)) {
      return null;
    }

    if (hayCamposIncompletos(dto)) {
      throw new ValidacionFormularioException(
          "La dirección no es obligatoria, pero si se indica una, todos los campos son necesarios."
      );
    }

    return normalizarDireccion(dto);
  }

  private static boolean todosLosCamposEstanVacios(DireccionDTO dto) {
    return Stream.of(dto.getCalle(), dto.getAltura(), dto.getMunicipio(), dto.getProvincia()).allMatch(String::isEmpty);
  }

  private static boolean hayCamposIncompletos(DireccionDTO dto) {
    return Stream.of(dto.getCalle(), dto.getAltura(), dto.getMunicipio(), dto.getProvincia()).anyMatch(String::isEmpty);
  }

  public static Direccion fromCoordenada(String latitud, String longitud) {
    // Validar que los campos obligatorios no estén vacíos
    CamposObligatoriosVacios.validarCampos(
        Pair.of("latitud", latitud),
        Pair.of("longitud", longitud)
    );

    // Crear una nueva instancia de Direccion con la coordenada proporcionada
    Coordenada coordenada = new Coordenada(latitud, longitud);
    Direccion direccion = new Direccion();
    direccion.setCoordenada(coordenada);

    try {
      // Obtener la ubicación según las coordenadas
      GeoRefUbicacion.Ubicacion ubicacion = GeoRefServicio.getInstancia()
          .obtenerDireccionSegunCoordenada(latitud, longitud)
          .getUbicacion();

      // Validar que la ubicación esté dentro de Argentina
      if (ubicacion.municipio_nombre.isEmpty() || ubicacion.provincia_nombre.isEmpty()) {
        throw new ValidacionFormularioException(
            "Se eligieron coordenadas fuera del territorio Argentino. Por favor, indique coordenadas dentro del país."
        );
      }

      // Asignar la nomenclatura con municipio y provincia
      direccion.setNomenclatura(ubicacion.municipio_nombre + ", " + ubicacion.provincia_nombre);
    } catch (IOException e) {
      // Manejar el error si falla la conexión al servicio de direcciones
      throw new ValidacionFormularioException("Ocurrió un problema con el servicio de direcciones. Por favor, reintente.");
    }

    return direccion;
  }

}
