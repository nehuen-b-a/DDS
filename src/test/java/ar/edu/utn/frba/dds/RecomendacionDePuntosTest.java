package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.ListadoPuntosRecomendados;
import ar.edu.utn.frba.dds.domain.entities.puntosRecomendados.RecomendacionPuntosServicio;
import ar.edu.utn.frba.dds.domain.entities.ubicacion.Coordenada;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RecomendacionDePuntosTest {/*
  RecomendacionPuntosServicio servicioPuntos = RecomendacionPuntosServicio.getInstancia();
  Coordenadas coordenadasUno = new Coordenadas();
  Coordenadas coordenadasDos = new Coordenadas();
  Coordenadas coordenadasTres = new Coordenadas();
  Coordenadas coordenadasCuatro = new Coordenadas();

  @BeforeEach
  public void antesDeTestear() {
    coordenadasUno.coordenadas.add(new Coordenada("-10.62015666394059","10.44137831038357"));
    coordenadasUno.coordenadas.add(new Coordenada("-20.62015666394059","20.44137831038357"));

    coordenadasDos.coordenadas.add(new Coordenada("-20.62015666394059","20.44137831038357"));
    coordenadasDos.coordenadas.add(new Coordenada("-40.62015666394059","40.44137831038357"));

    coordenadasTres.coordenadas.add(new Coordenada("-60.62015666394059","60.44137831038357"));
    coordenadasTres.coordenadas.add(new Coordenada("-70.62015666394059","70.44137831038357"));

    coordenadasCuatro.coordenadas.add(new Coordenada("-80.62015666394059","80.44137831038357"));
    coordenadasCuatro.coordenadas.add(new Coordenada("-85.62015666394059","85.44137831038357"));
  }

  @Test
  @DisplayName("La recomendacion de puntos mockeada arroja valores correctos")
  public void recomendacionDePuntosMockeadaArrojaValoresCorrectos() throws IOException {
    ListadoPuntosRecomendados listadoPuntosUno = servicioPuntos.listadoPuntosRecomendados(200, "-10.62015666394059", "10.44137831038357");
    Assertions.assertEquals(coordenadasUno.coordenadas, listadoPuntosUno.puntos);
    Assertions.assertEquals(coordenadasUno.coordenadas, listadoPuntosUno.puntos);
    ListadoPuntosRecomendados listadoPuntosDos = servicioPuntos.listadoPuntosRecomendados(400, "-20.62015666394059", "20.44137831038357");
    Assertions.assertEquals(coordenadasDos.coordenadas, listadoPuntosDos.puntos);
    Assertions.assertEquals(coordenadasDos.coordenadas, listadoPuntosDos.puntos);
    ListadoPuntosRecomendados listadoPuntosTres = servicioPuntos.listadoPuntosRecomendados(600, "-30.62015666394059", "30.44137831038357");
    Assertions.assertEquals(coordenadasTres.coordenadas, listadoPuntosTres.puntos);
    Assertions.assertEquals(coordenadasTres.coordenadas, listadoPuntosTres.puntos);
    ListadoPuntosRecomendados listadoPuntosCuatro = servicioPuntos.listadoPuntosRecomendados(800, "-40.62015666394059", "40.44137831038357");
    Assertions.assertEquals(coordenadasCuatro.coordenadas, listadoPuntosCuatro.puntos);
    Assertions.assertEquals(coordenadasCuatro.coordenadas, listadoPuntosCuatro.puntos);
  }

  @Getter
  private class Coordenadas {
    List<Coordenada> coordenadas;

    public Coordenadas() {
      this.coordenadas = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Coordenadas that = (Coordenadas) o;
      return Objects.equals(coordenadas, that.coordenadas);
    }
  }*/
}
