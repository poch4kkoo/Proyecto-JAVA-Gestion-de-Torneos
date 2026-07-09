package org.logica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias encargada de validar las reglas de restricciones de puntuación y formatos
 * específicos de las Disciplinas.
 */
public class DisciplinaTest {

    /**
     * Valida el comportamiento de puntuación del Ajedrez.
     * Verifica que se acepten puntuaciones válidas (1.0, 0.5, 0.0) y que su suma combinada
     * sea siempre igual a 1.0 (Tablas o Victoria/Derrota).
     */
    @Test
    public void testAjedrezValido() {
        //el ajederez siempre debe tener la misma suma del puntaje
        assertTrue(Disciplina.AJEDREZ.esValido(1.0f,0.0f));
        assertTrue(Disciplina.AJEDREZ.esValido(0.5f,0.5f));
        assertFalse(Disciplina.AJEDREZ.esValido(2.0f,0.0f)); //invalido
    }

    /**
     * Valida las restricciones de puntaje para la disciplina de Futbol.
     * Asegura que el sistema acepte marcadores enteros no negativos y rechace goles negativos.
     */
    @Test
    public void testFutbolValido() {
        //hay que ver que no esten los goles negativos
        assertTrue(Disciplina.FUTBOL.esValido(2.0f,1.0f));
        assertFalse(Disciplina.FUTBOL.esValido(-1.0f,0.0f));
    }

    /**
     * Valida la regla de exclusión de empates en la disciplina de Videojuegos.
     * Verifica que el sistema rechace marcadores iguales en juegos donde
     * obligatoriamente debe haber un ganador por competicion.
     */
    @Test
    public void testVideojuegosSinEmpate() {
        //verificamos que no hayan empates
        assertFalse(Disciplina.VIDEOJUEGOS.esValido(2.0f,2.0f));
        assertTrue(Disciplina.VIDEOJUEGOS.esValido(3.0f,1.0f));
    }
}