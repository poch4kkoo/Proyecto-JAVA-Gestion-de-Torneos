package org.logica;

public class ParticipanteVacio extends Participante {

    public ParticipanteVacio() {
        super("Error", "Vacio", "Error");
    }

    @Override
    public String getTipo() {
        return "Vacio";
    }
}