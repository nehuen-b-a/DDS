package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorApertura;
import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorMovimiento;
import ar.edu.utn.frba.dds.domain.entities.heladeras.receptores.ReceptorTemperatura;
import ar.edu.utn.frba.dds.middlewares.AuthMiddleware;
import ar.edu.utn.frba.dds.server.handlers.AppHandlers;
import ar.edu.utn.frba.dds.utils.javalin.Initializer;
import ar.edu.utn.frba.dds.utils.javalin.JavalinRenderer;
import ar.edu.utn.frba.dds.utils.javalin.PrettyProperties;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;
import java.io.IOException;
import java.util.function.Consumer;

public class Server {
  private static Javalin app = null;

  public static Javalin app() {
    if (app == null)
      throw new RuntimeException("App no inicializada");
    return app;
  }

  public static void init() {
    if (app == null) {
      Integer port = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("server_port"));
      app = Javalin.create(config()).start(port);

      AuthMiddleware.apply(app);
      AppHandlers.applyHandlers(app);
      Router.init(app);

      if (Boolean.parseBoolean(PrettyProperties.getInstance().propertyFromName("dev_mode"))) {
        Initializer initializer = new Initializer();
        initializer.init();
      }
      try {
        String brokerUrl = "tcp://broker.hivemq.com:1883";
        String topicTemperatura = "temperatura";
        String topicMovimiento = "movimiento";
        String topicApertura = "apertura";

        ReceptorTemperatura receptorTemperatura = new ReceptorTemperatura(brokerUrl, topicTemperatura);
        ReceptorMovimiento receptorMovimiento = new ReceptorMovimiento(brokerUrl, topicMovimiento);
        ReceptorApertura receptorApertura = new ReceptorApertura(brokerUrl, topicApertura);

        Thread receptorTemperaturaThread = new Thread(receptorTemperatura);
        Thread receptorMovimientoThread = new Thread(receptorMovimiento);
        Thread receptorAperturaThread = new Thread(receptorApertura);

        receptorTemperaturaThread.start();
        receptorMovimientoThread.start();
        receptorAperturaThread.start();

        System.out.println("Los receptores están corriendo en paralelo");

      } catch (Exception e) {
        e.printStackTrace();
      }

    }

  }

  private static Consumer<JavalinConfig> config() {
    return config -> {
      config.staticFiles.add(staticFiles -> {
        staticFiles.hostedPath = "/";
        staticFiles.directory = "/public";
      });

      config.fileRenderer(new JavalinRenderer().register("hbs", (path, model, context) -> {
        Handlebars handlebars = new Handlebars();
        HandlebarHelpers.registerHelpers(handlebars);

        Template template = null;
        try {
          template = handlebars.compile(
              "templates/" + path.replace(".hbs", ""));
          return template.apply(model);
        } catch (IOException e) {
          e.printStackTrace();
          context.status(HttpStatus.NOT_FOUND);
          return "No se encuentra la página indicada...";
        }
      }));
    };
  }
}