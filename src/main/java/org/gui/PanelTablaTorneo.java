package org.gui;

import org.logica.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

public class PanelTablaTorneo extends JPanel implements org.logica.Observer {

    private JButton btnGenerar;
    private JButton btnSiguienteRonda;
    private JPanel panelArbol;

    public PanelTablaTorneo() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GestorTorneo.getInstancia().registrarObserver(this);

        // Barra superior
        JPanel barraHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnGenerar = new JButton("Generar Tabla");
        btnSiguienteRonda = new JButton("Siguiente Ronda");
        btnSiguienteRonda.setEnabled(false);
        barraHerramientas.add(btnGenerar);
        barraHerramientas.add(btnSiguienteRonda);
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

        //avanza a la siguiente ronda cuando todos los partidos estan jugados
        btnSiguienteRonda.addActionListener((ActionEvent e) -> {
            GestorTorneo.getInstancia().avanzarRonda();
        });
    }

    @Override
    public void actualizar() {

        panelArbol.removeAll();

        GestorTorneo gestor = GestorTorneo.getInstancia();

        // Dibuja la tabla con los enfrentamientos
        if (!gestor.getEnfrentamientos().isEmpty()) {

            if (gestor.getFormato() == FormatoTorneo.LIGA_SIMPLE) {
                //liga simple muestra tabla de posiciones y partidos
                dibujarTablaLiga(gestor);
            } else {
                //elimiantorias agrupan enfrentamientos por ronda
                dibujarEliminatoria(gestor);
            }

        } else {
            // Mensaje por si no se han registrado participantes
            panelArbol.add(new JLabel("Añade a todos los participantes y apreta (Generar Tabla)"));
        }

        boolean esEliminatoria = gestor.getFormato() != FormatoTorneo.LIGA_SIMPLE;
        btnSiguienteRonda.setEnabled( esEliminatoria && !gestor.getEnfrentamientos().isEmpty() && gestor.rondaActualTerminada());

        panelArbol.revalidate();
        panelArbol.repaint();
    }

    //muestra enfrentamientos agrupados por ronda para eliminatorias
    private void dibujarEliminatoria(GestorTorneo gestor) {
        Map<Integer, List<Enfrentamiento>> porRonda = new LinkedHashMap<>();
        for (Enfrentamiento e : gestor.getEnfrentamientos()) {
            porRonda.computeIfAbsent(e.getRonda(), k -> new ArrayList<>()).add(e);
        }

        for (Map.Entry<Integer, List<Enfrentamiento>> entrada : porRonda.entrySet()) {
            JLabel lblRonda = new JLabel("  — Ronda " + entrada.getKey() + " —");
            lblRonda.setFont(new Font("Arial", Font.BOLD, 13));
            lblRonda.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
            panelArbol.add(lblRonda);

            for (Enfrentamiento enf : entrada.getValue()) {
                panelArbol.add(crearFilaEnfrentamiento(enf));
            }
        }
    }

    //tabla de posiciones y lista de partidos para liga simple
    private void dibujarTablaLiga(GestorTorneo gestor) {
        JLabel lblTabla = new JLabel("  Tabla de Posiciones");
        lblTabla.setFont(new Font("Arial", Font.BOLD, 13));
        lblTabla.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 10));
        panelArbol.add(lblTabla);

        String[] columnas = {"#", "Participante", "PJ", "G", "E", "P", "Pts"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false; }
        };

        for (int[] fila : calcularPosiciones(gestor)) {
            Participante p = gestor.getInscritos().get(fila[0]);
            if (p instanceof ParticipanteVacio) continue;
            modeloTabla.addRow(new Object[]{
                    modeloTabla.getRowCount() + 1,
                    p.getNombre(),
                    fila[1], fila[2], fila[3], fila[4], fila[5]
            });
        }

        JTable tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(22);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(500, Math.min(150, (modeloTabla.getRowCount() + 1) * 23)));
        scrollTabla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        scrollTabla.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelArbol.add(scrollTabla);

        JLabel lblPartidos = new JLabel("  Partidos");
        lblPartidos.setFont(new Font("Arial", Font.BOLD, 13));
        lblPartidos.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
        panelArbol.add(lblPartidos);

        for (Enfrentamiento enf : gestor.getEnfrentamientos()) {
            panelArbol.add(crearFilaEnfrentamiento(enf));
        }
    }

    // se calcula Partidos jugados, Ganador, Empates, Perdidos y Puntos por participante
    private List<int[]> calcularPosiciones(GestorTorneo gestor) {
        List<Participante> inscritos = gestor.getInscritos();
        int[][] stats = new int[inscritos.size()][6];
        for (int i = 0; i < inscritos.size(); i++) stats[i][0] = i;

        for (Enfrentamiento enf : gestor.getEnfrentamientos()) {
            if (!enf.isJugado()) continue;
            int i1 = inscritos.indexOf(enf.getParticipante1());
            int i2 = inscritos.indexOf(enf.getParticipante2());
            if (i1 < 0 || i2 < 0) continue;

            stats[i1][1]++; stats[i2][1]++;

            Participante ganador = enf.getGanador();
            if (ganador == null) {
                // Empate (1 punto cada uno)
                stats[i1][3]++; stats[i2][3]++;
                stats[i1][5]++; stats[i2][5]++;
            } else if (ganador == enf.getParticipante1()) {
                stats[i1][2]++; stats[i2][4]++; // G y P
                stats[i1][5] += 3;
            } else {
                stats[i2][2]++; stats[i1][4]++;
                stats[i2][5] += 3;
            }
        }

        List<int[]> lista = new ArrayList<>(Arrays.asList(stats));
        lista.sort((a, b) -> b[5] != a[5] ? b[5] - a[5] : b[2] - a[2]);
        return lista;
    }

    //fila clickeable para registrar resultado
    //si ya se jugo (verde), esta pendiente (amarillo), si se da un pase automatico (gris)
    private JPanel crearFilaEnfrentamiento(Enfrentamiento enf) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fila.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        boolean esPaseAutomatico = (enf.getParticipante1() instanceof ParticipanteVacio || enf.getParticipante2() instanceof ParticipanteVacio);

        // etiquetas simples por el momento
        JLabel etiquetaPartido = new JLabel(enf.toString());
        etiquetaPartido.setFont(new Font("Arial", Font.PLAIN, 14));
        etiquetaPartido.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (esPaseAutomatico) {
            fila.setBackground(new Color(230, 230, 230));
            etiquetaPartido.setForeground(Color.GRAY);
            fila.add(etiquetaPartido, BorderLayout.CENTER);
        } else if (enf.isJugado()) {
            fila.setBackground(new Color(220, 245, 220));
            fila.add(etiquetaPartido, BorderLayout.CENTER);
        } else {
            fila.setBackground(new Color(255, 250, 220));
            JButton btnRegistrar = new JButton("Registrar resultado");
            btnRegistrar.setFont(new Font("Arial", Font.PLAIN, 11));
            btnRegistrar.addActionListener(e -> {
                Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
                Frame frame = (ventanaPadre instanceof Frame) ? (Frame) ventanaPadre : null;
                new DialogoRegistrarResultados(frame, enf).setVisible(true);
            });
            fila.add(etiquetaPartido, BorderLayout.CENTER);
            fila.add(btnRegistrar, BorderLayout.EAST);
        }

        fila.setOpaque(true);
        return fila;
    }
}