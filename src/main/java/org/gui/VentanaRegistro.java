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
    private JSpinner spinHora;
    private JSpinner spinMinuto;
    private JSpinner spinCanchas;
    private JSpinner spinIntervalo;

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
    private JComboBox<String> comboCategoria;
    private JComboBox<String> comboArchivo;

    private JButton btnGestionarInscritos;

    private DefaultListModel<String> modeloListaInscritos;
    private JList<String> listaInscritosVisual;

    public VentanaRegistro() {
        setTitle("Sistema de Gestión de Torneos - Registro");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //1 me suscribo como observador
        GestorTorneo.getInstancia().registrarObserver(this);

        //panel superior: configurar el torneo
        JPanel panelNorte = new JPanel(new GridLayout(4, 4, 5, 5)); // Cambiamos a 3 filas
        panelNorte.setBorder(BorderFactory.createTitledBorder("1. Definir Características del Torneo"));

        txtNombreTorneo = new JTextField();
        comboDisciplina = new JComboBox<>(Disciplina.values());
        comboFormato = new JComboBox<>(FormatoTorneo.values());
        btnConfigurar = new JButton("Crear Torneo");

        //configuramos los selectores de numeros
        spinHora = new JSpinner(new SpinnerNumberModel(9, 0, 23, 1)); //0 a 23 hrs
        spinMinuto = new JSpinner(new SpinnerNumberModel(0, 0, 59, 15)); //de 15 en 15 mins
        spinCanchas = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1)); //1 cancha min
        spinIntervalo = new JSpinner(new SpinnerNumberModel(60, 15, 300, 15));//tiempo dsps de cada partido

        // Fila 1
        panelNorte.add(new JLabel("Nombre:"));
        panelNorte.add(txtNombreTorneo);
        panelNorte.add(new JLabel("Disciplina:"));
        panelNorte.add(comboDisciplina);

        // Fila 2
        panelNorte.add(new JLabel("Formato:"));
        panelNorte.add(comboFormato);
        panelNorte.add(new JLabel("N° Canchas/Mesas:"));
        panelNorte.add(spinCanchas);
        panelNorte.add(new JLabel("Duración partido (min):"));
        panelNorte.add(spinIntervalo);

        // Fila 3
        JPanel panelTiempo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTiempo.add(spinHora);
        panelTiempo.add(new JLabel(" : "));
        panelTiempo.add(spinMinuto);

        panelNorte.add(new JLabel("Hora Inicio:"));
        panelNorte.add(panelTiempo);
        panelNorte.add(new JLabel("")); // Espacio vacío para rellenar la grilla
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
        comboCategoria = new JComboBox<>(new String[]{"Banderas", "Avatares", "Personajes"});
        comboArchivo = new JComboBox<>();

        // Actualiza los archivos disponibles según la categoría elegida
        comboCategoria.addActionListener(e -> actualizarComboArchivos());
        actualizarComboArchivos(); // Carga inicial

        JButton btnAplicarAvatar = new JButton("Aplicar");

        JButton btnImagenPersonalizada = new JButton("Subir desde PC...");

        // Panel de control para las fotos de la app
        JPanel panelSelectoresImg = new JPanel(new GridLayout(3, 1, 2, 2));
        panelSelectoresImg.add(comboCategoria);
        panelSelectoresImg.add(comboArchivo);
        panelSelectoresImg.add(btnAplicarAvatar);
        panelSelectoresImg.add(btnImagenPersonalizada);

        // Vista previa física de la imagen
        lblAvatarPreview = new JLabel();
        lblAvatarPreview.setPreferredSize(new Dimension(80, 80));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        lblAvatarPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatarPreview.setVerticalAlignment(SwingConstants.CENTER);

        // Cargar imagen por defecto inicial
        cargarImagenInterna("Avatares", "avatar_0.png");

        // contenedor para el bloque izquierdo
        JPanel panelAvatarCompleto = new JPanel(new BorderLayout(5, 5));
        panelAvatarCompleto.add(lblAvatarPreview, BorderLayout.CENTER);
        panelAvatarCompleto.add(panelSelectoresImg, BorderLayout.SOUTH);

        // Evento para aplicar la imagen seleccionada de los combos
        btnAplicarAvatar.addActionListener(e -> {
            String cat = (String) comboCategoria.getSelectedItem();
            String arc = (String) comboArchivo.getSelectedItem();
            if (cat != null && arc != null) {
                cargarImagenInterna(cat, arc);
            }
        });


        // Evento para que el usuario pueda subir una imagens desde su pc
        btnImagenPersonalizada.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona tu imagen personalizada");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

            int seleccion = fileChooser.showOpenDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
                // Guardamos la ruta absoluta de la imagen
                rutaAvatarActual = archivoSeleccionado.getAbsolutePath();

                // Actualizamos la vista previa
                ImageIcon iconoNuevo = new ImageIcon(rutaAvatarActual);
                Image img = iconoNuevo.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                lblAvatarPreview.setIcon(new ImageIcon(img));
                lblAvatarPreview.setText("");
            }
        });


        panelDatosParticipante.add(panelAvatarCompleto, BorderLayout.WEST);
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
            String nombre = txtNombreTorneo.getText();
            Disciplina dis = (Disciplina) comboDisciplina.getSelectedItem();
            FormatoTorneo form = (FormatoTorneo) comboFormato.getSelectedItem();

            //capturamos los datos nuevos
            int canchas = (int) spinCanchas.getValue();
            int hora = (int) spinHora.getValue();
            int min = (int) spinMinuto.getValue();
            int intervalo = (int) spinIntervalo.getValue();

            if(nombre.trim().isEmpty() || dis == null || form == null) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos del torneo");
                return ;
            }

            //  mandamos los nuevos datos al gestor
            GestorTorneo.getInstancia().configurarTorneo(nombre, dis, form, hora, min, canchas, intervalo);

            txtNombreTorneo.setEnabled(false);
            comboDisciplina.setEnabled(false);
            comboFormato.setEnabled(false);
            spinHora.setEnabled(false); //bloqueamos los campos nuevos
            spinMinuto.setEnabled(false);
            spinCanchas.setEnabled(false);
            btnConfigurar.setEnabled(false);
            spinIntervalo.setEnabled(false);

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

            if ("Equipo".equalsIgnoreCase(tipo)) {
                Disciplina disActual = (Disciplina) comboDisciplina.getSelectedItem();
                int cantidadMiembros = modeloMiembrosPendientes.size();

                if (disActual != null) {
                    if (cantidadMiembros < disActual.getMinimoJugadores() || cantidadMiembros > disActual.getMaximoJugadores()) {
                        JOptionPane.showMessageDialog(this,
                                "Error: Para " + disActual.name() + " el equipo debe tener entre " +
                                        disActual.getMinimoJugadores() + " y " + disActual.getMaximoJugadores() + " miembros registrados.",
                                "Limites de Equipo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else if (cantidadMiembros == 0) {
                    JOptionPane.showMessageDialog(this, "Agregue al menos un miembro al equipo.");
                    return;
                }
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

        // Pestaña calendario
        pestanas.addTab("Calendario", new PanelCalendario());

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


    /**
     * Llena el combo de archivos dependiendo de la categoria seleccionada
     */
    private void actualizarComboArchivos() {
        comboArchivo.removeAllItems();
        String categoria = (String) comboCategoria.getSelectedItem();

        if ("Banderas".equals(categoria)) {
            comboArchivo.addItem("alemania.png");
            comboArchivo.addItem("argentina.png");
            comboArchivo.addItem("brasil.png");
            comboArchivo.addItem("chile.png");
            comboArchivo.addItem("china.png");
            comboArchivo.addItem("colombia.png");
            comboArchivo.addItem("ecuador.png");
            comboArchivo.addItem("espana.png");
            comboArchivo.addItem("estados-unidos.png");
            comboArchivo.addItem("francia.png");
            comboArchivo.addItem("italia.png");
            comboArchivo.addItem("marruecos.png");
            comboArchivo.addItem("mexico.png");
            comboArchivo.addItem("panama.png");
            comboArchivo.addItem("paraguay.png");
            comboArchivo.addItem("peru.png");
            comboArchivo.addItem("portugal.png");
            comboArchivo.addItem("reino-unido.png");
            comboArchivo.addItem("uruguay.png");
            comboArchivo.addItem("venezuela.png");

        } else if ("Avatares".equals(categoria)) {
            comboArchivo.addItem("avatar_0.png");

        } else if ("Personajes".equals(categoria)) {

        }
    }

    /**
     * Carga la imagen
     */
    private void cargarImagenInterna(String categoria, String archivo) {
        // ruta para la imagen seleccionada
        String rutaInterna = "imagenes/" + categoria + "/" + archivo;
        java.net.URL urlImg = VentanaRegistro.class.getResource(rutaInterna);

        if (urlImg != null) {
            ImageIcon icono = new ImageIcon(urlImg);
            Image imgEscalada = icono.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblAvatarPreview.setIcon(new ImageIcon(imgEscalada));
            lblAvatarPreview.setText("");

            // Guardamos la ruta para asignarla al Participante
            rutaAvatarActual = "src/main/resources/org/gui/" + rutaInterna;
        }
    }
}