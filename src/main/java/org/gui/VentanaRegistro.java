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
    private JTextField txtContactoParticipante;
    private JButton btnInscribir;

    private JPanel panelMiembros;
    private JTextField txtMiembro;
    private JButton btnAgregarMiembro;
    private DefaultListModel<String> modeloMiembrosPendientes;
    private JList<String> listaMiembrosPendientes;

    private JLabel lblAvatarPreview;
    private String rutaAvatarActual = "avatar_0.png";

    private JButton btnGestionarInscritos;

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
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(BorderFactory.createTitledBorder("2. Inscripción de Participantes"));

        JPanel panelDatosParticipante = new JPanel(new BorderLayout(10, 10));

        // Datos de los jugadores/equipos
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 5, 5));
        comboTipoParticipante = new JComboBox<>(new String[]{"Jugador", "Equipo"});
        txtNombreParticipante = new JTextField();
        txtContactoParticipante = new JTextField();
        btnInscribir = new JButton("Inscribir");
        btnInscribir.setEnabled(false); // Ativado apenas após criar o torneio

        panelFormulario.add(new JLabel("Tipo:"));
        panelFormulario.add(comboTipoParticipante);
        panelFormulario.add(new JLabel("Nombre:"));
        panelFormulario.add(txtNombreParticipante);
        panelFormulario.add(new JLabel("Contacto:"));
        panelFormulario.add(txtContactoParticipante);
        panelFormulario.add(new JLabel(""));
        panelFormulario.add(btnInscribir);

        // Previsualizacion de la bandera del equipo
        lblAvatarPreview = new JLabel("Sin Imagen");
        lblAvatarPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatarPreview.setPreferredSize(new Dimension(80, 80));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JButton btnCambiarAvatar = new JButton("Imagen Personalizada");

        JPanel panelAvatar = new JPanel(new BorderLayout(5, 5));
        panelAvatar.add(lblAvatarPreview, BorderLayout.CENTER);
        panelAvatar.add(btnCambiarAvatar, BorderLayout.SOUTH);

        // Evento para seleccionar imagen personalizada
        btnCambiarAvatar.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona el avatar o bandera");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

            int seleccion = fileChooser.showOpenDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
                rutaAvatarActual = archivoSeleccionado.getAbsolutePath();

                // Icono nuevo seleccionado
                ImageIcon iconoNuevo = new ImageIcon(rutaAvatarActual);
                Image img = iconoNuevo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblAvatarPreview.setIcon(new ImageIcon(img));
                lblAvatarPreview.setText("");
            }
        });



        panelDatosParticipante.add(panelAvatar, BorderLayout.WEST);
        panelDatosParticipante.add(panelFormulario, BorderLayout.CENTER);

        panelMiembros = new JPanel(new BorderLayout(5, 5));
        panelMiembros.setBorder(BorderFactory.createTitledBorder("Miembros del Equipo (a inscribir)"));

        JPanel panelAgregarMiembro = new JPanel(new BorderLayout(5, 5));
        txtMiembro = new JTextField();
        btnAgregarMiembro = new JButton("Agregar Miembro");
        panelAgregarMiembro.add(txtMiembro, BorderLayout.CENTER);
        panelAgregarMiembro.add(btnAgregarMiembro, BorderLayout.EAST);

        modeloMiembrosPendientes = new DefaultListModel<>();
        listaMiembrosPendientes = new JList<>(modeloMiembrosPendientes);

        panelMiembros.add(panelAgregarMiembro, BorderLayout.NORTH);
        panelMiembros.add(new JScrollPane(listaMiembrosPendientes), BorderLayout.CENTER);
        panelMiembros.setVisible(false); // Inicializa oculto

        panelCentro.add(panelDatosParticipante, BorderLayout.NORTH);
        panelCentro.add(panelMiembros, BorderLayout.CENTER);

        // Panel para agregar miembros de un equipo
        panelMiembros = new JPanel(new BorderLayout(5, 5));
        panelMiembros.setBorder(BorderFactory.createTitledBorder("Miembros del Equipo (a inscribir)"));

        panelAgregarMiembro = new JPanel(new BorderLayout(5, 5));
        txtMiembro = new JTextField();
        btnAgregarMiembro = new JButton("Agregar Miembro");
        panelAgregarMiembro.add(txtMiembro, BorderLayout.CENTER);
        panelAgregarMiembro.add(btnAgregarMiembro, BorderLayout.EAST);

        modeloMiembrosPendientes = new DefaultListModel<>();
        listaMiembrosPendientes = new JList<>(modeloMiembrosPendientes);

        panelMiembros.add(panelAgregarMiembro, BorderLayout.NORTH);
        panelMiembros.add(new JScrollPane(listaMiembrosPendientes), BorderLayout.CENTER);
        panelMiembros.setVisible(false); // Arranca oculto, solo aplica a Equipo

        // Lista de miembros al centro
        panelCentro.add(panelDatosParticipante, BorderLayout.NORTH);
        panelCentro.add(panelMiembros, BorderLayout.CENTER);

        btnAgregarMiembro.addActionListener((ActionEvent e) -> {
            String nombreMiembro = txtMiembro.getText().trim();
            if (nombreMiembro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del miembro.");
                return;
            }
            modeloMiembrosPendientes.addElement(nombreMiembro);
            txtMiembro.setText("");
        });

        comboTipoParticipante.addActionListener((ActionEvent e) -> {
            String tipo = (String) comboTipoParticipante.getSelectedItem();
            panelMiembros.setVisible("Equipo".equalsIgnoreCase(tipo));
        });

        //panel sur: lista de inscritos(OBSERVER)
        JPanel panelSur=new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createTitledBorder("Participantes Registrados"));
        modeloListaInscritos=new DefaultListModel<>();
        listaInscritosVisual=new JList<>(modeloListaInscritos);
        panelSur.add(new JScrollPane(listaInscritosVisual), BorderLayout.CENTER);

        btnGestionarInscritos = new JButton("Gestionar Inscritos");
        JPanel panelBotonGestion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotonGestion.add(btnGestionarInscritos);
        panelSur.add(panelBotonGestion, BorderLayout.SOUTH);

        btnGestionarInscritos.addActionListener((ActionEvent e) -> {
            new VentanaGestionInscritos(this).setVisible(true);
        });

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
            String contactoPart = txtContactoParticipante.getText().trim();


            if (nombrePart.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre válido.");
                return;
            }

            if (contactoPart.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un dato de contacto.");
                return;
            }

            String tipo = (String) comboTipoParticipante.getSelectedItem();
            if (tipo == null) {
                Disciplina disActual = (Disciplina) comboDisciplina.getSelectedItem();
                if (disActual != null) {
                    tipo = disActual.tipoParticipantePermitido("Individual") ? "Jugador" : "Equipo";
                } else {
                    tipo = "Jugador"; //Se pone por defecto
                }
            }

            if ("Equipo".equalsIgnoreCase(tipo) && modeloMiembrosPendientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Agregue al menos un miembro al equipo.");
                return;
            }

            String idRandom = "ID-" + nombrePart.toUpperCase().replaceAll("\\s+", "");

            // Crear e inscribir utilizando la fábrica original
            Participante nuevo = ParticipanteFactory.crearParticipante(tipo, idRandom, nombrePart, contactoPart);

            // Asignamos el avatar actual
            nuevo.setRutaAvatar(rutaAvatarActual);

            if (nuevo instanceof Equipo) {
                Equipo equipoNuevo = (Equipo) nuevo;
                for (int i = 0; i < modeloMiembrosPendientes.size(); i++) {
                    equipoNuevo.agregarMiembro(modeloMiembrosPendientes.get(i));
                }
                modeloMiembrosPendientes.clear();
            }

            GestorTorneo.getInstancia().inscribirParticipante(nuevo);

            txtNombreParticipante.setText("");
            txtContactoParticipante.setText("");
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