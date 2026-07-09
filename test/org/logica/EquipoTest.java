package org.logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas unitarias encargada de validar la lógica interna de la clase Equipo.
 */
public class EquipoTest {
    private Equipo equipo;

    //Configuracion inicial
    @BeforeEach
    void setUp() {
        equipo = (Equipo) ParticipanteFactory.crearParticipante("Equipo", "1", "COLO COLO", "contacto@colo.cl");
    }

    /**
     * Valida la correcta incorporacion de un integrante valido a la nomina oficial del equipo.
     */
    @Test
    void testAgregarMiembroExitoso() {
        equipo.agregarMiembro("Juan");
        assertEquals(1, equipo.getNombresMiembros().size());
        assertEquals("Juan", equipo.getNombresMiembros().get(0));
    }

    /**
     * Asegura que el sistema descarte e ignore valores nulos o cadenas con puros espacios en blanco,
     * impidiendo que la lista interna sea alterada.
     */
    @Test
    void testAgregarMiembroInvalidoNoModificaLista() {
        equipo.agregarMiembro(null);
        equipo.agregarMiembro("   ");
        assertTrue(equipo.getNombresMiembros().isEmpty(), "No se deben agregar miembros nulos o vacíos.");
    }

    /**
     * Valida que no se registren duplicados del mismo deportista, incluso si vienen con espacios adicionales.
     */
    @Test
    void testNoPermitirMiembrosDuplicados() {
        equipo.agregarMiembro("Marcelo Martinez");
        equipo.agregarMiembro("Marcelo Martinez     ");

        assertEquals(1, equipo.getNombresMiembros().size(), "El sistema no debe registrar duplicados en la plantilla.");
    }

    /**
     * Valida la correcta eliminacion de un atleta de la nomina del equipo.
     */
    @Test
    void testRemoverMiembroExitoso() {
        equipo.agregarMiembro("Pou");
        equipo.removerMiembro("Pou");
        assertTrue(equipo.getNombresMiembros().isEmpty());
    }
}