package org.logica;

import java.util.ArrayList;
import java.util.List;

public class Equipo extends Participante{
    private List<String> nombresMiembros;

    public Equipo(String id, String nombre, String contacto) {
        super(id, nombre, contacto);
        this.nombresMiembros = new ArrayList<>();
    }

    public void agregarMiembro(String nombreJugador) {
        this.nombresMiembros.add(nombreJugador);
    }

    public void removerMiembro(String nombreJugador) { this.nombresMiembros.remove(nombreJugador);}

    public List<String> getNombresMiembros() {
        return nombresMiembros;
    }

    @Override
    public String getTipo() {
        return "Equipo";
    }
}
