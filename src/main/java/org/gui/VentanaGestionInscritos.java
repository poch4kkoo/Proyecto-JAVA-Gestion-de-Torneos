package org.gui;

import org.logica.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;


public class VentanaGestionInscritos extends JDialog {

    private DefaultListModel<Participante> modeloInscritos;
    private JList<Participante> listaInscritos;

    private JTextField txtContacto;
    private JButton btnGuardarContacto;
    private JButton btnEliminarParticipante;

    private JPanel panelMiembros;
    private DefaultListModel<String> modeloMiembros;
    private JList<String> listaMiembros;
    private JTextField txtNuevoMiembro;
    private JButton btnAgregarMiembro;
    private JButton btnQuitarMiembro;

    public VentanaGestionInscritos(Frame owner) {
        super(owner, "Gestionar Inscritos", true);
        setSize(550, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // Lista de inscritos (izquierda)
        modeloInscritos = new DefaultListModel<>();
        listaInscritos = new JList<>(modeloInscritos);
        listaInscritos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel panelLista = new JPanel(new BorderLayout());
        panelLista.setBorder(BorderFactory.createTitledBorder("Inscritos"));
        panelLista.add(new JScrollPane(listaInscritos), BorderLayout.CENTER);

        // Panel de detalle (derecha)
        JPanel panelDetalle = new JPanel();
        panelDetalle.setLayout(new BoxLayout(panelDetalle, BoxLayout.Y_AXIS));
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalle / Edición"));

        JPanel filaContacto = new JPanel(new BorderLayout(5, 5));
        filaContacto.add(new JLabel("Contacto:"), BorderLayout.WEST);
        txtContacto = new JTextField();
        filaContacto.add(txtContacto, BorderLayout.CENTER);
        btnGuardarContacto = new JButton("Guardar Contacto");
        filaContacto.add(btnGuardarContacto, BorderLayout.EAST);

        // Panel de miembros (solo se muestra para Equipo)
        panelMiembros = new JPanel(new BorderLayout(5, 5));
        panelMiembros.setBorder(BorderFactory.createTitledBorder("Miembros del Equipo"));
        modeloMiembros = new DefaultListModel<>();
        listaMiembros = new JList<>(modeloMiembros);
        panelMiembros.add(new JScrollPane(listaMiembros), BorderLayout.CENTER);

        JPanel filaMiembro = new JPanel(new BorderLayout(5, 5));
        txtNuevoMiembro = new JTextField();
        btnAgregarMiembro = new JButton("Agregar");
        btnQuitarMiembro = new JButton("Quitar Seleccionado");
        JPanel botonesMiembro = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesMiembro.add(btnAgregarMiembro);
        botonesMiembro.add(btnQuitarMiembro);
        filaMiembro.add(txtNuevoMiembro, BorderLayout.CENTER);
        filaMiembro.add(botonesMiembro, BorderLayout.EAST);
        panelMiembros.add(filaMiembro, BorderLayout.SOUTH);

        btnEliminarParticipante = new JButton("Cancelar Inscripción");
        JPanel panelEliminar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelEliminar.add(btnEliminarParticipante);

        panelDetalle.add(filaContacto);
        panelDetalle.add(Box.createVerticalStrut(10));
        panelDetalle.add(panelMiembros);
        panelDetalle.add(Box.createVerticalStrut(10));
        panelDetalle.add(panelEliminar);

        add(panelLista, BorderLayout.WEST);
        add(panelDetalle, BorderLayout.CENTER);

        // Eventos
        listaInscritos.addListSelectionListener(e -> mostrarDetalle());

        btnGuardarContacto.addActionListener((ActionEvent e) -> {
            Participante seleccionado = listaInscritos.getSelectedValue();
            if (seleccionado == null) return;

            String nuevoContacto = txtContacto.getText().trim();
            if (nuevoContacto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El contacto no puede estar vacío.");
                return;
            }
            seleccionado.setContacto(nuevoContacto);
            JOptionPane.showMessageDialog(this, "Contacto actualizado.");
            GestorTorneo.getInstancia().notificar();
        });

        btnAgregarMiembro.addActionListener((ActionEvent e) -> {
            Participante seleccionado = listaInscritos.getSelectedValue();
            if (!(seleccionado instanceof Equipo)) return;

            String nombreMiembro = txtNuevoMiembro.getText().trim();
            if (nombreMiembro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del miembro.");
                return;
            }
            ((Equipo) seleccionado).agregarMiembro(nombreMiembro);
            txtNuevoMiembro.setText("");
            mostrarDetalle();
        });

        btnQuitarMiembro.addActionListener((ActionEvent e) -> {
            Participante seleccionado = listaInscritos.getSelectedValue();
            String miembroSeleccionado = listaMiembros.getSelectedValue();
            if (!(seleccionado instanceof Equipo) || miembroSeleccionado == null) return;

            ((Equipo) seleccionado).removerMiembro(miembroSeleccionado);
            mostrarDetalle();
        });

        btnEliminarParticipante.addActionListener((ActionEvent e) -> {
            Participante seleccionado = listaInscritos.getSelectedValue();
            if (seleccionado == null) return;

            if (!GestorTorneo.getInstancia().getEnfrentamientos().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No se puede cancelar la inscripción: el torneo ya fue generado.",
                        "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirmacion = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que desea cancelar la inscripción de \"" + seleccionado.getNombre() + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (confirmacion == JOptionPane.YES_OPTION) {
                GestorTorneo.getInstancia().eliminarParticipante(seleccionado);
                cargarInscritos();
                limpiarDetalle();
            }
        });

        cargarInscritos();
    }

    private void cargarInscritos() {
        modeloInscritos.clear();
        List<Participante> inscritos = GestorTorneo.getInstancia().getInscritos();
        for (Participante p : inscritos) {
            modeloInscritos.addElement(p);
        }
    }

    private void mostrarDetalle() {
        Participante seleccionado = listaInscritos.getSelectedValue();
        if (seleccionado == null) {
            limpiarDetalle();
            return;
        }

        txtContacto.setText(seleccionado.getContacto());

        if (seleccionado instanceof Equipo) {
            panelMiembros.setVisible(true);
            modeloMiembros.clear();
            for (String miembro : ((Equipo) seleccionado).getNombresMiembros()) {
                modeloMiembros.addElement(miembro);
            }
        } else {
            panelMiembros.setVisible(false);
        }
    }

    private void limpiarDetalle() {
        txtContacto.setText("");
        modeloMiembros.clear();
        panelMiembros.setVisible(false);
    }
}