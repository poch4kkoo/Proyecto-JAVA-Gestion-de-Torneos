package org.logica;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * clase de pruebas unitarias para el patron factory (participantefactory).
 * verifica la correcta instanciacion de los distintos tipos de participantes.
 */
public class ParticipanteFactoryTest {
    /**
     * verifica que la fabrica cree correctamente un objeto de tipo jugador.
     */
    @Test
    public void testCrearJugador() {
        Participante p=ParticipanteFactory.crearParticipante("Jugador","1","Tomas","123");
        assertTrue(p instanceof Jugador);
        assertEquals("Tomas",p.getNombre());
        assertEquals("Individual",p.getTipo());
    }
    /**
     * verifica que la fabrica cree correctamente un objeto de tipo equipo.
     */
    @Test
    public void testCrearEquipo() {
        Participante p=ParticipanteFactory.crearParticipante("Equipo","2","Los Leones","456");
        assertTrue(p instanceof Equipo);
        assertEquals("Los Leones",p.getNombre());
        assertEquals("Equipo",p.getTipo());
    }
    /**
     * verifica el manejo de excepciones de la fabrica.
     * comprueba que lance un error si se solicita un tipo de participante inexistente.
     */
    @Test
    public void testTipoInvalido() {
        //verifica que la fabrica lance un error si le pasan un tipo raro
        assertThrows(IllegalArgumentException.class, () -> {
            ParticipanteFactory.crearParticipante("Marciano","3","Alien","000");
        });
    }
}