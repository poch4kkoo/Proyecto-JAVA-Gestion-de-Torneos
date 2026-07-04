package org.gui;

import org.logica.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelCalendario extends JPanel implements org.logica.Observer {

    private JPanel panelContenedor;

    public PanelCalendario() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Nos suscribimos para escuchar cuando se genere o actualice el torneo
        GestorTorneo.getInstancia().registrarObserver(this);

        // Panel para ver la lista de partidos
        panelContenedor = new JPanel();
        panelContenedor.setLayout(new BoxLayout(panelContenedor, BoxLayout.Y_AXIS));
        panelContenedor.setBackground(Color.WHITE);

        // Hacemos que tenga scroll por si son muchos dias
        JScrollPane scroll = new JScrollPane(panelContenedor);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
    }

    @Override
    public void actualizar() {
        panelContenedor.revalidate();
        panelContenedor.repaint();
    }


}
