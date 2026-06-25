package org.gui;

import org.logica.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PanelTablaTorneo extends JPanel implements Observer {

    private JButton btnGenerar;
    private JPanel panelArbol;

    public PanelTablaTorneo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GestorTorneo.getInstancia().registrarObserver(this);

        // Barra superior
        JPanel barraHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerar = new JButton("Generar Tabla");
        barraHerramientas.add(btnGenerar);
        add(barraHerramientas, BorderLayout.NORTH);

        // Panel central con scroll
        panelArbol = new JPanel();
        panelArbol.setLayout(new BoxLayout(panelArbol, BoxLayout.Y_AXIS));
        panelArbol.setBackground(Color.WHITE);

        add(new JScrollPane(panelArbol), BorderLayout.CENTER);

        // Evento para arrancar el torneo
        btnGenerar.addActionListener((ActionEvent e) -> {
            GestorTorneo.getInstancia().generarTorneo();
            GestorTorneo.getInstancia().notificar();
        });
    }

    @Override
    public void actualizar() {

        panelArbol.removeAll();

        GestorTorneo gestor = GestorTorneo.getInstancia();

        // Dibuja la tabla con los enfrentamientos
        if (!gestor.getEnfrentamientos().isEmpty()) {
            for (Enfrentamiento enf : gestor.getEnfrentamientos()) {
                // etiquetas simples por el momento
                JLabel etiquetaPartido = new JLabel(enf.toString());
                etiquetaPartido.setFont(new Font("Arial", Font.PLAIN, 14));
                etiquetaPartido.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                panelArbol.add(etiquetaPartido);
            }
        } else {
            // Mensaje por si no se han registrado participantes
            panelArbol.add(new JLabel("Añade a todos los participantes y apreta (Generar Tabla)"));
        }

        panelArbol.revalidate();
        panelArbol.repaint();
    }
}