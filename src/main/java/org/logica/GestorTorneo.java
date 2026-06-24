package org.logica;

import java.util.ArrayList;
import java.util.List;

public class GestorTorneo {


    private static GestorTorneo instanciaUnica;

    private String nombre;
    private Disciplina disciplina;
    private FormatoTorneo formato;
    private List<Participante> Inscritos;
    private List<Enfrentamiento> enfrentamientos;

    // El constructor es privado para seguir el patron singleton.
    private GestorTorneo() {

        this.Inscritos = new ArrayList<>();
        this.enfrentamientos = new ArrayList<>();
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

    public void generarTorneo() {
        this.enfrentamientos.clear();

        //Liga simple: todos contra todos
        if (formato == FormatoTorneo.LIGA_SIMPLE) {
            for (int i = 0; i < Inscritos.size(); i++) {
                for (int j = i + 1; j < Inscritos.size(); j++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(i), Inscritos.get(j)));
                }
            }
        }

        //Eliminatoria directa: si pierde queda eliminado inmediatamente.
        //Hasta ahora solo se tiene la logica para emparejar para la primera ronda.

        //lo mas ideal seria que las inscripciones sean una potencia de 2.
        //En caso de que no lo sean, se asignaran Byes
        else if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {

            // Cambia el tamaño de la tabla segun cuantos participantes sean
            if (Inscritos.size() > 0) {
                int proximaPotencia = 1;
                while (proximaPotencia < Inscritos.size()) {
                    proximaPotencia *= 2;
                }

                // Si faltan participantes, se agrega un "Bye"
                while (Inscritos.size() < proximaPotencia) {
                    Inscritos.add(new ParticipanteVacio());
                }
            }

            for (int i = 0; i < Inscritos.size(); i += 2) {
                enfrentamientos.add(new Enfrentamiento(Inscritos.get(i), Inscritos.get(i + 1)));
            }
        }

        //Se debe perder dos veces para que de verdad quede eliminado.
        //Si pierde una vez baja a la llave de perdedores, si no pierde queda en la llave de ganadores.
        //Solo se tiene la logica para la primera ronda.
        else if (formato == FormatoTorneo.ELIMINATORIA_DOBLE) {

            // Cambia el tamaño de la tabla segun cuantos participantes sean
            if (Inscritos.size() > 0) {
                int proximaPotencia = 1;
                while (proximaPotencia < Inscritos.size()) {
                    proximaPotencia *= 2;
                }

                // Si faltan participantes, se agrega un Bye
                while (Inscritos.size() < proximaPotencia) {
                    Inscritos.add(new ParticipanteVacio());
                }
            }

            for (int i = 0; i < Inscritos.size(); i += 2) {
                enfrentamientos.add(new Enfrentamiento(Inscritos.get(i), Inscritos.get(i + 1)));
            }
        }
    }

    // Getters

    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public FormatoTorneo getFormato() { return formato; }
    public List<Participante> getInscritos() { return Inscritos; }
    public List<Enfrentamiento> getEnfrentamientos() { return enfrentamientos; }
}