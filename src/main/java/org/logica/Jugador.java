package org.logica;

/**
 * Clase concreta que representa a un participante que compite de manera individual en el torneo.
 */
public class Jugador extends Participante {

    /**
     * Construye una instancia de un jugador individual inicializando sus datos de registro
     * a traves del constructor de la clase padre.
     * @param id Identificador unico del jugador.
     * @param nombre Nombre asignado al jugador.
     * @param contacto Informacion de contacto del jugador.
     */
    public Jugador(String id, String nombre, String contacto) {
        super(id, nombre, contacto);
    }

    /**
     * Devuelve la naturaleza o el tipo de clasificación de este participante.
     * @return Un String que dice "Individual".
     */
    @Override
    public String getTipo() {
        return "Individual";
    }
}
