package org.logica;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias para la fabrica ParticipanteFactory.
 */
public class ParticipanteFactoryTest {

    /**
     * Verifica que la fabrica cree correctamente una instancia de Jugador
     * cuando se solicita el tipo "Jugador", validando sus propiedades basicas.
     */
    @Test
    public void testCrearJugador() {
        Participante p=ParticipanteFactory.crearParticipante("Jugador","1","Tomas","123");
        assertTrue(p instanceof Jugador);
        assertEquals("Tomas",p.getNombre());
        assertEquals("Individual",p.getTipo());
    }

    /**
     * Verifica que la fabrica cree correctamente una instancia de Equipo
     * cuando se solicita el tipo "Equipo", validando sus propiedades basicas.
     */
    @Test
    public void testCrearEquipo() {
        Participante p=ParticipanteFactory.crearParticipante("Equipo","2","Los Leones","456");
        assertTrue(p instanceof Equipo);
        assertEquals("Los Leones",p.getNombre());
        assertEquals("Equipo",p.getTipo());
    }

    /**
     * Valida que la fabrica lance una excepcian controlada si se ingresa un tipo desconocido.
     */
    @Test
    public void testTipoInvalido() {
        //verifica que la fabrica lance un error si le pasan un tipo raro
        assertThrows(IllegalArgumentException.class, () -> {
            ParticipanteFactory.crearParticipante("Marciano","3","Alien","000");
        });
    }

    /**
     * Valida que el metodo de la fabrica sea insensible a mayusculas/minusculas
     * al evaluar las cadenas que determinan el tipo de participante.
     */
    @Test
    public void testCrearParticipanteIgnoraMayusculas() {
        Participante p1 = ParticipanteFactory.crearParticipante("jugador", "10", "Juan", "abc");
        Participante p2 = ParticipanteFactory.crearParticipante("EQUIPO", "11", "Club", "def");

        assertTrue(p1 instanceof Jugador);
        assertTrue(p2 instanceof Equipo);
    }
}