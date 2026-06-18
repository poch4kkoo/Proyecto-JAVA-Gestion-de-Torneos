package org.logica;

public abstract class Participante {
    private String id;
    private String nombre;
    private String contacto;

    public Participante(String id, String nombre, String contacto) {
        this.id = id;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getContacto() { return contacto; }

    public abstract String getTipo();

    @Override
    public String toString() {
        return nombre + " (" + getTipo() + ")";
    }
}
