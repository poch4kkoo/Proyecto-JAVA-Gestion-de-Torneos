package org.logica;

/**
 * Clase que representa un competidor ficticio o nulo dentro del sistema.
 */
public class ParticipanteVacio extends Participante {

    /**
     * Inicializa un nuevo participante vacío asignándole valores por defecto
     * que indican explícitamente la ausencia de un rival real.
     */
    public ParticipanteVacio() {
        super("Error", "Vacio", "Error");
    }

    /**
     * Retorna la identidad de clasificación de esta clase comodín.
     * @return El literal {@code "Vacio"}.
     */
    @Override
    public String getTipo() {
        return "Vacio";
    }
}