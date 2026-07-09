package org.logica;

/**
 * Enumeración que define las disciplinas deportivas soportadas por el sistema de gestión de torneos.
 */
public enum Disciplina {
    FUTBOL {
        @Override
        public boolean esValido(float p1, float p2) {
            return (p1 >= 0 && p2 >= 0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return " Goles:" + (int) p1 + " - " + (int) p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return tipo.equalsIgnoreCase("Equipo");
        }

        @Override
        public boolean tieneModalidadFija() { return true;}

        @Override
        public int getMinimoJugadores() { return 5; } // futbol 5 minimo
        @Override
        public int getMaximoJugadores() { return 22; } //si quieres suplentes
    }    ,
    AJEDREZ {
        @Override
        public boolean esValido(float p1, float p2) {
            boolean localValido = (p1 == 0.0f || p1 == 0.5f || p1 == 1.0f);
            boolean visitanteValido = (p2 == 0.0f || p2 == 0.5f || p2 == 1.0f);
            return localValido && visitanteValido && (p1 + p2 == 1.0f);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            if (p1==p2) return "Tablas (0.5 - 0.5)";
            //jugador 1 siempre blanco y jugador 2 siempre negro
            return p1 > p2 ? "Gana Blanco (" +p1+ " - "+p2+")"
                    : "Gana Negro (" +p1+" - "+p2+")";
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return tipo.equalsIgnoreCase("Individual");
        }

        @Override
        public boolean tieneModalidadFija() { return true;}
        @Override
        public int getMinimoJugadores() { return 1; }
        @Override
        public int getMaximoJugadores() { return 1; }
    },
    VIDEOJUEGOS {
        @Override
        public boolean esValido(float p1, float p2) {
            return p1!=p2 && p1>=0 && p2>=0 && (p1%1==0) && (p2%1==0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return (int)p1 + " - " + (int)p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return true;
        }

        @Override
        public boolean tieneModalidadFija() { return false;}
        @Override
        public int getMinimoJugadores() { return 1; }
        @Override
        public int getMaximoJugadores() { return 5; } //equipode Valo
    },
    TENIS {
        @Override
        public boolean esValido(float p1, float p2) {
            //el tenis se gana por sets (ej: 2-0, 2-1) no  empates
            return p1>=0 && p2>=0 && p1!=p2 && (p1%1==0) && (p2%1==0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return "Sets: " + (int)p1 + " - " + (int)p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return true;
        }

        @Override
        public boolean tieneModalidadFija() { return false;}
        @Override
        public int getMinimoJugadores() { return 1; }
        @Override
        public int getMaximoJugadores() { return 2; } //dobles
    },
    BASQUETBOL {
        @Override
        public boolean esValido(float p1, float p2) {
            //puntajes altos enteros, sin empates en el resultado final
            return p1>=0 && p2>=0 && p1!=p2 && (p1%1==0) && (p2%1==0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return "Puntos: " + (int)p1 + " - " + (int)p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return tipo.equalsIgnoreCase("Equipo");
        }

        @Override
        public boolean tieneModalidadFija() { return true;}
        @Override
        public int getMinimoJugadores() { return 5; }
        @Override
        public int getMaximoJugadores() { return 12; }
    },
    VOLEIBOL {
        @Override
        public boolean esValido(float p1, float p2) {
            //el voleibol se gana por sets (ej: 3-0, 3-1, 3-2). no  empates
            return p1>=0 && p2>=0 && p1!=p2 && (p1%1==0) && (p2%1==0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return "Sets: " + (int)p1 + " - " + (int)p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return tipo.equalsIgnoreCase("Equipo");
        }

        @Override
        public boolean tieneModalidadFija() { return true;}
        @Override
        public int getMinimoJugadores() { return 6; }
        @Override
        public int getMaximoJugadores() { return 12; }
    },
    BOXEO {
        @Override
        public boolean esValido(float p1, float p2) {
            //el boxeo se define por tarjetas de jueces. existen empates
            return p1>=0 && p2>=0 && (p1%1==0) && (p2%1==0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            if (p1==p2) return "Empate (Tarjetas: "+(int)p1 + " - " + (int)p2 + ")";
            return "Tarjetas: " + (int)p1 + " - " + (int)p2;
        }

        @Override
        public boolean tipoParticipantePermitido(String tipo) {
            return tipo.equalsIgnoreCase("Individual");
        }

        @Override
        public boolean tieneModalidadFija() { return true;}
        @Override
        public int getMinimoJugadores() { return 1; }
        @Override
        public int getMaximoJugadores() { return 1; }
    };

    /**
     *
     * @param p1 Puntaje del participante 1.
     * @param p2 Puntaje del participante 2.
     * @return True si cummple las reglas de la disciplina, False en el caso contrario.
     */
    public abstract boolean esValido(float p1, float p2);

    /**
     * Genera una cadena de texto estilizada y legible con la nomenclatura del deporte.
     * @param p1 Puntaje del participante 1.
     * @param p2 Puntaje del participante 2.
     * @return Cadena formateada con los puntajes.
     */
    public abstract String formatearPuntaje(float p1, float p2);

    /**
     * Restringe las solicitudes de inscripcion dictaminando si la disciplina acepta
     * a un participante del tipo "equipo" o "individual".
     * @param tipo Cadena de texto del Tipo de participante.
     * @return True si el "tipo" esta autorizado, False si no es compatible.
     */
    public abstract boolean tipoParticipantePermitido(String tipo);

    /**
     * Indica si la disciplina posee una categoria fija en cuanto al numero de competidores,
     * (solo equipos o solo individual).
     * @return True si la modalidad es fija y False si no lo es.
     */
    public abstract boolean tieneModalidadFija();

    /**
     * Obtiene el tope minimo de jugadores aceptados por equipo.
     * @return Numero entero con la cantidad minima de jugadores permitidos.
     */
    public abstract int getMinimoJugadores();

    /**
     * Obtiene el tope maximo de jugadores aceptados por equipo.
     * @return Numero entero con la cantidad maxima de jugadores permitidos.
     */
    public abstract int getMaximoJugadores();
}