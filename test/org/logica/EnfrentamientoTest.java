package org.logica;

import org.junit.jupiter.api.BeforeEach;

public class EnfrentamientoTest {

    private Participante equipo1;
    private Participante equipo2;
    private Enfrentamiento enfrentamiento;

    @BeforeEach
    void setUp() {

        // Preparamos el singleton con la disciplina Futbol
        GestorTorneo.getInstancia().configurarTorneo("Torneo Test", Disciplina.FUTBOL, FormatoTorneo.ELIMINATORIA_DIRECTA);

        // creamos a los participantes
        equipo1 = ParticipanteFactory.crearParticipante("Equipo", "1", "Argentina", "111");
        equipo2 = ParticipanteFactory.crearParticipante("Equipo", "2", "Chile", "222");

        enfrentamiento = new Enfrentamiento(equipo1, equipo2, 1, "Eliminatoria");
    }


}