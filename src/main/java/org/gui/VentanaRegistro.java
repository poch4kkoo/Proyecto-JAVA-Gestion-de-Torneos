package org.gui;
import org.logica.*; //importa el GestorTorneo, la Fabrica, las Disciplinas, etc.
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaRegistro extends JFrame implements Observer {

    private JComboBox<Disciplina> comboDisciplina;
    private JComboBox<FormatoTorneo> comboFormato;
    private JTextField txtNombreTorneo;
    private JButton btnConfigurar;

    private JComboBox<String> comboTipoParticipante;
    private JTextField txtNombreParticipante;
    private JButton btnInscribir;

    private DefaultListModel<String> modeloListaInscritos;
    private JList<String> listaInscritosVisual;

    public VentanaRegistro() {
        setTitle("Sistema de Gestión de Torneos - Registro");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //1 me suscribo como observador
        GestorTorneo.getInstancia().registrarObserver(this);

        //panel superior: configurar el torneo
        JPanel panelNorte=new JPanel(new GridLayout(2, 4, 5, 5));
        panelNorte.setBorder(BorderFactory.createTitledBorder("1. Definir Características del Torneo"));

        txtNombreTorneo=new JTextField();
        comboDisciplina=new JComboBox<>(Disciplina.values());
        comboFormato=new JComboBox<>(FormatoTorneo.values());
        btnConfigurar=new JButton("Crear Torneo");

        panelNorte.add(new JLabel("Nombre:"));
        panelNorte.add(txtNombreTorneo);
        panelNorte.add(new JLabel("Disciplina:"));
        panelNorte.add(comboDisciplina);
        panelNorte.add(new JLabel("Formato:"));
        panelNorte.add(comboFormato);
        panelNorte.add(new JLabel("")); //rspacio
        panelNorte.add(btnConfigurar);

        //panel central: inscribir participantes
        JPanel panelCentro=new JPanel(new GridLayout(3, 2, 5, 5));
        panelCentro.setBorder(BorderFactory.createTitledBorder("2. Inscripción de Participantes"));

        comboTipoParticipante=new JComboBox<>(new String[]{"Jugador", "Equipo"});
        txtNombreParticipante=new JTextField();
        btnInscribir = new JButton("Inscribir Participante");
        btnInscribir.setEnabled(false); // Se activa cuando se crea el torneo

        panelCentro.add(new JLabel("Tipo:"));
        panelCentro.add(comboTipoParticipante);
        panelCentro.add(new JLabel("Nombre:"));
        panelCentro.add(txtNombreParticipante);
        panelCentro.add(new JLabel(""));
        panelCentro.add(btnInscribir);

        //panel sur: lista de inscritos(OBSERVER)
        JPanel panelSur=new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createTitledBorder("Participantes Registrados"));
        modeloListaInscritos=new DefaultListModel<>();
        listaInscritosVisual=new JList<>(modeloListaInscritos);
        panelSur.add(new JScrollPane(listaInscritosVisual), BorderLayout.CENTER);

        //eventos de los botones
        //evento Configurar Torneo
        btnConfigurar.addActionListener((ActionEvent e) -> {
            String nombre=txtNombreTorneo.getText();
            Disciplina dis = (Disciplina) comboDisciplina.getSelectedItem();
            FormatoTorneo form = (FormatoTorneo) comboFormato.getSelectedItem();


            if(nombre.trim().isEmpty() || dis == null || form == null) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del torneo");
                return ;
            }

            GestorTorneo.getInstancia().configurarTorneo(nombre, dis, form);

            txtNombreTorneo.setEnabled(false);
            comboDisciplina.setEnabled(false);
            comboFormato.setEnabled(false);
            btnConfigurar.setEnabled(false);

            comboTipoParticipante.setEnabled(false);

            btnInscribir.setEnabled(true);
        });

        // Evento Inscribir (USA EL FACTORY PATTERN)
        btnInscribir.addActionListener((ActionEvent e) -> {
            String nombrePart = txtNombreParticipante.getText();

            if (nombrePart.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre válido.");
                return;
            }

            String tipo = (String) comboTipoParticipante.getSelectedItem();
            if (tipo == null) {
                Disciplina disActual = (Disciplina) comboDisciplina.getSelectedItem();
                if (disActual != null) {
                    // Si la disciplina acepta individual, es Jugador. en el caso contrario, Equipo.
                    tipo = disActual.tipoParticipantePermitido("Individual") ? "Jugador" : "Equipo";
                } else {
                    tipo = "Jugador"; //Se pone por defecto
                }
            }

            String idRandom = "ID-" + nombrePart.toUpperCase().replaceAll("\\s+", "");

            // Crear e inscribir utilizando la fábrica
            Participante nuevo = ParticipanteFactory.crearParticipante(tipo, idRandom, nombrePart, "Contacto");
            GestorTorneo.getInstancia().inscribirParticipante(nuevo);

            txtNombreParticipante.setText("");
        });

        comboDisciplina.addActionListener((ActionEvent e) -> {
            actualizarSugerenciaParticipante();
        });

        actualizarSugerenciaParticipante();

        add(panelNorte, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);


        JPanel panelRegistroCompleto = new JPanel(new BorderLayout());
        panelRegistroCompleto.add(panelNorte, BorderLayout.NORTH);
        panelRegistroCompleto.add(panelCentro, BorderLayout.CENTER);
        panelRegistroCompleto.add(panelSur, BorderLayout.SOUTH);

        // Se crea una barra superior para elegin las pestañas
        JTabbedPane pestanas = new JTabbedPane();

        // Pestaña Regstro torneo
        pestanas.addTab("Inscripción y Configuración", panelRegistroCompleto);
        PanelTablaTorneo panelVisualLlaves = new PanelTablaTorneo();

        // Pestaña de tabla del torneo
        pestanas.addTab(" Tabla Torneo", panelVisualLlaves);

        add(pestanas, BorderLayout.CENTER);
    }

    @Override
    public void actualizar() {
        //limpiamos la lista visual
        modeloListaInscritos.clear();

        //volvemos a cargar los datos actualizados desde el singleton
        GestorTorneo gestor=GestorTorneo.getInstancia();
        for (Participante p : gestor.getInscritos()) {
            modeloListaInscritos.addElement(p.toString());
        }

        //actualizamos el tíiulo de la ventana para mostrar el estado
        if(gestor.getNombre() != null) {
            setTitle("Torneo: " + gestor.getNombre() + " | " + gestor.getDisciplina() + " - Inscritos: " + gestor.getInscritos().size());
        }
    }
//metodo main para que se pueda probar la ventana al instante
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaRegistro().setVisible(true);
        });
    }

    private void actualizarSugerenciaParticipante() {
        //si ya se configuro el torneo, no se altera nada de la interfaz
        if (!btnConfigurar.isEnabled()) return;

        Disciplina seleccionada = (Disciplina) comboDisciplina.getSelectedItem();
        if (seleccionada == null) return;

        if (seleccionada.tieneModalidadFija()) {
            // si la disciplina es "fija" (solo individual o solo equipos) se cambia la seleccion a estas y se "congela"
            if (seleccionada.tipoParticipantePermitido("Individual")) {
                comboTipoParticipante.setSelectedItem("Jugador");
            } else if (seleccionada.tipoParticipantePermitido("Equipo")) {
                comboTipoParticipante.setSelectedItem("Equipo");
            }
            comboTipoParticipante.setEnabled(false);
        } else {
            // Caso de Videojuegos, se permite que se elija libremente
            comboTipoParticipante.setEnabled(true);
        }
    }
}