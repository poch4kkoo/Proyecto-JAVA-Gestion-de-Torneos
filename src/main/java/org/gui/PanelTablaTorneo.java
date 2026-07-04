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
        btnSiguienteRonda.setEnabled(esEliminatoria && !gestor.getEnfrentamientos().isEmpty() && gestor.rondaActualTerminada());

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


        String[] columnas = {"#", "Logo", "Participante", "PJ", "G", "E", "P", "Pts"};
        DefaultTableModel modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }

            // dibuja el icono en la columna 1
            @Override public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1) return ImageIcon.class;
                return super.getColumnClass(columnIndex);
            }
        };

        for (int[] fila : calcularPosiciones(gestor)) {
            Participante p = gestor.getInscritos().get(fila[0]);
            if (p instanceof ParticipanteVacio) continue;

            // Obtenemos el ícono escalado
            ImageIcon icono = obtenerIconoEscalado(p.getRutaAvatar(), 22, 22);

            modeloTabla.addRow(new Object[]{
                    modeloTabla.getRowCount() + 1,
                    icono,
                    p.getNombre(),
                    fila[1], fila[2], fila[3], fila[4], fila[5]
            });
        }

        JTable tabla = new JTable(modeloTabla);
        tabla.setFillsViewportHeight(true);
        tabla.setRowHeight(28);
        tabla.getColumnModel().getColumn(0).setMaxWidth(30);
        tabla.getColumnModel().getColumn(1).setMaxWidth(45); // Columna del Logo

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setPreferredSize(new Dimension(500, Math.min(150, (modeloTabla.getRowCount() + 1) * 29)));

        // Ubicacion y tamaño Tabla liga
        scrollTabla.setMaximumSize(new Dimension(500, 200));
        scrollTabla.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelArbol.add(scrollTabla);

        JLabel lblPartidos = new JLabel(" Partidos");
        lblPartidos.setFont(new Font("Arial", Font.BOLD, 13));
        lblPartidos.setBorder(BorderFactory.createEmptyBorder(12, 10, 4, 10));
        lblPartidos.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelArbol.add(lblPartidos);

        for (Enfrentamiento enf : gestor.getEnfrentamientos()) {
            JPanel filaPartido = crearFilaEnfrentamiento(enf);
            filaPartido.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelArbol.add(filaPartido);
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
                stats[i1][2]++; stats[i2][4]++;
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
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        fila.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        boolean esPaseAutomatico = (enf.getParticipante1() instanceof ParticipanteVacio || enf.getParticipante2() instanceof ParticipanteVacio);
        boolean esEliminatoria = GestorTorneo.getInstancia().getFormato() != FormatoTorneo.LIGA_SIMPLE;

        // NUEVA REGLA: Saber si el partido está atascado en un empate
        boolean necesitaDesempate = enf.isJugado() && enf.getGanador() == null && esEliminatoria;

        // Panel contenedor de texto e iconos
        JPanel panelPartido = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelPartido.setOpaque(false);

        // Participante 1
        JLabel lblP1 = new JLabel(enf.getParticipante1().getNombre());
        if (!(enf.getParticipante1() instanceof ParticipanteVacio)) {
            lblP1.setIcon(obtenerIconoEscalado(enf.getParticipante1().getRutaAvatar(), 24, 24));
        }

        // Participante 2
        JLabel lblP2 = new JLabel(enf.getParticipante2().getNombre());
        lblP2.setHorizontalTextPosition(SwingConstants.LEFT); // El nombre a la izquierda, el ícono a la derecha
        if (!(enf.getParticipante2() instanceof ParticipanteVacio)) {
            lblP2.setIcon(obtenerIconoEscalado(enf.getParticipante2().getRutaAvatar(), 24, 24));
        }

        //Marcador
        String textoCentro = " vs ";
        if (enf.isJugado()) {
            textoCentro = " (" + enf.getPuntaje1() + ") - (" + enf.getPuntaje2() + ") ";
        } else if (esPaseAutomatico) {
            textoCentro = " (Pase automático) ";
        }
        JLabel lblCentro = new JLabel(textoCentro);
        lblCentro.setFont(new Font("Arial", Font.BOLD, 13));

        // Juntar todo en el panel
        panelPartido.add(lblP1);
        panelPartido.add(lblCentro);
        panelPartido.add(lblP2);

        // Botones para editar enfrentamientos
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelBotones.setOpaque(false);

        // Botón "Editar": Solo aparece si es eliminatoria, es la Ronda 1 y no se ha jugado aún
        if (esEliminatoria && enf.getRonda() == 1 && !enf.isJugado()) {
            JButton btnEditar = new JButton("Editar");
            btnEditar.setFont(new Font("Arial", Font.PLAIN, 11));
            btnEditar.addActionListener(e -> {
                Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
                Frame frame = (ventanaPadre instanceof Frame) ? (Frame) ventanaPadre : null;
                new DialogoEditarEnfrentamiento(frame, enf).setVisible(true);
            });
            panelBotones.add(btnEditar);
        }

        // Botón "Registrar" o "Desempatar"
        // Aparece si NO se ha jugado, O si se jugó pero necesita desempate
        if ((!enf.isJugado() || necesitaDesempate) && !esPaseAutomatico) {
            JButton btnRegistrar = new JButton(necesitaDesempate ? "Desempatar" : "Registrar");
            btnRegistrar.setFont(new Font("Arial", Font.BOLD, 11));

            // Si es para desempatar, lo ponemos en texto rojo para llamar la atención
            if (necesitaDesempate) {
                btnRegistrar.setForeground(Color.RED);
            }

            btnRegistrar.addActionListener(e -> {
                Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
                Frame frame = (ventanaPadre instanceof Frame) ? (Frame) ventanaPadre : null;
                new DialogoRegistrarResultados(frame, enf).setVisible(true);
            });
            panelBotones.add(btnRegistrar);
        }

        // Aspecto visual de las filas (Colores de fondo)
        if (esPaseAutomatico) {
            fila.setBackground(new Color(230, 230, 230)); // Gris
            lblP1.setForeground(Color.GRAY);
            lblP2.setForeground(Color.GRAY);
            lblCentro.setForeground(Color.GRAY);
        } else if (necesitaDesempate) {
            fila.setBackground(new Color(255, 200, 200)); // Rojo suave (Alerta de empate)
        } else if (enf.isJugado()) {
            fila.setBackground(new Color(220, 245, 220)); // Verde (Completado)
        } else {
            fila.setBackground(new Color(255, 250, 220)); // Amarillo (Pendiente)
        }

        fila.add(panelPartido, BorderLayout.CENTER);
        if (panelBotones.getComponentCount() > 0) {
            fila.add(panelBotones, BorderLayout.EAST);
        }

        fila.setOpaque(true);
        return fila;
    }

    // Metodo para cargar imagenes
    private ImageIcon obtenerIconoEscalado(String ruta, int width, int height) {
        if (ruta == null || ruta.trim().isEmpty()) {
            return null;
        }
        try {
            ImageIcon iconoOriginal = new ImageIcon(ruta);
            // Validar que la imagen realmente existe
            if (iconoOriginal.getIconWidth() == -1) {
                return null;
            }
            Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(imagenEscalada);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar: " + ruta);
            return null;
        }
    }
}