package org.logica;

public abstract class Participante {
    private String id;
    private String nombre;
    private String contacto;
    private String rutaAvatar;

    public Participante(String id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
        this.rutaAvatar = "avatar_0.png";
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }
    public abstract String getTipo();

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setContacto(String contacto) { this.contacto = contacto; }

    @Override
    public String toString() {
        return nombre + " (" + getTipo() + ")";
    }

    public String getRutaAvatar() { return rutaAvatar; }
    public void setRutaAvatar(String rutaAvatar) { this.rutaAvatar = rutaAvatar; }
}
