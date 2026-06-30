package org.logica;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ParticipanteFactoryTest {
    @Test
    public void testCrearJugador() {
        Participante p=ParticipanteFactory.crearParticipante("Jugador","1","Tomas","123");
        assertTrue(p instanceof Jugador);
        assertEquals("Tomas",p.getNombre());
        assertEquals("Individual",p.getTipo());
    }
    @Test
    public void testCrearEquipo() {
        Participante p=ParticipanteFactory.crearParticipante("Equipo","2","Los Leones","456");
        assertTrue(p instanceof Equipo);
        assertEquals("Los Leones",p.getNombre());
        assertEquals("Equipo",p.getTipo());
    }
    @Test
    public void testTipoInvalido() {
        //verifica que la fabrica lance un error si le pasan un tipo raro
        assertThrows(IllegalArgumentException.class, () -> {
            ParticipanteFactory.crearParticipante("Marciano","3","Alien","000");
        });
    }
}