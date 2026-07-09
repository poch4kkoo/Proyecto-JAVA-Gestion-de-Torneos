package org.logica;

import java.util.ArrayList;
import java.util.List;


/**
 * Clase principal encargada de gestionar la logica de un torneo.
 * Hace uso del Patron Singleton para garantizar una unica instancia centralizada, ademas actua
 * como el sujeto en el Patron Observer para notificar cambios a la interfaz grafica.
 *
 * Administra inscripciones, intercambia participantes en primera ronda, programa horarios
 * automáticamente en canchas y genera llaves para formatos de Liga Simple, Eliminatoria Directa
 * y Eliminatoria Doble.
 */
public class GestorTorneo {

    // Instancia unica globlal del gestor (patron singleton)
    private static GestorTorneo instanciaUnica;

    //Atributos de configuracion del torneo.
    private String nombre;
    private Disciplina disciplina;
    private FormatoTorneo formato;

    //Listas de datos
    private List<Participante> Inscritos;
    private List<Enfrentamiento> enfrentamientos;
    private List<Observer> observadores;

    //Variables de control de estado y de cronograma
    private int rondaActual;
    private String mensajeEstado; // reemplaza System.out.println para notificar a la GUI
    private int horaInicio = 9;
    private int minutoInicio = 0;
    private int maxCanchas = 3;
    private int intervaloMinutos = 60;

    /**
     * Constructor privado para restringir la instanciacion externa, asi cumpliendo
     * con el patron singleton. Inicializa las listas de datos.
     */
    private GestorTorneo() {

        this.Inscritos = new ArrayList<>();
        this.enfrentamientos = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    /**
     * Obtienen la instancia unica de la clase GestorTorneo.
     * Si esta no existe, la crea.
     * @return Una Instancia unica de GestorTorneo.
     */
    public static GestorTorneo getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new GestorTorneo();
        }
        return instanciaUnica;
    }


    /**
     * Registra un nuevo observador patra recibir notificaciones de actualizacion.
     * @param o Objeto que implementa Observer.
     */
    public void registrarObserver(Observer o) {
        observadores.add(o);
    }

    /**
     * Elimina un observador de la lista de notificaciones.
     * @param o Objeto a remover.
     */
    public void eliminarObserver(Observer o) {
        observadores.remove(o);
    }

    /**
     * Notifica a todos los observadores registrados ejecuutando sus metodos de actualizacion.
     * Se invoca ante cualquier cambio de estado critico en la logica.
     */
    public void notificar() {
        for (Observer o : observadores) {
            o.actualizar();
        }
    }

    /**
     * Configura los parametros generales y logisticos del torneo.
     *
     * @param nombre Nombre del torneo.
     * @param disciplina Deporte o disciplina a competir.
     * @param formato Tipo de formato (Liga simple, Eliminatoria directa o doble).
     * @param horaInicio Hora base en la que se inician las actividades.
     * @param minutoInicio Minuto base en la que se inician las actividades.
     * @param maxCanchas Numero de cancha/tablas disponibles.
     * @param intervaloMinutos Duracion asignada por bloque de juego.
     */
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

    /**
     * Inscribe a un participante a la lista de competidores.
     * @param participante Objeto de tipo Participante que se inscribira al torneo.
     */
    public void inscribirParticipante(Participante participante) {
        this.Inscritos.add(participante);
        notificar();
    }

    /**
     * Elimina un participante de la lista de competidores inscritos.
     * @param participante Objeto de tipo Participante a eliminar.
     */
    public void eliminarParticipante(Participante participante) {
        this.Inscritos.remove(participante);
        notificar();
    }


    /**
     * Permite intercambiar de posición a dos participantes en los enfrentamientos iniciales (solo ronda 1).
     * @param p1 Primer participante a intercambiar.
     * @param p2 Segundo participante a intercambiar.
     */
    public void intercambiarParticipantes(Participante p1, Participante p2) {
        if (p1 == p2 || this.rondaActual != 1) return; // Protección

        Enfrentamiento enf1 = null, enf2 = null;
        boolean p1EsLocal = true, p2EsLocal = true;

        // Buscar enfrentamientos correspondientes de la primera ronda
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

    /**
     * Resgistra un enfrentamiento de manera segura en el historial.
     * Si detecta que uno de los dos competidores es un Participante vacio, le otorga de manera
     * automatica la victoria al participante real.
     * @param e Objeto de tipo Enfrentamiento que se va a evaluar y añadir.
     */
    private void registrarEnfrentamientoSeguro(Enfrentamiento e) {
        if (e.getParticipante1() instanceof ParticipanteVacio) {
            e.registrarResultado(0, 1); // Auto-gana el 2
        } else if (e.getParticipante2() instanceof ParticipanteVacio) {
            e.registrarResultado(1, 0); // Auto-gana el 1
        }
        this.enfrentamientos.add(e);
    }

    /**
     * Genera la programacion completa de la primera ronda del torneo calculando los emparejaminetos
     * basados en el formato deportivo configurado.
     * Maneja el rellenado dinamico con BYEs para formatos de eliminacion si el numero de inscritos
     * no corresponde a una potencia exacta de 2.
     */
    public void generarTorneo() {
        this.enfrentamientos.clear();
        this.rondaActual = 1;

        //LIGA SIMPLE: Todos contra todos
        if (formato == FormatoTorneo.LIGA_SIMPLE) {
            for (int i = 0; i < Inscritos.size(); i++) {
                for (int j = i + 1; j < Inscritos.size(); j++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(i), Inscritos.get(j)));
                }
            }
        }

        //ELIMINATORIA  DIRECTA: Formato de muerte subita.
        else if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {
            if (Inscritos.size() > 0) {
                int proximaPotencia = 1;
                while (proximaPotencia < Inscritos.size()) {
                    proximaPotencia *= 2;
                }

                int cantidadByes = proximaPotencia - Inscritos.size();
                int indiceActual = 0;

                //Se emparejan los primeros participantes con un espacio libre (avance directo).
                for (int i = 0; i < cantidadByes; i++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), new ParticipanteVacio()));
                    indiceActual++;
                }

                //Se empareja el resto de participantes en llaves normales de competencia.
                while (indiceActual < Inscritos.size()) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), Inscritos.get(indiceActual + 1)));
                    indiceActual += 2;
                }
            }
        }

        //ELIMINATORIA DOBLE: Formato con Cuadro de Ganadores y Perdedores
        else if (formato == FormatoTorneo.ELIMINATORIA_DOBLE) {
            if (Inscritos.size() > 0) {
                int proximaPotencia = 1;
                while (proximaPotencia < Inscritos.size()) {
                    proximaPotencia *= 2;
                }

                int cantidadByes = proximaPotencia - Inscritos.size();
                int indiceActual = 0;

                // Se generan los Byes iniciales dentro del Cuadro de Ganadores.
                for (int i = 0; i < cantidadByes; i++) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), new ParticipanteVacio(), 1, "Ganadores"));
                    indiceActual++;
                }

                // Se completan los partidos normales de la ronda de ganadores
                while (indiceActual < Inscritos.size()) {
                    enfrentamientos.add(new Enfrentamiento(Inscritos.get(indiceActual), Inscritos.get(indiceActual + 1), 1, "Ganadores"));
                    indiceActual += 2;
                }
            }
        }

        asignarHorariosAutomaticos();
        notificar();
    }

    /**
     * Valida si todos los enfrentamientos correspondientes a la ronda que está en curso
     * han concluido exitosamente y cuentan con un ganador definido.
     * Bloquea el paso si se detectan empates no resueltos en fases de eliminación directa/doble.
     *
     * @return True si la ronda se completo en su totalidad y False si quedan partidos pendientes o sin definicion.
     */
    public boolean rondaActualTerminada() {
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                if (!e.isJugado()) {

                    // Si el partido incluye un espacio vacío, no bloquea, pues avanza de forma automática
                    if (e.getParticipante1() instanceof ParticipanteVacio || e.getParticipante2() instanceof ParticipanteVacio) {
                        continue;
                    }
                    return false;  //Existen partidos pendientes
                }

                //Se prohíben empates en formatos de eliminación directa o doble.
                if (formato != FormatoTorneo.LIGA_SIMPLE && e.getGanador() == null) {
                    this.mensajeEstado = "⚠ Hay un partido empatado que requiere definición antes de continuar.";
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Avanza a la siguiente ronda del torneo recopilando los resultados parciales
     * de la ronda anterior y estructurando las nuevas llaves.
     */
    public void avanzarRonda() {

        // Separamos la logica segun el formato para evitar que eliminatoria directa
        // genere una llave de perdedores que no corresponde
        if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {
            avanzarRondaDirecta();
            return;
        }

        // Listas locales temporales de recolección para la ronda que acaba de cerrar
        List<Participante> ganadoresWB = new ArrayList<>();
        List<Participante> perdedoresWB = new ArrayList<>();
        List<Participante> ganadoresLB = new ArrayList<>();

        List<Enfrentamiento> wbAnterior = new ArrayList<>();
        List<Enfrentamiento> lbAnterior = new ArrayList<>();

        //Clasificacion analitica de los resultados de la ronda concluida.
        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                if (e.getLlave().equalsIgnoreCase("Ganadores")) {
                    wbAnterior.add(e);

                    if (e.isJugado() && e.getGanador() != null) {
                        ganadoresWB.add(e.getGanador());

                        //Determinacion del perdedor para descenderlo a el Cuadro de Perdedores.
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

        //Se valida que la final del Cuadro de Ganadores ya se haya disputado.
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

                // Si es la ronda inicial de perdedores, se cruzan directamente entre los eliminados de la WB
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

                // Rondas intermedias: sobrevivientes de LB vs los que acaban de caer de WB
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

        // CASO 2: la llave de ganadores se redujo a un solo partido
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

                    // Respaldo de seguridad para el procesamiento genérico de la Lower Bracket (Cuadro de Perdedores).
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
     * Logica exclusiva para avanzar rondas en Eliminatoria Directa.
     * Solo avanza los ganadores de la llave "Ganadores", sin crear
     * ninguna llave de perdedores (los eliminados quedan fuera).
     */
    private void avanzarRondaDirecta() {
        List<Participante> clasificados = new ArrayList<>();

        for (Enfrentamiento e : enfrentamientos) {
            if (e.getRonda() == this.rondaActual) {
                Participante ganador = e.getGanador();
                if (ganador != null && !(ganador instanceof ParticipanteVacio)) {
                    clasificados.add(ganador);
                }
            }
        }

        // Si queda 1 o menos clasificados el torneo termino
        if (clasificados.size() <= 1) {
            notificar();
            return;
        }

        this.rondaActual++;

        // Emparejamos a los clasificados en nuevos partidos
        for (int i = 0; i + 1 < clasificados.size(); i += 2) {
            enfrentamientos.add(new Enfrentamiento(
                    clasificados.get(i), clasificados.get(i + 1), this.rondaActual, "Ganadores"
            ));
        }

        // Si el numero de clasificados es impar (no deberia pasar si la generacion fue correcta)
        // el ultimo pasa con Bye
        if (clasificados.size() % 2 != 0) {
            registrarEnfrentamientoSeguro(new Enfrentamiento(
                    clasificados.get(clasificados.size() - 1), new ParticipanteVacio(), this.rondaActual, "Ganadores"
            ));
        }

        asignarHorariosAutomaticos();
        notificar();
    }

    /**
     * Calcula el lider de puntos en Liga Simple recorriendo todos los enfrentamientos.
     * Victoria = 3 puntos, Empate = 1 punto, Derrota = 0 puntos.
     * @return El Participante con mas puntos, o null si hay empate en el primer puesto.
     */
    private Participante calcularLiderLiga() {
        Participante lider = null;
        int maxPuntos = -1;

        for (Participante p : Inscritos) {
            if (p instanceof ParticipanteVacio) continue;
            int puntos = 0;

            for (Enfrentamiento enf : enfrentamientos) {
                if (!enf.isJugado()) continue;
                Participante ganador = enf.getGanador();

                if (ganador == p) {
                    puntos += 3; // victoria
                } else if (ganador == null) {
                    // empate: verificamos que el participante este en ese partido
                    if (enf.getParticipante1() == p || enf.getParticipante2() == p) {
                        puntos += 1;
                    }
                }
            }

            if (puntos > maxPuntos) {
                maxPuntos = puntos;
                lider = p;
            }
        }
        return lider;
    }

    /**
     * Analiza el árbol estructural completo del torneo para deducir matemáticamente
     + la etapa exacta que corresponde agendar en fases avanzadas de Eliminatoria Doble.
     * @return Cadena identificadora del estado del torneo ("SemifinalLB", "GranFinal", "RondaNormalLB", "FinalLB").
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

        // Control de estado: Evalúa si el finalista caído de arriba ya jugó previamente en el cuadro de perdedores
        boolean yaBajoAPerdedores = enfrentamientos.stream()
                .anyMatch(e -> e.getLlave().equalsIgnoreCase("Perdedores") &&
                        (e.getParticipante1() == perdedorFinalGanadores || e.getParticipante2() == perdedorFinalGanadores));

        long partidosLBRondaAnterior = enfrentamientos.stream()
                .filter(e -> e.getRonda() == (this.rondaActual - 1) && e.getLlave().equalsIgnoreCase("Perdedores"))
                .count();

        // Si aún no ha bajado a jugar, decide si corresponde limpiar el cuadro (Semifinal) o cruzarlo (FinalLB).
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
            //Asignacion de horario a los de la ronda actual (Pase automatico).
            if (e.getRonda() == this.rondaActual &&
                    !(e.getParticipante1() instanceof ParticipanteVacio) &&
                    !(e.getParticipante2() instanceof ParticipanteVacio)) {

                //Formateamos la hora para que se pueda visualizar mejor.
                String horaTexto = String.format("%02d:%02d HRS", horaBase, minutoBase);
                e.setHora(horaTexto);
                e.setRecinto("Cancha " + contadorCancha);

                contadorCancha++;

                // Si se supera la ocupación simultánea de canchas, se avanza al siguiente bloque horario
                if (contadorCancha > maxCanchas) {
                    contadorCancha = 1;
                    minutoBase += this.intervaloMinutos;

                    //Se ajusta los minutos y horas para seguir el sistema de 24 hrs (1 h = 60 mins)..
                    while (minutoBase >= 60) {
                        minutoBase -= 60;
                        horaBase += 1;
                    }

                    if (horaBase >= 24) {
                        horaBase -= 24;
                    }
                }
            }
        }
    }

    //GETTERS:

    /**
     * Obtiene el nombre asignado al torneo actual.
     * @return Un string con el nombre del torneo.
     */
    public String getNombre() { return nombre; }

    /**
     * Obtiene la disciplina o deporte en el que se compite en el torneo.
     * @return Objeto del tipo Disciplina.
     */
    public Disciplina getDisciplina() { return disciplina; }

    /**
     * Obtiene el formato de competencia del torneo.
     * @return Un objeto de tipo FormatoTorneo que rige el torneo.
     */
    public FormatoTorneo getFormato() { return formato; }

    /**
     * Obtiene la lista completa de todos los competidores inscritos en el torneo.
     * @return Una lista que contiene objetos de tipo Participantes
     */
    public List<Participante> getInscritos() { return Inscritos; }

    /**
     * Obtiene el historial completo de los enfrentamientos generados e instituidos en el torneo.
     * Incluye los partidos de todas las rondas y llaves (Ganadores, Perdedores y Gran Final).
     * @return Una Lista con todos los objetos de tipo Enfrentamiento.
     */
    public List<Enfrentamiento> getEnfrentamientos() { return enfrentamientos; }

    /**
     * Determina si el torneo ya tiene un ganador definitivo segun el formato.
     * - Eliminatoria directa: ganador del unico partido de la ultima ronda.
     * - Eliminatoria doble:   ganador de la Gran Final.
     * - Liga simple:          lider en puntos cuando todos los partidos estan jugados.
     *
     * @return El Participante ganador, o null si el torneo sigue en curso.
     */
    public Participante getGanadorTorneo() {
        if (enfrentamientos.isEmpty()) return null;

        if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {
            // Buscamos el unico partido de la ultima ronda de la llave Ganadores
            int ultimaRonda = enfrentamientos.stream()
                    .filter(e -> e.getLlave().equalsIgnoreCase("Ganadores"))
                    .mapToInt(Enfrentamiento::getRonda).max().orElse(0);
            List<Enfrentamiento> finalRonda = new ArrayList<>();
            for (Enfrentamiento e : enfrentamientos) {
                if (e.getRonda() == ultimaRonda && e.getLlave().equalsIgnoreCase("Ganadores")) {
                    finalRonda.add(e);
                }
            }
            if (finalRonda.size() == 1 && finalRonda.get(0).isJugado()) {
                return finalRonda.get(0).getGanador();
            }
        }

        if (formato == FormatoTorneo.ELIMINATORIA_DOBLE) {
            for (Enfrentamiento e : enfrentamientos) {
                if (e.getLlave().equalsIgnoreCase("Gran Final") && e.isJugado() && e.getGanador() != null) {
                    return e.getGanador();
                }
            }
        }

        if (formato == FormatoTorneo.LIGA_SIMPLE) {
            // Solo anunciamos lider cuando todos los partidos estan jugados
            for (Enfrentamiento e : enfrentamientos) {
                if (!e.isJugado()) return null;
            }
            return calcularLiderLiga();
        }

        return null;
    }

    /**
     * Retorna el ultimo mensaje de estado importante (ej: empate bloqueante)
     * y lo limpia para que no se muestre dos veces en la GUI.
     */
    public String consumirMensajeEstado() {
        String msg = this.mensajeEstado;
        this.mensajeEstado = null;
        return msg;
    }
}