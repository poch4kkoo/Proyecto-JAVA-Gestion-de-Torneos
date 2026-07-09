package org.logica;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * clase de pruebas unitarias para la enumeracion disciplina.
 * verifica que las reglas de negocio y validaciones matematicas de los
 * puntajes funcionen correctamente segun el deporte.
 */
public class DisciplinaTest {
    /**
     * verifica la logica de puntajes en el ajedrez.
     * comprueba que la suma de los puntajes sea exactamente 1.0 y
     * valida los escenarios de victoria y empate (tablas).
     */
    @Test
    public void testAjedrezValido() {
        //el ajederez siempre debe tener la misma suma del puntaje
        assertTrue(Disciplina.AJEDREZ.esValido(1.0f,0.0f));
        assertTrue(Disciplina.AJEDREZ.esValido(0.5f,0.5f));
        assertFalse(Disciplina.AJEDREZ.esValido(2.0f,0.0f)); //invalido
    }
    /**
     * verifica la logica de puntajes en el futbol.
     * comprueba que se acepten resultados validos y rechaza goles negativos.
     */
    @Test
    public void testFutbolValido() {
        //hay que ver que no esten los goles negativos
        assertTrue(Disciplina.FUTBOL.esValido(2.0f,1.0f));
        assertFalse(Disciplina.FUTBOL.esValido(-1.0f,0.0f));
    }
    /**
     * verifica la logica de puntajes en los videojuegos.
     * comprueba que no existan empates y que se acepten solo victorias claras.
     */
    @Test
    public void testVideojuegosSinEmpate() {
        //verificamos que no hayan empates
        assertFalse(Disciplina.VIDEOJUEGOS.esValido(2.0f,2.0f));
        assertTrue(Disciplina.VIDEOJUEGOS.esValido(3.0f,1.0f));
    }
}