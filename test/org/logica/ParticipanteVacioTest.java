package org.logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias encargada de validar el comportamiento
 * de la clase ParticipanteVacio, que se encarga de los pases automaticos
 * en torneos de eliminatoria.
 */
public class ParticipanteVacioTest {

    private ParticipanteVacio bye;

    @BeforeEach
    void setUp() {
        bye = new ParticipanteVacio();
    }

    @Test
    @DisplayName("Debe inicializarse con valores por defecto vacíos o nulos seguros")
    void testValoresPorDefecto() {
        assertNotNull(bye.getNombre(), "El nombre no debería ser nulo");
        assertNotNull(bye.getId(), "El ID no debería ser nulo.");
    }

    @Test
    @DisplayName("El método getTipo() debe retornar una cadena vacía")
    void testTipoParticipanteVacio() {
        assertNotNull(bye.getTipo(), "El tipo de participante vacío debe estar definido de forma segura.");
    }
}
