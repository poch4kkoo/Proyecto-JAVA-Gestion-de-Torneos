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
    private List<Observer> observadores;
    private int rondaActual;

    // El constructor es privado para seguir el patron singleton.
    private GestorTorneo() {

        this.Inscritos = new ArrayList<>();
        this.enfrentamientos = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // El punto de acceso para obtener la instancia unica
    public static GestorTorneo getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorTorneo();
        }
        return instanciaUnica;
    }

    public void registrarObserver(Observer o) {
        observadores.add(o);
    }

    public void eliminarObserver(Observer o) {
        observadores.remove(o);
    }

    public void notificar() {
        for (Observer o : observadores) {
            o.actualizar();
        }
    }

    // Configuracion del torneo

    public void configurarTorneo(String nombre, Disciplina disciplina, FormatoTorneo formato) {
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.formato = formato;
        notificar();
    }

    // Metodo para inscribir personas

    public void inscribirParticipante(Participante participante) {
        this.Inscritos.add(participante);
        notificar();
    }

    public void eliminarParticipante(Participante participante) {
        this.Inscritos.remove(participante);
        notificar();
    }

    public void generarTorneo() {
        this.enfrentamientos.clear();
        this.rondaActual = 1;

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

                int cantidadByes = proximaPotencia - Inscritos.size();
                int indiceActual = 0;

                // Emparejamos a los participantes con un bye
                for (int i = 0; i < cantidadByes; i++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), new ParticipanteVacio()));
                    indiceActual++;
                }

                // Emparejamos a los demas participantes
                while (indiceActual < Inscritos.size()) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), Inscritos.get(indiceActual + 1)));
                    indiceActual += 2;
                }
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

                int cantidadByes = proximaPotencia - Inscritos.size();
                int indiceActual = 0;

                // Emparejamos a los participantes con un bye
                for (int i = 0; i < cantidadByes; i++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), new ParticipanteVacio()));
                    indiceActual++;
                }

                // Emparejamos a los demas participantes
                while (indiceActual < Inscritos.size()) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), Inscritos.get(indiceActual + 1)));
                    indiceActual += 2;
                }
            }
        }

        notificar();
    }


    public boolean rondaActualTerminada() {
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual && !e.isJugado()) {
                // avanza de ronda si no hay rival.
                if (e.getParticipante1() instanceof ParticipanteVacio || e.getParticipante2() instanceof ParticipanteVacio) {
                    continue;
                }
                return false; // Hay partidos pendientes
            }
        }
        return true;
    }

    /**
     * Avanza a la siguiente ronda creando una nueva lista con los
     * ganadores de la anterior ronda
     *
     */
    public void avanzarRonda() {

        List<Participante> clasificados = new ArrayList<>();

        // Añade a los ganadores a la lista de clasificados
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                Participante ganador = e.getGanador();
                if (ganador != null) {
                    clasificados.add(ganador);
                }
            }
        }

        // Comprueba si alguien gano el torneo
        if (clasificados.size() == 1) {
            System.out.println("Ganador del Torneo: " + clasificados.get(0).getNombre());
            notificar();
            return;
        }

        // Genera los enfrentamientos de esta ronda
        this.rondaActual++;
        for (int i = 0; i < clasificados.size(); i += 2) {
            enfrentamientos.add(new Enfrentamiento(clasificados.get(i), clasificados.get(i + 1), this.rondaActual));
        }

        notificar();
    }

    // Getters

    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public FormatoTorneo getFormato() { return formato; }
    public List<Participante> getInscritos() { return Inscritos; }
    public List<Enfrentamiento> getEnfrentamientos() { return enfrentamientos; }
}