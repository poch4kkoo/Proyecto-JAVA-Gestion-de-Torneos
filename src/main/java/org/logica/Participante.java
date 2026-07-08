package org.logica;

/**
 * Clase abstracta que define la estructura y el comportamiento base de cualquier competidor
 * dentro del sistema de gestión de torneos.
 */
public abstract class Participante {

    private String id;
    private String nombre;
    private String contacto;
    private String rutaAvatar;

    /**
     * Construye un nuevo participante con sus datos de identificacion y de contacto basico.
     * De manera predeterminada, se le asigana un avatar inicial.
     *
     * @param id Identificador único o documento del participante.
     * @param nombre Nombre del participante.
     * @param contacto Informacion de contacto del participante.
     */
    public Participante(String id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.rutaAvatar = "avatar_0.png";
    }

    //GETTERS

    /**
     * Obtiene el identificador unico del participante.
     * @return Un String con el ID.
     */
    public String getId() { return id; }

    /**
     * Obtiene el nombre dek participante (individual o equipo).
     * @return El nombre registrado.
     */
    public String getNombre() { return nombre; }

    /**
     * Obtiene la informacion de contacto registrado.
     * @return Un String con la informacion de contacto.
     */
    public String getContacto() { return contacto; }

    /**
     * Metodo abstracto para obtener la clasificacion o naturaleza del participante (individual o equipo).
     * @return Un String que representa el tipo (Jugador o Equipo).
     */
    public abstract String getTipo();

    /**
     * Obtiene la ruta del archivo o recurso de imagen asignado como avatar del participante.
     * @return Nombre o ruta del archivo de imagen.
     */
    public String getRutaAvatar() { return rutaAvatar; }

    //SETTERS:

    /**
     * Modifica el nombre del participante.
     * @param nombre Nuevo nombre a asignar.
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Modifica la informacion de contacto del participante.
     * @param contacto Nueva informacion de contacto.
     */
    public void setContacto(String contacto) { this.contacto = contacto; }

    /**
     * Devuelve una representacion textual simplificada del participante.
     * @return Cadena con el formato "Nombre (Tipo)".
     */
    @Override
    public String toString() {
        return nombre + " (" + getTipo() + ")";
    }

    /**
     * Permite personalizar la apariencia visual del participante asignándole un avatar específico.
     * @param rutaAvatar Nombre o ruta del nuevo archivo de imagen (ej: "avatar_boxeador.png").
     */
    public void setRutaAvatar(String rutaAvatar) { this.rutaAvatar = rutaAvatar; }
}
