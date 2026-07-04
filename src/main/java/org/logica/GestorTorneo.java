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
            if (Inscritos.size() > 0) {
                int proximaPotencia = 1;
                while (proximaPotencia < Inscritos.size()) {
                    proximaPotencia *= 2;
                }

                int cantidadByes = proximaPotencia - Inscritos.size();
                int indiceActual = 0;

                // Participantes contra un Bye
                for (int i = 0; i < cantidadByes; i++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), new ParticipanteVacio(), 1, "Ganadores"));
                    indiceActual++;
                }
                //  enfrentamiento de resto de participantes
                while (indiceActual < Inscritos.size()) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), Inscritos.get(indiceActual + 1), 1, "Ganadores"));
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
        // La logica de la liga ocurre en una sola ronda
        if (formato == FormatoTorneo.LIGA_SIMPLE) {
            return;
        }

        // Logica eliminatoria directa
        if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {
            List<Participante> clasificados = new ArrayList<>();
            for (Enfrentamiento e : enfrentamientos) {
                if (e.getRonda() == this.rondaActual && e.getGanador() != null) {
                    clasificados.add(e.getGanador());
                }
            }
            if (clasificados.size() == 1) {
                System.out.println("Ganador del Torneo: " + clasificados.get(0).getNombre());
                notificar();
                return;
            }
            this.rondaActual++;
            for (int i = 0; i < clasificados.size(); i += 2) {
                enfrentamientos.add(new Enfrentamiento(clasificados.get(i), clasificados.get(i + 1), this.rondaActual));
            }
            notificar();
            return;
        }

        // Logica eliminatoria doble

        if (formato == FormatoTorneo.ELIMINATORIA_DOBLE) {

            List<Enfrentamiento> wb = new ArrayList<>();    // llave de ganadores
            List<Enfrentamiento> lb = new ArrayList<>();    // llave perdedores
            List<Enfrentamiento> gf = new ArrayList<>();    // guardara a los finalistas

            // Separamos las llaves de la ronda actual
            for (Enfrentamiento e : enfrentamientos) {
                if (e.getRonda() == this.rondaActual) {
                    if (e.getLlave().equals("Ganadores")) wb.add(e);
                    else if (e.getLlave().equals("Perdedores")) lb.add(e);
                    else if (e.getLlave().equals("Gran Final")) gf.add(e);
                }
            }

            this.rondaActual++;

            // Control de la Gran Final
            if (!gf.isEmpty()) {
                Enfrentamiento finalAnterior = gf.get(0);
                Participante ganadorFinal = finalAnterior.getGanador();

                // El que venia del bracket de ganadores debe perder 2 veces para ser derrotado
                if (ganadorFinal == finalAnterior.getParticipante2() &&
                        enfrentamientos.stream().noneMatch(e -> e.getLlave().equals("Gran Final") && e.getRonda() == this.rondaActual)) {
                    enfrentamientos.add(new Enfrentamiento(finalAnterior.getParticipante1(), finalAnterior.getParticipante2(), this.rondaActual, "Gran Final"));
                    notificar();
                    return;
                } else {
                    System.out.println("Ganador Definitivo del Torneo: " + ganadorFinal.getNombre());
                    notificar();
                    return;
                }
            }

            // Nuevas listas para los resultados de los partidos concluidos
            List<Participante> ganadoresWB = new ArrayList<>();
            List<Participante> perdedoresWB = new ArrayList<>();
            List<Participante> ganadoresLB = new ArrayList<>();

            for (Enfrentamiento e : wb) {
                if (e.getGanador() != null) {
                    ganadoresWB.add(e.getGanador());
                    perdedoresWB.add(e.getGanador() == e.getParticipante1() ? e.getParticipante2() : e.getParticipante1());
                }
            }
            for (Enfrentamiento e : lb) {
                if (e.getGanador() != null && !(e.getGanador() instanceof ParticipanteVacio)) {
                    ganadoresLB.add(e.getGanador());
                }
            }

            // Caso 1: Quedan partidos en la wb
            if (wb.size() > 1) {
                for (int i = 0; i < ganadoresWB.size(); i += 2) {
                    enfrentamientos.add(new Enfrentamiento(ganadoresWB.get(i), ganadoresWB.get(i + 1), this.rondaActual, "Ganadores"));
                }
                if (lb.isEmpty()) {
                    for (int i = 0; i < perdedoresWB.size(); i += 2) {
                        enfrentamientos.add(new Enfrentamiento(perdedoresWB.get(i), perdedoresWB.get(i + 1), this.rondaActual, "Perdedores"));
                    }
                }
            }
            // Caso 2: Acaba de terminar la wb
            else if (wb.size() == 1) {
                if (!ganadoresLB.isEmpty()) {
                    for (int i = 0; i < ganadoresLB.size(); i++) {
                        enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(i), perdedoresWB.get(i), this.rondaActual, "Perdedores"));
                    }
                }
            }
            // Caso 3: el ganador de la wb esta esperando en gf, solo la de perdedores sigue activa
            else if (wb.isEmpty() && !lb.isEmpty()) {
                if (ganadoresLB.size() > 1) {
                    for (int i = 0; i < ganadoresLB.size(); i += 2) {
                        enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(i), ganadoresLB.get(i + 1), this.rondaActual, "Perdedores"));
                    }
                }
                else if (ganadoresLB.size() == 1) {
                    Enfrentamiento finalGanadores = enfrentamientos.stream()
                            .filter(e -> e.getLlave().equals("Ganadores"))
                            .reduce((first, second) -> second).orElse(null);

                    if (finalGanadores != null) {
                        Participante invicto = finalGanadores.getGanador();
                        Participante perdedorFinalGanadores = finalGanadores.getGanador() == finalGanadores.getParticipante1() ? finalGanadores.getParticipante2() : finalGanadores.getParticipante1();

                        // Verificamos si el perdedor de la final de ganadores ya jugó en la llave de perdedores
                        boolean yaJugoEnPerdedores = enfrentamientos.stream()
                                .anyMatch(e -> e.getLlave().equals("Perdedores") && (e.getParticipante1() == perdedorFinalGanadores || e.getParticipante2() == perdedorFinalGanadores));

                        if (!yaJugoEnPerdedores) {
                            // Final de la Llave de Perdedores
                            enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(0), perdedorFinalGanadores, this.rondaActual, "Perdedores"));
                        } else {
                            // Final entre ambas llaves
                            enfrentamientos.add(new Enfrentamiento(invicto, ganadoresLB.get(0), this.rondaActual, "Gran Final"));
                        }
                    }
                }
            }
            notificar();
        }
    }

    // Getters

    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public FormatoTorneo getFormato() { return formato; }
    public List<Participante> getInscritos() { return Inscritos; }
    public List<Enfrentamiento> getEnfrentamientos() { return enfrentamientos; }
}