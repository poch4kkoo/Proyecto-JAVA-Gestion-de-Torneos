package org.gui;

import org.logica.*;
import javax.swing.*;
import java.awt.*;

public class DialogoEditarEnfrentamiento extends JDialog {

    public DialogoEditarEnfrentamiento(Frame owner, Enfrentamiento enfrentamientoActual) {
        super(owner, "Editar Enfrentamiento", true);
        setSize(400, 240);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel panelCentral = new JPanel(new GridLayout(4, 1, 5, 5));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Se muestran los participnates del partido a intercambiar
        panelCentral.add(new JLabel("Se cambiará a:"));
        JComboBox<Participante> comboOrigen = new JComboBox<>();

        comboOrigen.addItem(enfrentamientoActual.getParticipante1());
        comboOrigen.addItem(enfrentamientoActual.getParticipante2());
        panelCentral.add(comboOrigen);

        // Se muestran todos los participantes para intercambiar
        panelCentral.add(new JLabel("por:"));
        JComboBox<Participante> comboDestino = new JComboBox<>();

        for (Enfrentamiento e : GestorTorneo.getInstancia().getEnfrentamientos()) {
            if (e.getRonda() == 1) {
                comboDestino.addItem(e.getParticipante1());
                comboDestino.addItem(e.getParticipante2());
            }
        }
        panelCentral.add(comboDestino);

        add(panelCentral, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Intercambiar");

        btnCancelar.addActionListener(e -> dispose());

        btnGuardar.addActionListener(e -> {
            Participante p1 = (Participante) comboOrigen.getSelectedItem();
            Participante p2 = (Participante) comboDestino.getSelectedItem();

            if (p1 != null && p2 != null && p1 != p2) {
                // Ejecutamos el swap que hicimos en GestorTorneo
                GestorTorneo.getInstancia().intercambiarParticipantes(p1, p2);
            }
            dispose();
        });

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);
    }
}

