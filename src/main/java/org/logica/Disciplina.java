package org.logica;

public enum Disciplina {
    FUTBOL {
        @Override
        public boolean esValido(float p1, float p2) {
            return (p1 >= 0 && p2 >= 0);
        }

        @Override
        public String formatearPuntaje(float p1, float p2) {
            return " Goles:" + (int)p1 + " - " + (int)p2;
        }
    },
    AJEDREZ {
        @Override
        public boolean esValido(float p1, float p2) {

            boolean localValido = (p1 == 0.0f || p1 == 0.5f || p1 == 1.0f);
            boolean visitanteValido = (p2 == 0.0f || p2 == 0.5f || p2 == 1.0f);

            return localValido && visitanteValido && (p1 + p2 == 1.0f);
        }
        @Override
        public String formatearPuntaje(float p1, float p2) {
            if (p1 == p2) return "Tablas (0.5 - 0.5)";

            //Jugador 1 siempre blanco y Jugador 2 siempre negro.
            return p1 > p2 ? "Gana Blanco (" + p1 + " - " + p2 + ")"
                    : "Gana Negro (" + p1 + " - " + p2 + ")";
        }
    },
    VIDEOJUEGOS {
        @Override
        public boolean esValido(float p1, float p2) {

            return p1 != p2 && p1 >= 0 && p2 >= 0 && (p1 % 1 == 0) && (p2 % 1 == 0);
        }
        @Override
        public String formatearPuntaje(float p1, float p2) {
            return (int)p1 + " - " + (int)p2;
        }
    };

    public abstract boolean esValido(float p1, float p2);
    public abstract String formatearPuntaje(float p1, float p2);
}
