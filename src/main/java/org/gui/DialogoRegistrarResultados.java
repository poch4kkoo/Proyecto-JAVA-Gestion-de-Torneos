package org.gui;

import org.logica.*;
import javax.swing.*;
import java.awt.*;

public class DialogoRegistrarResultados extends JDialog {

    private Enfrentamiento enfrentamiento;
    private JSpinner spinner1;
    private JSpinner spinner2;

    public DialogoRegistrarResultados(Frame owner, Enfrentamiento enfrentamiento) {
        super(owner, "Registrar Resultado", true);
        this.enfrentamiento = enfrentamiento;

        setSize(380,200);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10,10));

        JLabel titulo = new JLabel(enfrentamiento.getParticipante1().getNombre() + " vs " + enfrentamiento.getParticipante2().getNombre(), SwingConstants.CENTER);

        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
        add(titulo, BorderLayout.NORTH);

        JPanel panelPuntajes = new JPanel(new GridLayout(2, 3, 10, 10));
        panelPuntajes.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));

        Disciplina disciplina = GestorTorneo.getInstancia().getDisciplina();

        double aumento = (disciplina == Disciplina.AJEDREZ) ? 0.5 : 1.0;

        spinner1 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, aumento));
        spinner2 = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, aumento));

        panelPuntajes.add(new JLabel(enfrentamiento.getParticipante1().getNombre() + ":", SwingConstants.RIGHT));
        panelPuntajes.add(spinner1);
        panelPuntajes.add(new JLabel("puntos"));

        panelPuntajes.add(new JLabel(enfrentamiento.getParticipante2().getNombre() + ":", SwingConstants.RIGHT));
        panelPuntajes.add(spinner2);
        panelPuntajes.add(new JLabel("puntos"));

        add(panelPuntajes, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Guardar resultado");

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());

        btnGuardar.addActionListener(e -> {

            float p1 = ((Double) spinner1.getValue()).floatValue();
            float p2 = ((Double) spinner2.getValue()).floatValue();

            try {
                enfrentamiento.registrarResultado(p1, p2);
                dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,"Marcador invalido para la disciplina actual\n" + ex.getMessage(),"Error de validacion", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
