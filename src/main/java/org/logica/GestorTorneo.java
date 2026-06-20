package org.logica;

import java.util.ArrayList;
import java.util.List;

public class GestorTorneo {


    private static GestorTorneo instanciaUnica;

    private String nombre;
    private Disciplina disciplina;
    private FormatoTorneo formato;
    private List<Participante> Inscritos;

    // El constructor es privado para seguir el patron singleton.
    private GestorTorneo() {
        this.Inscritos = new ArrayList<>();
    }

    // El punto de acceso para obtener la instancia unica
    public static GestorTorneo getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorTorneo();
        }
        return instanciaUnica;
    }

    // Configuracion del torneo

    public void configurarTorneo(String nombre, Disciplina disciplina, FormatoTorneo formato) {
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.formato = formato;
    }

    // Metodo para inscribir personas

    public void inscribirParticipante(Participante participante) {
        this.Inscritos.add(participante);
    }

    // Getters

    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public FormatoTorneo getFormato() { return formato; }
    public List<Participante> getInscritos() { return Inscritos; }
}