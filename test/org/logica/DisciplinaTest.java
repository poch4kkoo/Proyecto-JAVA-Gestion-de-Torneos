package org.logica;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DisciplinaTest {
    @Test
    public void testAjedrezValido() {
        //el ajederez siempre debe tener la misma suma del puntaje
        assertTrue(Disciplina.AJEDREZ.esValido(1.0f,0.0f));
        assertTrue(Disciplina.AJEDREZ.esValido(0.5f,0.5f));
        assertFalse(Disciplina.AJEDREZ.esValido(2.0f,0.0f)); //invalido
    }
    @Test
    public void testFutbolValido() {
        //hay que ver que no esten los goles negativos
        assertTrue(Disciplina.FUTBOL.esValido(2.0f,1.0f));
        assertFalse(Disciplina.FUTBOL.esValido(-1.0f,0.0f));
    }
    @Test
    public void testVideojuegosSinEmpate() {
        //verificamos que no hayan empates
        assertFalse(Disciplina.VIDEOJUEGOS.esValido(2.0f,2.0f));
        assertTrue(Disciplina.VIDEOJUEGOS.esValido(3.0f,1.0f));
    }
}