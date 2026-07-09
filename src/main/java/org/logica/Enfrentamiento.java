package org.logica;

/**
 * Clase que representa un partido o confrontacion especifica dentro del torneo.
 */
public class Enfrentamiento {

    private Participante participante1;
    private Participante participante2;
    private float puntaje1;
    private float puntaje2;
    private boolean jugado;
    private int ronda;
    private String fecha;
    private String hora;
    private String llave;
    private String recinto;

    /**
     * Constructor por defecto para registrar un enfrentamiento simple.
     * Inicializa el partido asignandolo por defecto a la Ronda 1 de la llave de "Ganadores".
     * @param participante1 El primer participante.
     * @param participante2 El segundo participante.
     */
    public Enfrentamiento(Participante participante1, Participante participante2) {
        this(participante1, participante2, 1, "Ganadores");
    }

    /**
     * Constructor secundario que permite especificar el número de ronda.
     * Inicializa el partido asignandolo por defecto a la llave de "Ganadores".
     * @param participante1 El primer participante.
     * @param participante2 El segundo participante.
     * @param ronda Numero de ronda correspondiento.
     */
    public Enfrentamiento(Participante participante1, Participante participante2, int ronda) {
        this(participante1, participante2, ronda, "Ganadores");
    }

    /**
     * Constructor principal y detallado del enfrentamiento.
     * Inicializa todas las propiedades logisticas, establece los marcadores iniciales en cero
     * y marca el enfrentamiento en estado pendiente.
     * @param participante1 El primer participante.
     * @param participante2 El segundo participante.
     * @param ronda Numero de la ronda correspondiente.
     * @param llave Nombre o etiqueta de la llave.
     */
    public Enfrentamiento(Participante participante1, Participante participante2, int ronda, String llave) {
        this.participante1 = participante1;
        this.participante2 = participante2;
        this.ronda = ronda;
        this.llave = llave;
        this.puntaje1 = 0;
        this.puntaje2 = 0;
        this.jugado = false;
        this.fecha = "JORNADA " + ronda;
        this.hora = "Por definir ";
        this.recinto = "Por definir"; //nuevo
    }

    /**
     * Registra formalmente el marcador del encuentro. validando que los puntajes cumplan con las reglas
     * de las disciplina del torneo antes guardarlos, cambia el estado a jugado y despacha la
     * notificacion a los observadores.
     *
     * @param puntaje1 Marcador obtenido por el participante 1.
     * @param puntaje2 Marcador obtenido por el participante 2.
     * @throws IllegalArgumentException Si la combinacion de puntajes es ilegal segun la disciplina seleccionada.
     */
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

    /**
     * Indica si el enfrentamiento ya concluyo y posee un resultado registrado.
     * @return True si ya se jugo, False si esta pendiente.
     */
    public boolean isJugado() {
        return jugado;
    }

    /**
     * Metodo auxiliar privado para formatear visualmente el puntaje del participante 2.
     * @return El puntaje en formato String si ya se jugo, o un guion si esta pendiente.
     */
    private String puntajeResultados() {
        return jugado ? String.valueOf(puntaje2) : "-";
    }

    //SETTERS:

    /**
     * Establece la fecha programada para el partido.
     * @param fecha String descriptivo de la fecha.
     */
    public void setFecha(String fecha) { this.fecha = fecha; }

    /**
     * Establece la hora programada para el inicio del partido.
     * @param hora String que representa la hora de inicio.
     */
    public void setHora(String hora) { this.hora = hora; }

    /**
     * Asigna la cancha o recinto especifico donde se llevara a cabo el partido.
     * @param recinto Nombre de la cancha o escenario deportivo.
     */
    public void setRecinto(String recinto) { this.recinto = recinto; }

    /**
     * Permite modificar/establecer el participante1.
     * @param p El nuevo objeto de tipo Participante.
     */
    public void setParticipante1(Participante p) { this.participante1 = p; }

    /**
     * Permite modificar/establecer el participante2.
     * @param p El nuevo objeto de tipo Participante.
     */
    public void setParticipante2(Participante p) { this.participante2 = p; }


    //GETTERS:

    /**
     * Evalua el estado actual del partido y los  puntajes para determianr el ganadoor del encuentro.
     * En el caso de que uno de los participantes sea un participante vacio, de manera automatica se declara
     * gamnador al participante real.
     *
     * @return El objeto Participante ganador, o null si el partido esat pendiente o termino en empate.
     */
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

    /**
     * Obtiene el participante 1 registrado en el encuentro.
     * @return Objeto participante.
     */
    public Participante getParticipante1() {
        return participante1;
    }

    /**
     * Obtiene el participante 2 registrado en el encuentro.
     * @return Objeto participante.
     */
    public Participante getParticipante2() {
        return participante2;
    }

    /**
     * Obtiene el puntaje del participante 1.
     * @return Valor flotante del puntaje.
     */
    public float getPuntaje1() {
        return puntaje1;
    }

    /**
     * Obtiene el puntaje del participante 2.
     * @return Valor flotante del puntaje.
     */
    public float getPuntaje2() {
        return puntaje2;
    }

    /**
     * Obtiene el número de ronda en el que está indexado este partido.
     * @return Entero con el número de ronda.
     */
    public int getRonda() { return ronda; }

    /**
     * Obtiene la llave o el sector de clasificación del torneo al que pertenece el partido.
     * @return Cadena de texto ("Ganadores" o "Perdedores").
     */
    public String getLlave() { return llave; }

    /**
     * Obtiene la fecha asignada.
     * @return Texto de la fecha.
     */
    public String getFecha() { return fecha; }

    /**
     * Obtienen la hora programada para el inicio del encuentro.
     * @return Cadena de texto con la hora.
     */
    public String getHora() { return hora; }

    /**
     * Obtiene el recinto o cancha asignada para el partido.
     * @return Nombre del recinto.
     */
    public String getRecinto() { return recinto; }

    /**
     * Genera una representación textual formateada del enfrentamiento.
     * @return Cadena con el formato "Nombre1 (Puntaje1) vs (Puntaje2) Nombre2" o "Nombre1 vs Nombre2 (Pendiente)".
     */
    @Override
    public String toString() {
        if (jugado) {
            return participante1.getNombre() + " (" + puntaje1 + ") vs (" + puntajeResultados() + ") " + participante2.getNombre();
        }
        return participante1.getNombre() + " vs " + participante2.getNombre() + " (Pendiente)";
    }

}
