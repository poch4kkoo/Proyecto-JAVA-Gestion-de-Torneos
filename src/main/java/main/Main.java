package main;
import org.gui.VentanaRegistro; //

public class Main {
    public static void main(String[] args) {
        //ejecuta la interfaz grafica
        javax.swing.SwingUtilities.invokeLater(() -> {
            new VentanaRegistro().setVisible(true);
        });
    }
}