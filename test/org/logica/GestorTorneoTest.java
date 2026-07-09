package org.logica;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 * Clase de pruebas unitarias para la clase principal GestorTorneo.
 * Verifica la logica de inscripcion y la generacion correcta de enfrentamientos.
 */
public class GestorTorneoTest {
    private GestorTorneo gestor;
    /**
     * Configuracion inicial antes de cada prueba.
     * Como el GestorTorneo es un Singleton, limpiamos las listas manualmente
     * para asegurar que las pruebas sean independientes y no se sumen datos.
     */
    @BeforeEach
    public void setUp() {
        gestor=GestorTorneo.getInstancia();
        //limpiamos los datos de otras pruebas
        gestor.getInscritos().clear();
        gestor.getEnfrentamientos().clear();

        //configuramos un torneo base para las pruebas
        gestor.configurarTorneo("Torneo Test", Disciplina.FUTBOL, FormatoTorneo.LIGA_SIMPLE, 9, 0, 3, 60);
    }
    /**
     * Verifica que al registrar un participante valido,
     * este se añada correctamente a la lista interna del gestor.
     */
    @Test
    public void testInscribirParticipanteExitoso() {
        Participante p = ParticipanteFactory.crearParticipante("Equipo", "1", "Los Pumas", "123");
        gestor.inscribirParticipante(p);

        assertEquals(1, gestor.getInscritos().size(), "El participante deberia haberse añadido a la lista de inscritos");
        assertEquals("Los Pumas", gestor.getInscritos().get(0).getNombre());
    }
    /**
     * Verifica que al eliminar un participante previamente inscrito,
     * la lista se actualice correctamente.
     */
    @Test
    public void testEliminarParticipante() {
        Participante p = ParticipanteFactory.crearParticipante("Equipo", "1", "Los Pumas", "123");
        gestor.inscribirParticipante(p);
        gestor.eliminarParticipante(p);

        assertTrue(gestor.getInscritos().isEmpty(), "La lista de inscritos deberia estar vacia tras eliminar al participante");
    }
    /**
     * Verifica que la generacion de llaves funcione correctamente para el
     * formato de Liga Simple (todos contra todos).
     * Formula matematica:n por (n-1)/2.Para 4 equipos,deben ser 6 partidos.
     */
    @Test
    public void testGenerarTorneoLigaSimple() {
        //inscribimos 4 equipos
        gestor.inscribirParticipante(ParticipanteFactory.crearParticipante("Equipo","1","Equipo A","111"));
        gestor.inscribirParticipante(ParticipanteFactory.crearParticipante("Equipo","2","Equipo B","222"));
        gestor.inscribirParticipante(ParticipanteFactory.crearParticipante("Equipo","3","Equipo C","333"));
        gestor.inscribirParticipante(ParticipanteFactory.crearParticipante("Equipo","4","Equipo D","444"));
        //generamos el torneo
        gestor.generarTorneo();
        //verificamos que se hayan creado exactamente 6 enfrentamientos
        assertEquals(6, gestor.getEnfrentamientos().size(), "4 equipos en liga simple deben generar exactamente 6 enfrentamientos");
    }
}