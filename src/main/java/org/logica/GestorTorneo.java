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
    private int horaInicio = 9;//nuevos
    private int minutoInicio = 0;//nuevos
    private int maxCanchas = 3;//nuevos
    private int intervaloMinutos = 60;//nuevo

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

    public void configurarTorneo(String nombre, Disciplina disciplina, FormatoTorneo formato, int horaInicio, int minutoInicio, int maxCanchas, int intervaloMinutos) {
        this.nombre = nombre;
        this.disciplina=disciplina;
        this.formato=formato;
        this.horaInicio=horaInicio;
        this.minutoInicio=minutoInicio;
        this.maxCanchas=maxCanchas;
        this.intervaloMinutos=intervaloMinutos;
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


    // metodo para editar enfrentamientos
    public void intercambiarParticipantes(Participante p1, Participante p2) {
        if (p1 == p2 || this.rondaActual != 1) return; // Protección

        Enfrentamiento enf1 = null, enf2 = null;
        boolean p1EsLocal = true, p2EsLocal = true;

        // Buscar enfrentamientos actuales
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == 1) {
                if (e.getParticipante1() == p1) { enf1 = e; p1EsLocal = true; }
                else if (e.getParticipante2() == p1) { enf1 = e; p1EsLocal = false; }

                if (e.getParticipante1() == p2) { enf2 = e; p2EsLocal = true; }
                else if (e.getParticipante2() == p2) { enf2 = e; p2EsLocal = false; }
            }
        }

        // Si encontramos a ambos, invertimos sus posiciones
        if (enf1 != null && enf2 != null) {
            if (p1EsLocal) enf1.setParticipante1(p2); else enf1.setParticipante2(p2);
            if (p2EsLocal) enf2.setParticipante1(p1); else enf2.setParticipante2(p1);
            notificar();
        }
    }

    private void registrarEnfrentamientoSeguro(Enfrentamiento e) {
        if (e.getParticipante1() instanceof ParticipanteVacio) {
            e.registrarResultado(0, 1); // Auto-gana el 2
        } else if (e.getParticipante2() instanceof ParticipanteVacio) {
            e.registrarResultado(1, 0); // Auto-gana el 1
        }
        this.enfrentamientos.add(e);
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
        asignarHorariosAutomaticos();
        notificar();
    }


    public boolean rondaActualTerminada() {
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                if (!e.isJugado()) {
                    // Avanza de ronda si no hay rival
                    if (e.getParticipante1() instanceof ParticipanteVacio || e.getParticipante2() instanceof ParticipanteVacio) {
                        continue;
                    }
                    return false; //hay partidos pendientes
                }

                //regla nueva: si es eliminatoria y hay empate, bloqueamos el avance
                if (formato != FormatoTorneo.LIGA_SIMPLE && e.getGanador() == null) {
                    System.out.println("No se puede avanzar: Hay un partido empatado que requiere definición.");
                    return false;
                }
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

        List<Participante> ganadoresWB = new ArrayList<>();
        List<Participante> perdedoresWB = new ArrayList<>();
        List<Participante> ganadoresLB = new ArrayList<>();

        List<Enfrentamiento> wbAnterior = new ArrayList<>();
        List<Enfrentamiento> lbAnterior = new ArrayList<>();

        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                if (e.getLlave().equalsIgnoreCase("Ganadores")) {
                    wbAnterior.add(e);

                    if (e.isJugado() && e.getGanador() != null) {
                        ganadoresWB.add(e.getGanador());
                        // Identificamos quién perdió en la Winners Bracket
                        Participante perdedor = (e.getGanador() == e.getParticipante1())
                                ? e.getParticipante2()
                                : e.getParticipante1();

                        if (perdedor != null && !(perdedor instanceof ParticipanteVacio)) {
                            perdedoresWB.add(perdedor);
                        }
                    }
                } else if (e.getLlave().equalsIgnoreCase("Perdedores")) {
                    lbAnterior.add(e);
                    if (e.isJugado() && e.getGanador() != null) {
                        ganadoresLB.add(e.getGanador());
                    }
                }
            }
        }

        Participante perdedorFinalGanadoresFijado = null;
        Participante invictoGanadores = null;

        Enfrentamiento finalGanadoresMatch = enfrentamientos.stream()
                .filter(e -> e.getLlave().equalsIgnoreCase("Ganadores"))
                .reduce((first, second) -> second).orElse(null);

        if (finalGanadoresMatch != null && finalGanadoresMatch.isJugado() && finalGanadoresMatch.getRonda() >= 3) {
            invictoGanadores = finalGanadoresMatch.getGanador();
            perdedorFinalGanadoresFijado = (finalGanadoresMatch.getGanador() == finalGanadoresMatch.getParticipante1())
                    ? finalGanadoresMatch.getParticipante2()
                    : finalGanadoresMatch.getParticipante1();

            perdedoresWB.remove(perdedorFinalGanadoresFijado);
            ganadoresLB.remove(perdedorFinalGanadoresFijado);
        }

        this.rondaActual++;

        // CASO 1: Quedan partidos activos corriendo en la llave de ganadores
        if (wbAnterior.size() > 1) {

            int limiteGWB = ganadoresWB.size();
            boolean esImparGWB = (limiteGWB % 2 != 0);
            if (esImparGWB) limiteGWB--;

            for (int i = 0; i < limiteGWB; i += 2) {
                enfrentamientos.add(new Enfrentamiento(ganadoresWB.get(i), ganadoresWB.get(i + 1), this.rondaActual, "Ganadores"));
            }
            if (esImparGWB) {
                registrarEnfrentamientoSeguro(new Enfrentamiento(ganadoresWB.get(ganadoresWB.size() - 1), new ParticipanteVacio(), this.rondaActual, "Ganadores"));
            }

            // Manejo de la llave de perdedores en el Caso 1
            if (lbAnterior.isEmpty()) {
                int limitePWB = perdedoresWB.size();
                boolean esImparPWB = (limitePWB % 2 != 0);
                if (esImparPWB) limitePWB--;

                for (int i = 0; i < limitePWB; i += 2) {
                    enfrentamientos.add(new Enfrentamiento(perdedoresWB.get(i), perdedoresWB.get(i + 1), this.rondaActual, "Perdedores"));
                }
                if (esImparPWB) {
                    registrarEnfrentamientoSeguro(new Enfrentamiento(perdedoresWB.get(perdedoresWB.size() - 1), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                }
            } else {
                int i = 0;
                while (i < ganadoresLB.size() && i < perdedoresWB.size()) {
                    enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(i), perdedoresWB.get(i), this.rondaActual, "Perdedores"));
                    i++;
                }
                while (i < ganadoresLB.size()) {
                    registrarEnfrentamientoSeguro(new Enfrentamiento(ganadoresLB.get(i), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                    i++;
                }
                while (i < perdedoresWB.size()) {
                    registrarEnfrentamientoSeguro(new Enfrentamiento(perdedoresWB.get(i), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                    i++;
                }
            }
        }

        // CASO 2: la llave de ganadores se redujo a 1 solo partido
        else if (wbAnterior.size() == 1 && perdedorFinalGanadoresFijado == null) {
            if (!ganadoresLB.isEmpty()) {
                int i = 0;
                while (i < ganadoresLB.size() && i < perdedoresWB.size()) {
                    enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(i), perdedoresWB.get(i), this.rondaActual, "Perdedores"));
                    i++;
                }
                while (i < ganadoresLB.size()) {
                    registrarEnfrentamientoSeguro(new Enfrentamiento(ganadoresLB.get(i), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                    i++;
                }
                while (i < perdedoresWB.size()) {
                    registrarEnfrentamientoSeguro(new Enfrentamiento(perdedoresWB.get(i), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                    i++;
                }
            }
        }

        // CASO 3: La Winners Bracket terminó (wbAnterior está vacía) o el finalista de arriba está esperando
        else if (wbAnterior.isEmpty() || perdedorFinalGanadoresFijado != null) {

            String etapaActual = determinarEtapaFinalDoble();

            switch (etapaActual) {
                case "SemifinalLB":
                    // Se enfrentan los dos sobrevivientes de la llave de abajo de forma aislada
                    if (ganadoresLB.size() >= 2) {
                        enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(0), ganadoresLB.get(1), this.rondaActual, "Perdedores"));
                    }
                    break;

                case "FinalLB":
                    // El ganador de la semifinal de abajo juega contra el perdedor de la final de ganadores
                    if (ganadoresLB.size() == 1 && perdedorFinalGanadoresFijado != null) {
                        enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(0), perdedorFinalGanadoresFijado, this.rondaActual, "Perdedores"));
                    }
                    break;

                case "GranFinal":
                    // Partido por el campeonato definitivo
                    if (ganadoresLB.size() == 1 && invictoGanadores != null) {
                        enfrentamientos.add(new Enfrentamiento(invictoGanadores, ganadoresLB.get(0), this.rondaActual, "Gran Final"));
                    }
                    break;

                default:

                    if (ganadoresLB.size() > 1) {
                        int limiteLB = ganadoresLB.size();
                        boolean esImparLB = (limiteLB % 2 != 0);
                        if (esImparLB) limiteLB--;

                        for (int i = 0; i < limiteLB; i += 2) {
                            enfrentamientos.add(new Enfrentamiento(ganadoresLB.get(i), ganadoresLB.get(i + 1), this.rondaActual, "Perdedores"));
                        }
                        if (esImparLB) {
                            registrarEnfrentamientoSeguro(new Enfrentamiento(ganadoresLB.get(ganadoresLB.size() - 1), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                        }
                    } else if (ganadoresLB.size() == 1) {
                        registrarEnfrentamientoSeguro(new Enfrentamiento(ganadoresLB.get(0), new ParticipanteVacio(), this.rondaActual, "Perdedores"));
                    }
                    break;
            }
        }

        asignarHorariosAutomaticos();
        notificar();
    }

    /**
     * Determina con precisión matemática qué etapa de la fase final corresponde jugar.
     */

    private String determinarEtapaFinalDoble() {
        // Buscamos un enfrentamiento de Ganadores que haya sido único en su ronda (la final de WB)
        Enfrentamiento finalGanadores = enfrentamientos.stream()
                .filter(e -> e.getLlave().equalsIgnoreCase("Ganadores"))
                .filter(e -> {
                    // Cuenta cuántos partidos de ganadores hubo en la misma ronda de ese partido
                    long partidosEnEsaRonda = enfrentamientos.stream()
                            .filter(x -> x.getRonda() == e.getRonda() && x.getLlave().equalsIgnoreCase("Ganadores"))
                            .count();
                    return partidosEnEsaRonda == 1; // La final de ganadores es el único partido de su ronda
                })
                .findFirst().orElse(null);

        if (finalGanadores == null || !finalGanadores.isJugado()) {
            return "RondaNormalLB";
        }

        Participante perdedorFinalGanadores = (finalGanadores.getGanador() == finalGanadores.getParticipante1())
                ? finalGanadores.getParticipante2()
                : finalGanadores.getParticipante1();

        boolean yaBajoAPerdedores = enfrentamientos.stream()
                .anyMatch(e -> e.getLlave().equalsIgnoreCase("Perdedores") &&
                        (e.getParticipante1() == perdedorFinalGanadores || e.getParticipante2() == perdedorFinalGanadores));

        long partidosLBRondaAnterior = enfrentamientos.stream()
                .filter(e -> e.getRonda() == (this.rondaActual - 1) && e.getLlave().equalsIgnoreCase("Perdedores"))
                .count();

        if (!yaBajoAPerdedores) {
            if (partidosLBRondaAnterior == 2) {
                return "SemifinalLB";
            }
            return "FinalLB";
        }

        return "GranFinal";
    }

    /**
     * Asigna horarios dinamicos y reparte los partidos en 3 canchas/recintos diferentes.
     * Añade la hora que pida el usuario de diferencia entre cada bloque de partidos.
     */
    private void asignarHorariosAutomaticos() {
        int horaBase = this.horaInicio;
        int minutoBase = this.minutoInicio;
        int maxCanchas = this.maxCanchas;
        int contadorCancha = 1;

        for (Enfrentamiento e : enfrentamientos) {
            //solo asignamos horario a los de la ronda actual (Pase auto)
            if (e.getRonda() == this.rondaActual &&
                    !(e.getParticipante1() instanceof ParticipanteVacio) &&
                    !(e.getParticipante2() instanceof ParticipanteVacio)) {

                //formateamos la hora para que se vea bien
                String horaTexto = String.format("%02d:%02d HRS", horaBase, minutoBase);
                e.setHora(horaTexto);
                e.setRecinto("Cancha " + contadorCancha);

                contadorCancha++;

                //si ya ocupamos las 3 canchas en este bloque horario, avanzamos el reloj
                //si ya ocupamos las canchas en este bloque, avanzamos el reloj de nuevo
                if (contadorCancha > maxCanchas) {
                    contadorCancha = 1;
                    minutoBase += this.intervaloMinutos;

                    //ussamos un while por si el partido dura 120 mins o mas
                    while (minutoBase >= 60) {
                        minutoBase -= 60;
                        horaBase += 1;
                    }

                    //evitamos que den las "25:00 hrs"
                    if (horaBase >= 24) {
                        horaBase -= 24;
                    }
                }
            }
        }
    }
    //Getters

    public String getNombre() { return nombre; }
    public Disciplina getDisciplina() { return disciplina; }
    public FormatoTorneo getFormato() { return formato; }
    public List<Participante> getInscritos() { return Inscritos; }
    public List<Enfrentamiento> getEnfrentamientos() { return enfrentamientos; }
}