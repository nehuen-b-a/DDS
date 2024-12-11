package ar.edu.utn.frba.dds.domain.main;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class PersistenciaMain implements WithSimplePersistenceUnit {
  public static void main(String[] arg) {
    PersistenciaMain instance = new PersistenciaMain();
    instance.inicializar();
  }

  private void inicializar() {
    withTransaction(() -> {
    });
  }
}
