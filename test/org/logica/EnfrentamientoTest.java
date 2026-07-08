package org.logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias encargada de validar el correcto funcionamiento
 * de la lógica interna de los enfrentamientos y la gestión de marcadores.
 */
public class EnfrentamientoTest {

    private Participante equipo1;
    private Participante equipo2;
    private Enfrentamiento enfrentamiento;

    //Configuración inicial
    @BeforeEach
    void setUp() {

        // Preparamos el singleton con la disciplina Futbol
        GestorTorneo.getInstancia().configurarTorneo("Torneo Test", Disciplina.FUTBOL, FormatoTorneo.ELIMINATORIA_DIRECTA, 9, 0, 3, 60);

        //Creamos a los participantes
        equipo1 = ParticipanteFactory.crearParticipante("Equipo", "1", "Argentina", "111");
        equipo2 = ParticipanteFactory.crearParticipante("Equipo", "2", "Chile", "222");

        enfrentamiento = new Enfrentamiento(equipo1, equipo2, 1, "Eliminatoria");

    }

    /**
     * Verifica que las propiedades asignadas en el constructor se almacenen
     * de manera correcta y el partido inicie en un estado no jugado.
     */
    @Test
    void testEstadoInicialEnfrentamiento() {
        assertNotNull(enfrentamiento, "El enfrentamiento no debería ser nulo.");
        assertEquals(equipo1, enfrentamiento.getParticipante1(), "El participante 1 debe ser Argentina.");
        assertEquals(equipo2, enfrentamiento.getParticipante2(), "El participante 2 debe ser Chile.");
        assertEquals(1, enfrentamiento.getRonda(), "La ronda inicial configurada debe ser 1.");
        assertEquals("Eliminatoria", enfrentamiento.getLlave(), "La llave asignada debe ser 'Eliminatoria'.");
        assertFalse(enfrentamiento.isJugado(), "El enfrentamiento no debería marcarse como jugado al inicio.");
        assertNull(enfrentamiento.getGanador(), "El ganador inicial debe ser nulo.");
    }

    /**
     * Valida el registro de un resultado donde el primer participante sale resulta victorioso.
     */
    @Test
    void testRegistrarResultadoVictoriaLocal() {
        // Simulación de marcador: Argentina 3 - 1 Chile
        enfrentamiento.registrarResultado(3, 1);

        assertTrue(enfrentamiento.isJugado(), "El partido debería cambiar su estado a jugado.");
        assertEquals(equipo1, enfrentamiento.getGanador(), "El ganador debería ser el equipo 1 (Argentina).");
    }

    /**
     * Valida el registro de un resultado donde el segundo participante sale victorioso.
     */
    @Test
    void testRegistrarResultadoVictoriaVisitante() {
        // Simulación de marcador: Argentina 0 - 2 Chile
        enfrentamiento.registrarResultado(0, 2);

        assertTrue(enfrentamiento.isJugado(), "El partido debería cambiar su estado a jugado.");
        assertEquals(equipo2, enfrentamiento.getGanador(), "El ganador debería ser el equipo 2 (Chile).");
    }

    /**
     * Valida el comportamiento del sistema ante un marcador de empate.
     */
    @Test
    void testRegistrarResultadoEmpate() {
        // Simulación de marcador: Argentina 2 - 2 Chile
        enfrentamiento.registrarResultado(2, 2);

        assertTrue(enfrentamiento.isJugado(), "El partido debe marcarse como jugado.");
        assertNull(enfrentamiento.getGanador(), "En caso de empate reglamentario, el ganador directo debería ser nulo.");
    }
}