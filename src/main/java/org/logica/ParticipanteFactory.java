package org.logica;
public class ParticipanteFactory {

    /**
     * Fabrica un participante dependiendo del tipo seleccionado en la interfaz.
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