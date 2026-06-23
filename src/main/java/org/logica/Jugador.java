package org.logica;

public class Jugador extends Participante {

    public Jugador(String id, String nombre, String contacto) {
        super(id, nombre, contacto);
    }

    @Override
    public String getTipo() {
        return "Individual";
    }
}
