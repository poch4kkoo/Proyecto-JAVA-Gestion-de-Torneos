package org.logica;

/**
 * Interfaz que define el contrato básico para la implementación del patrón de diseño Observer.
 * Permite establecer un mecanismo de suscripción para que cualquier componente del sistema sea
 * notificado de manera automatica ante cambios en el torneo.
 */
public interface Observer {

    /**
     * Metodo de actualizacion que es invocado de manera sincrona por el sujeto observado (GestorTorneo).
     * cuando ocurre una modificacion critica en los datos.
     */
    void actualizar();
}
