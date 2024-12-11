package ar.edu.utn.frba.dds.domain.entities.heladeras;

import ar.edu.utn.frba.dds.domain.entities.tarjetas.Tarjeta;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioHeladera;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTarjetas;
import ar.edu.utn.frba.dds.domain.repositories.imp.RepositorioTecnicos;
import java.util.Optional;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ReceptorAperturaHeladera implements IMqttMessageListener {
  private RepositorioHeladera repositorioHeladeras;
  private RepositorioTarjetas repositorioTarjeta;

  public ReceptorAperturaHeladera(RepositorioHeladera repositorioHeladeras, RepositorioTarjetas repositorioTarjeta) {
    this.repositorioHeladeras = repositorioHeladeras;
    this.repositorioTarjeta = repositorioTarjeta;
  }

  public void messageArrived(String topic, MqttMessage mensaje) { // formato: idHeladera | codigoTarjeta
    try{
      String[] payload = dividirPayload(mensaje.toString());
      if(payload != null){
        Long idHeladera = Long.parseLong(payload[0]);
        String tipoMensaje = payload[1];
        String codigoTarjeta = payload[2];

        procesarMensaje(idHeladera, tipoMensaje, codigoTarjeta);
      }
    } catch (NumberFormatException e) {
      System.err.println("Error al convertir el valor a entero: " + e.getMessage());
    } catch (Exception e) {
      System.err.println("Error al procesar el mensaje: " + e.getMessage());
    }
  }

  private String[] dividirPayload(String payload) {
    String[] partes = payload.split(",");
    if (partes.length == 2) {
      String[] parteDivididas = partes[1].split(":");
      if (parteDivididas.length == 2) {
        return new String[]{partes[0], parteDivididas[0], parteDivididas[1]};
      } else {
        System.err.println("Formato de TipoDeMensaje:Valor incorrecto");
      }
    } else {
      System.err.println("Formato de payload incorrecto");
    }
    return null;
  }

  private void procesarMensaje(Long idHeladera, String tipoMensaje, String codigoTarjeta) {
    Optional<Heladera> optionalHeladera = repositorioHeladeras.buscarPorId(idHeladera, Heladera.class);
    if (optionalHeladera.isPresent()) {
      Heladera heladera = optionalHeladera.get();
      if (!tipoMensaje.equals("Apertura")) {
        System.err.println("Tipo de mensaje no reconocido: " + tipoMensaje);
      } else {
        Optional<Tarjeta> optionalTarjeta =  repositorioTarjeta.buscarCodigo(codigoTarjeta);
        if (optionalTarjeta.isPresent()) {
          heladera.validarApertura(optionalTarjeta.get());
        } else {
          System.err.println("Tarjeta no encontrada para el código: " + codigoTarjeta);
        }
      }
    } else {
      System.err.println("Heladera no encontrada para el ID: " + idHeladera);
    }
  }
}
