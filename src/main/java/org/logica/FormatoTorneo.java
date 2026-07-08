package org.logica;

/**
 * Enumeración que define las modalidades o estructuras de competición admitidas por el sistema.
 */
public enum FormatoTorneo {

    //Formato de muerte súbita o eliminación simple.
    //El participante queda descalificado inmediatamente despues de perder.
    ELIMINATORIA_DIRECTA,

    //Formato de eliminacion doble, se generan dos cuadros.
    //Una vez pierde un participante no queda eliminado de meanera inmediata, sino que
    //baja a el Cuadro de los Perdedores.
    ELIMINATORIA_DOBLE,

    //Formato de liga tradicional o "todos contra todos"
    LIGA_SIMPLE
}
