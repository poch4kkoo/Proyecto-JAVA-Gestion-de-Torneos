package org.logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias encargada de validar el manejo
 * de los datos de Jugador y su edicion
 */
public class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() {
        jugador = (Jugador) ParticipanteFactory.crearParticipante("Jugador", "ID1", "Pedro", "12345678");
    }

    @Test
    @DisplayName("Debe almacenar correctamente los datos del jugador")
    void testAtributosIniciales() {
        assertEquals("ID1", jugador.getId(), "El ID debe coincidir");
        assertEquals("Pedro", jugador.getNombre(), "El nombre debe coincidir");
        assertEquals("12345678", jugador.getContacto(), "El contacto debe coincidir");
    }

    @Test
    @DisplayName("Debe permitir actualizar la información de contacto")
    void testSetContacto() {

        jugador.setContacto("pedro@gmail.com");

        assertEquals("pedro@gmail.com", jugador.getContacto(), "El contacto del jugador debió actualizarse.");
    }
}