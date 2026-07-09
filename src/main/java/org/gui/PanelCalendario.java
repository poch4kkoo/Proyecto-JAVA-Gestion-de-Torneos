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
        panelContenedor.removeAll();
        List<Enfrentamiento> partidos = GestorTorneo.getInstancia().getEnfrentamientos();

        if (partidos.isEmpty()) {
            JPanel pnlVacio = new JPanel(new FlowLayout(FlowLayout.CENTER));
            pnlVacio.setBackground(Color.WHITE);
            pnlVacio.add(new JLabel("Aún no hay enfrentamientos. Genera la tabla del torneo primero."));
            panelContenedor.add(pnlVacio);
        } else {
            // Agrupar los partidos segun su fecha
            Map<String, List<Enfrentamiento>> porFecha = new LinkedHashMap<>();
            for (Enfrentamiento enf : partidos) {
                // Filtramos los pases automáticos
                if (enf.getParticipante1() instanceof ParticipanteVacio || enf.getParticipante2() instanceof ParticipanteVacio) {
                    continue;
                }
                porFecha.computeIfAbsent(enf.getFecha(), k -> new ArrayList<>()).add(enf);
            }

            int contadorPartido = 1;

            // interfaz gráfica
            for (Map.Entry<String, List<Enfrentamiento>> entrada : porFecha.entrySet()) {

                String fechaActual = entrada.getKey();
                List<Enfrentamiento> partidosDelDia = entrada.getValue();

                // Barra del Día
                JPanel headerDia = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
                headerDia.setBackground(new Color(41, 128, 185)); // Azul
                headerDia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

                JLabel lblDia = new JLabel(fechaActual);
                lblDia.setForeground(Color.WHITE);
                lblDia.setFont(new Font("Arial", Font.BOLD, 13));
                headerDia.add(lblDia);

                // Boton de edicion fecha
                JButton btnEditarFecha = new JButton("Editar");
                btnEditarFecha.setFont(new Font("Arial", Font.PLAIN, 10));
                btnEditarFecha.setMargin(new Insets(2, 5, 2, 5));
                btnEditarFecha.setFocusPainted(false);
                btnEditarFecha.setToolTipText("Editar");

                btnEditarFecha.addActionListener(e -> {
                    String nuevaFecha = JOptionPane.showInputDialog(
                            this,
                            "Nombre de la Jornada (semifinal, liga 2026, etc):",
                            fechaActual
                    );

                    if (nuevaFecha != null && !nuevaFecha.trim().isEmpty()) {
                        String fechaFormateada = nuevaFecha.trim().toUpperCase();
                        for (Enfrentamiento enf : partidosDelDia) {
                            enf.setFecha(fechaFormateada);
                        }
                        actualizar(); // Volvemos a dibujar el calendario
                    }
                });

                headerDia.add(btnEditarFecha);
                panelContenedor.add(headerDia);

                // partidos de ese día
                for (Enfrentamiento enf : partidosDelDia) {
                    panelContenedor.add(crearFilaPartido(enf, contadorPartido));
                    contadorPartido++;
                }

                // Espacio antes del siguiente día
                panelContenedor.add(Box.createVerticalStrut(15));
            }
        }

        panelContenedor.revalidate();
        panelContenedor.repaint();
    }

    private JPanel crearFilaPartido(Enfrentamiento enf, int numero) {
        JPanel fila = new JPanel(new BorderLayout(5, 0));
        fila.setBackground(Color.WHITE);
        fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Llaves
        JPanel pnlIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlIzq.setOpaque(false);

        JLabel lblNum = new JLabel(" " + numero + " ", SwingConstants.CENTER);
        lblNum.setOpaque(true);
        lblNum.setBackground(new Color(39, 174, 96)); // Verde
        lblNum.setForeground(Color.WHITE);
        lblNum.setFont(new Font("Arial", Font.BOLD, 12));

        String textoLlave = enf.getLlave().toUpperCase();
        FormatoTorneo formato = GestorTorneo.getInstancia().getFormato();

        // Etiqueta para el Formato del torneo
        if (formato == FormatoTorneo.LIGA_SIMPLE) {
            textoLlave = "LIGA";
        } else if (formato == FormatoTorneo.ELIMINATORIA_DIRECTA) {
            textoLlave = "ELIMINATORIA";
        }

        JLabel lblInfo = new JLabel("| " + textoLlave + " |");
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 11));

        pnlIzq.add(lblNum);
        pnlIzq.add(lblInfo);

        // Banderas y Nombres
        JPanel pnlCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        pnlCentro.setOpaque(false);

        JLabel lblP1 = new JLabel(enf.getParticipante1().getNombre());
        lblP1.setIcon(obtenerIconoEscalado(enf.getParticipante1().getRutaAvatar(), 20, 20));
        lblP1.setHorizontalTextPosition(SwingConstants.LEFT); // Nombre a la izquierda de la bandera

        JLabel lblVs = new JLabel("VS");
        lblVs.setFont(new Font("Arial", Font.BOLD, 10));
        lblVs.setForeground(Color.GRAY);

        JLabel lblP2 = new JLabel(enf.getParticipante2().getNombre());
        lblP2.setIcon(obtenerIconoEscalado(enf.getParticipante2().getRutaAvatar(), 20, 20));

        pnlCentro.add(lblP1);
        pnlCentro.add(lblVs);
        pnlCentro.add(lblP2);

        //Hora del partido
        JPanel pnlDer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlDer.setOpaque(false);

        JLabel lblHora = new JLabel(enf.getHora());
        lblHora.setFont(new Font("Arial", Font.BOLD, 12));
        lblHora.setForeground(new Color(39, 174, 96));

        // boton para editar la hora
        JButton btnEditarHora = new JButton("DD/MM, HRS");
        btnEditarHora.setFont(new Font("Arial", Font.PLAIN, 10));
        btnEditarHora.setMargin(new Insets(2, 5, 2, 5));
        btnEditarHora.setFocusPainted(false);
        btnEditarHora.setToolTipText("Editar Fecha y hora");

        btnEditarHora.addActionListener(e -> {
            String nuevaHora = JOptionPane.showInputDialog(
                    this,
                    "Ingrese hora y fecha para este partido:",
                    enf.getHora()
            );

            // actualizamos la hora
            if (nuevaHora != null && !nuevaHora.trim().isEmpty()) {
                enf.setHora(nuevaHora.trim().toUpperCase());
                actualizar(); // Redibuja el calendario
            }
        });

        pnlDer.add(lblHora);
        pnlDer.add(btnEditarHora);

        // Ensamblaje
        fila.add(pnlIzq, BorderLayout.WEST);
        fila.add(pnlCentro, BorderLayout.CENTER);
        fila.add(pnlDer, BorderLayout.EAST);

        return fila;
    }

    private ImageIcon obtenerIconoEscalado(String ruta, int width, int height) {
        if (ruta == null || ruta.trim().isEmpty()) return null;
        try {
            ImageIcon iconoOriginal = new ImageIcon(ruta);
            if (iconoOriginal.getIconWidth() == -1) return null;
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            return null;
        }
    }
}