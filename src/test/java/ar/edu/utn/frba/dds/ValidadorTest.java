package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.domain.entities.validador.AusenciaDeCredencialesPorDefecto;
import ar.edu.utn.frba.dds.domain.entities.validador.ListaDePeoresClavesMemorizadas;
import ar.edu.utn.frba.dds.domain.entities.validador.LongitudEstipulada;
import ar.edu.utn.frba.dds.domain.entities.usuarios.Usuario;
import ar.edu.utn.frba.dds.domain.entities.validador.ValidadorDeClave;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ValidadorTest {
    /*
    private Usuario usuario;
    private ValidadorDeClave validador;

    @BeforeEach
    public void antesDeTestear() {
        usuario = new Usuario("Juan");

        // Inicializa el validador con las restricciones deseadas
        validador = new ValidadorDeClave(
            new LongitudEstipulada(16),
            new ListaDePeoresClavesMemorizadas(),
            new AusenciaDeCredencialesPorDefecto("Juan")
        );
    }

    @Test
    @DisplayName("El validador no falla ante una clave correcta")
    public void claveValidaNoDeberiaFallar() {
        String claveValida = "hola1234hola";
        Assertions.assertTrue(validador.validar(claveValida), "La clave válida no debería generar errores.");
        System.out.println(validador.getErroresFinales());
        Assertions.assertEquals("", validador.getErroresFinales(), "No debería haber errores para una clave válida.");
    }

    @Test
    @DisplayName("El validador falla al ingresar una clave igual que el nombre de usuario")
    public void claveIgualNombreDeUsuarioDeberiaFallar() {
        String claveIgualNombre = "Juan";
        Assertions.assertFalse(validador.validar(claveIgualNombre), "La clave igual al nombre de usuario debería generar un error.");
        String mensajeEsperado = new AusenciaDeCredencialesPorDefecto("Juan").getMensajeError();
        Assertions.assertTrue(validador.getErroresFinales().contains(mensajeEsperado), "El mensaje de error esperado no está presente.");
    }

    @Test
    @DisplayName("El validador falla al ingresar una clave perteneciente a las mil peores")
    public void clavePeorDeberiaFallar() {
        String clavePeor = "123456";
        Assertions.assertFalse(validador.validar(clavePeor), "La clave de la lista de peores debería generar un error.");
        String mensajeEsperado = new ListaDePeoresClavesMemorizadas().getMensajeError();
        Assertions.assertTrue(validador.getErroresFinales().contains(mensajeEsperado), "El mensaje de error esperado no está presente.");
    }

    @Test
    @DisplayName("El validador falla por no cumplir la longitud mínima esperada")
    public void fallaValidadorPorLongitud() {
        String secretoCorto = "admin";
        Assertions.assertFalse(validador.validar(secretoCorto), "La clave corta debería generar un error.");
        String mensajeEsperado = new LongitudEstipulada(16).getMensajeError();
        Assertions.assertTrue(validador.getErroresFinales().contains(mensajeEsperado), "El mensaje de error esperado no está presente.");
    }
    */
}
