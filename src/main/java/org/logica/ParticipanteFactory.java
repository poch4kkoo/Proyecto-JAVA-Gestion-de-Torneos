package org.logica;

/**
 * Clase de utilidad que implementa el patrón de diseño Factory.
 * Se encarga de centralizar y desacoplar la creación de instancias concretas de Participantes.
 */
public class ParticipanteFactory {

    /**
     * Fabrica y devuelve una instacia especifica de un participante dependiendo del tio seleccionado por le usuario.
     * @param tipo Naturaleza del competidor ("Jugador" o "Equipo").
     * @param id Identificador unico del nuevo participante.
     * @param nombre Nombre asignado al nuevo participante.
     * @param contacto Informacion de contacto del nuevo participante.
     * @return Una instacia concreta derivada de la clase abstracta Participante.
     * @throws IllegalArgumentException Si el parametro tipo no coincide con ninguna subclase valida ("Jugador" o "Equipo").
     */
    public static Participante crearParticipante(String tipo, String id, String nombre, String contacto) {
        if (tipo.equalsIgnoreCase("Jugador")) {
            return new Jugador(id, nombre, contacto);
        } else if (tipo.equalsIgnoreCase("Equipo")) {
            return new Equipo(id, nombre, contacto);
        }
        throw new IllegalArgumentException("Tipo de participante no reconocido: " + tipo);
    }
}