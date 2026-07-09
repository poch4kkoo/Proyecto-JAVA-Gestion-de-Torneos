package org.logica;
/**
 * enumeracion que representa las distintas disciplinas deportivas o juegos
 * disponibles en el sistema de torneos. cada constante define sus propias
 * reglas de validacion de puntajes, formatos de impresion y restricciones
 * sobre los tipos y cantidades de participantes permitidos.
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
            //el tenis se gana por sets (ej: 2-0, 2-1) no empates
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
            //el voleibol se gana por sets (ej: 3-0, 3-1, 3-2). no empates
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
     * valida si el puntaje ingresado es correcto para la disciplina.
     *
     * @param p1 puntaje del participante 1.
     * @param p2 puntaje del participante 2.
     * @return true si el resultado es valido, false en caso contrario.
     */
    public abstract boolean esValido(float p1, float p2);
    /**
     * formatea el puntaje numerico a una cadena de texto legible para el usuario.
     *
     * @param p1 puntaje del participante 1.
     * @param p2 puntaje del participante 2.
     * @return el resultado formateado(ej:"sets: 3-0").
     */
    public abstract String formatearPuntaje(float p1, float p2);

    /**
     * determina que tipo de inscripcion o participante se permite inscribir.
     * mas que nada es para seguir la logica real de los torneos (no tiene sentido
     * un torneo de futbol con participantes individuales o una partida de ajedrez en equipos).
     *
     * @param tipo el tipo de participante ("equipo" o "individual").
     * @return true si el tipo es permitido en esta disciplina.
     */
    public abstract boolean tipoParticipantePermitido(String tipo);
    /**
     * indica si la disciplina solo admite un tipo de participante estricto.
     *
     * @return true si la modalidad es fija,false si es mixta o variable (como videojuegos).
     */
    public abstract boolean tieneModalidadFija();
    /**
     * obtiene la cantidad minima de jugadores permitidos por equipo.
     *
     * @return el numero minimo de integrantes.
     */
    public abstract int getMinimoJugadores();
    /**
     * obtiene la cantidad maxima de jugadores permitidos por equipo.
     *
     * @return el numero maximo de integrantes.
     */
    public abstract int getMaximoJugadores();
}