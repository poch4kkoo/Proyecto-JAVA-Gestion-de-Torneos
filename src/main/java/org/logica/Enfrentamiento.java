package org.logica;

public class Enfrentamiento {
    private Participante participante1;
    private Participante participante2;
    private float puntaje1;
    private float puntaje2;
    private boolean jugado;
    private int ronda;

    public Enfrentamiento(Participante participante1, Participante participante2) {
        this.participante1 = participante1;
        this.participante2 = participante2;
        puntaje1 = 0;
        puntaje2 = 0;
        jugado = false;
        this.ronda = 1;

    }

    public Enfrentamiento(Participante participante1, Participante participante2, int ronda) {
        this.participante1 = participante1;
        this.participante2 = participante2;
        puntaje1 = 0;
        puntaje2 = 0;
        jugado = false;
        this.ronda = ronda;
    }

    public void registrarResultado(float puntaje1, float puntaje2) {
        Disciplina disciplinaActual = GestorTorneo.getInstancia().getDisciplina();
        if (!disciplinaActual.esValido(puntaje1, puntaje2)) {
            throw new IllegalArgumentException("Marcador no permitido para la disciplina: " + disciplinaActual);
        }

        this.puntaje1 = puntaje1;
        this.puntaje2 = puntaje2;
        this.jugado = true;

        GestorTorneo.getInstancia().notificar();
    }

    public Participante getGanador() {

        // Si no se enfrenta a nadie, gana automaticamente
        if (participante1 instanceof ParticipanteVacio) return participante2;
        if (participante2 instanceof ParticipanteVacio) return participante1;

        if (!jugado) {
            return null;
        }

        if (puntaje1 > puntaje2) {
            return participante1;
        }

        if (puntaje2 > puntaje1) {
            return participante2;
        }

        return null;
    }

    public Participante getParticipante1() {
        return participante1;
    }

    public Participante getParticipante2() {
        return participante2;
    }

    public float getPuntaje1() {
        return puntaje1;
    }

    public float getPuntaje2() {
        return puntaje2;
    }

    public boolean isJugado() {
        return jugado;
    }

    public int getRonda() { return ronda; }

    private String puntajeResultados() {
        return jugado ? String.valueOf(puntaje2) : "-";
    }

    @Override
    public String toString() {
        if (jugado) {
            return participante1.getNombre() + " (" + puntaje1 + ") vs (" + puntajeResultados() + ") " + participante2.getNombre();
        }
        return participante1.getNombre() + " vs " + participante2.getNombre() + " (Pendiente)";
    }

}
