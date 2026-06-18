package org.logica;

public abstract class Jugador extends Participante {

    public Jugador(String id, String nombre, String contacto) {
        super(id, nombre, contacto);
    }

    @Override
    public String getTipo() {
        return "Individual";
    }
}
