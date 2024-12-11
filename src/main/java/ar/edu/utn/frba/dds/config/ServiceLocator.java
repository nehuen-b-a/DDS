package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.controllers.*;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaJavaXMail;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaTelegramBot;
import ar.edu.utn.frba.dds.domain.adapters.AdaptadaWhatsAppTwillio;
import ar.edu.utn.frba.dds.domain.repositories.Repositorio;
import ar.edu.utn.frba.dds.domain.repositories.imp.*;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
  private static Map<String, Object> instances = new HashMap<>();

  @SuppressWarnings("unchecked")
  public static <T> T instanceOf(Class<T> componentClass) {
    String componentName = componentClass.getName();
    if (!instances.containsKey(componentName)) {
      Object instance = null;
      if (componentName.equals(ControladorPersonaVulnerable.class.getName())) {
        instance = new ControladorPersonaVulnerable(
                instanceOf(RepositorioPersonaVulnerable.class),
                instanceOf(RepositorioPersonaHumana.class),
                instanceOf(RepositorioTarjetas.class)
        );
      } else if (componentName.equals(ControladorRegistroUsuario.class.getName())) {
        instance = new ControladorRegistroUsuario(
                instanceOf(RepositorioUsuario.class),
                instanceOf(RepositorioRol.class)
        );
      } else if (componentName.equals(ControladorEleccionTipoCuenta.class.getName())) {
        instance = new ControladorEleccionTipoCuenta();
      } else if (componentName.equals(ControladorTecnicos.class.getName())) {
        instance = new ControladorTecnicos(
                instanceOf(RepositorioTecnicos.class),
                instanceOf(RepositorioUsuario.class),
                instanceOf(RepositorioRol.class)
        );
      } else if (componentName.equals(ControladorCargaMasiva.class.getName())) {
        instance = new ControladorCargaMasiva(
                ServiceLocator.instanceOf(RepositorioPersonaHumana.class),
                ServiceLocator.instanceOf(AdaptadaJavaXMail.class),
                ServiceLocator.instanceOf(RepositorioUsuario.class),
                ServiceLocator.instanceOf(RepositorioRol.class),
                ServiceLocator.instanceOf(Repositorio.class)
        );
      } else if (componentName.equals(ControladorInicio.class.getName())) {
        instance = new ControladorInicio();
      } else if (componentName.equals(ControladorReportes.class.getName())) {
        instance = new ControladorReportes();
      } else if (componentName.equals(ControladorPersonaHumana.class.getName())) {
        instance = new ControladorPersonaHumana(instanceOf(RepositorioPersonaHumana.class), instanceOf(RepositorioTarjetas.class));
      } else if (componentName.equals(ControladorPersonaJuridica.class.getName())) {
        instance = new ControladorPersonaJuridica(instanceOf(RepositorioPersonaJuridica.class));
      } else if (componentName.equals(ControladorDonacionVianda.class.getName())) {
        instance = new ControladorDonacionVianda(
            instanceOf(RepositorioPersonaHumana.class),
            instanceOf(RepositorioDonacionVianda.class),
            instanceOf(RepositorioHeladera.class),
            instanceOf(RepositorioSolicitudApertura.class)
        );
      } else if (componentName.equals(ControladorDonacionDinero.class.getName())) {
        instance = new ControladorDonacionDinero(
            instanceOf(Repositorio.class),
            instanceOf(RepositorioPersonaJuridica.class),
            instanceOf(RepositorioPersonaHumana.class),
            instanceOf(RepositorioDonacionDinero.class)
        );
      } else if (componentName.equals(Repositorio.class.getName())) {
        instance = new Repositorio();
      } else if (componentName.equals(ControladorMapa.class.getName())) {
        instance = new ControladorMapa(
                ServiceLocator.instanceOf(RepositorioHeladera.class),
                ServiceLocator.instanceOf(RepositorioPersonaJuridica.class),
                ServiceLocator.instanceOf(RepositorioSuscripcion.class)
        );
      } else if (componentName.equals(ControladorSuscripcion.class.getName())) {
        instance = new ControladorSuscripcion(
                ServiceLocator.instanceOf(RepositorioHeladera.class),
                ServiceLocator.instanceOf(RepositorioSuscripcion.class),
                ServiceLocator.instanceOf(RepositorioPersonaHumana.class)
        );
      } else if (componentName.equals(ControladorIncidenteHeladera.class.getName())) {
        instance = new ControladorIncidenteHeladera(
                ServiceLocator.instanceOf(RepositorioHeladera.class),
                ServiceLocator.instanceOf(RepositorioPersonaHumana.class),
                ServiceLocator.instanceOf(Repositorio.class)
        );
      } else if (componentName.equals(ControladorOferta.class.getName())) {
        instance = new ControladorOferta(
            ServiceLocator.instanceOf(RepositorioOferta.class),
            ServiceLocator.instanceOf(RepositorioRubro.class),
            ServiceLocator.instanceOf(RepositorioPersonaJuridica.class),
            ServiceLocator.instanceOf(RepositorioPersonaHumana.class),
            ServiceLocator.instanceOf(RepositorioOfertaCanjeada.class)
        );
      } else if (componentName.equals(RepositorioPersonaHumana.class.getName())) {
        instance = new RepositorioPersonaHumana();
      } else if (componentName.equals(RepositorioPersonaJuridica.class.getName())) {
        instance = new RepositorioPersonaJuridica();
      } else if (componentName.equals(RepositorioTecnicos.class.getName())) {
        instance = new RepositorioTecnicos();
      } else if (componentName.equals(RepositorioUsuario.class.getName())) {
        instance = new RepositorioUsuario();
      } else if (componentName.equals(RepositorioRol.class.getName())) {
        instance = new RepositorioRol();
      } else if (componentName.equals(AdaptadaJavaXMail.class.getName())) {
        instance = new AdaptadaJavaXMail();
      } else if (componentName.equals(AdaptadaTelegramBot.class.getName())) {
        instance = new AdaptadaTelegramBot();
      } else if (componentName.equals(AdaptadaWhatsAppTwillio.class.getName())) {
        instance = new AdaptadaWhatsAppTwillio();
      } else if (componentName.equals(RepositorioHeladera.class.getName())) {
        instance = new RepositorioHeladera();
      } else if (componentName.equals(RepositorioPersonaVulnerable.class.getName())) {
        instance = new RepositorioPersonaVulnerable();
      } else if (componentName.equals(ControladorDistribucionVianda.class.getName())) {
        instance = new ControladorDistribucionVianda(
            instanceOf(RepositorioDistribucionVianda.class),
            instanceOf(RepositorioPersonaHumana.class),
            instanceOf(RepositorioHeladera.class),
            instanceOf(RepositorioSolicitudApertura.class)
        );
      } else if (componentName.equals(ControladorHeladera.class.getName())) {
        instance = new ControladorHeladera(
            instanceOf(RepositorioHeladera.class),
            instanceOf(RepositorioPersonaJuridica.class),
            instanceOf(RepositorioSuscripcion.class),
            instanceOf(Repositorio.class),
            instanceOf(RepositorioTecnicos.class)
        );
      } else if (componentName.equals(ControladorModeloHeladera.class.getName())) {
        instance = new ControladorModeloHeladera(instanceOf(Repositorio.class));
      } else if (componentName.equals(RepositorioDistribucionVianda.class.getName())) {
        instance = new RepositorioDistribucionVianda();
      } else if (componentName.equals(ControladorInicioSesion.class.getName())) {
        instance = new ControladorInicioSesion(ServiceLocator.instanceOf(RepositorioUsuario.class));
      } else if (componentName.equals(RepositorioOferta.class.getName())) {
        instance = new RepositorioOferta();
      } else if (componentName.equals(RepositorioOfertaCanjeada.class.getName())) {
        instance = new RepositorioOfertaCanjeada();
      } else if (componentName.equals(RepositorioRubro.class.getName())) {
        instance = new RepositorioRubro();
      } else if (componentName.equals(RepositorioSuscripcion.class.getName())) {
        instance = new RepositorioSuscripcion();
      } else if (componentName.equals(RepositorioTarjetas.class.getName())) {
        instance = new RepositorioTarjetas();
      } else if (componentName.equals(RepositorioDonacionVianda.class.getName())) {
        instance = new RepositorioDonacionVianda();
      } else if (componentName.equals(RepositorioGeoRef.class.getName())) {
        instance = new RepositorioGeoRef();
      } else if (componentName.equals(RepositorioDonacionDinero.class.getName())) {
        instance = new RepositorioDonacionDinero();
      } else if (componentName.equals(RepositorioSolicitudApertura.class.getName())) {
        instance = new RepositorioSolicitudApertura();
      }

      instances.put(componentName, instance);
    }

    return (T) instances.get(componentName);
  }
}