package org.logica;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase concreta que representa a un participante en forma de equipo.
 */
public class Equipo extends Participante{

    //Lista con los nombres de los miembros que conforman el equipo.
    private List<String> nombresMiembros;

    /**
     * Construye un nuevo competidor grupal e inicializa el listado de atletas asociados al equipo.
     *
     * @param id Identificador unico del equipo.
     * @param nombre Nombre del equipo.
     * @param contacto Informacion de contacto del equipo.
     */
    public Equipo(String id, String nombre, String contacto) {
        super(id, nombre, contacto);
        this.nombresMiembros = new ArrayList<>();
    }

    /**
     * Añade unnuevo miembro al equipo.
     * @param nombreJugador Nombre del jugador que se integra al equipo.
     */
    public void agregarMiembro(String nombreJugador) {

        if(nombreJugador == null || nombreJugador.trim().isEmpty()) { return ;}
        if(nombresMiembros.contains(nombreJugador.trim())) { return ; }

        this.nombresMiembros.add(nombreJugador.trim());
    }

    /**
     * Remueve a un integrante en especifico del equipo.
     * @param nombreJugador Nombre del jugador a eliminar del equipo.
     */
    public void removerMiembro(String nombreJugador) { this.nombresMiembros.remove(nombreJugador);}

    /**
     * Obtiene la nomina de todos los integrantes inscritos bajo este equipo.
     * @return Una lista que contiene los nombres de los integrantes del equipo.
     */
    public List<String> getNombresMiembros() {
        return nombresMiembros;
    }

    /**
     * Devuelve la naturaleza o el tipo de clasificación de este participante.
     * @return Un String que dice "Equipo".
     */
    @Override
    public String getTipo() {
        return "Equipo";
    }
}
