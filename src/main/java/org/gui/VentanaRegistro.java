package org.gui;
import org.logica.*; //importa el GestorTorneo, la Fabrica, las Disciplinas, etc.
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
/**
 * clase principal de la interfaz grafica que maneja el registro de torneos y participantes.
 * implementa el patron observer para reaccionar a los cambios en el gestor central.
 */
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
    /**
     * constructor de la ventana de registro.
     * inicializa todos los componentes visuales, paneles de configuracion, selectores de imagenes
     * y establece las pestañas principales de la aplicacion.
     */
    public VentanaRegistro() {
        setTitle("Sistema de Gestión de Torneos - Registro");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        //1 me suscribo como observador
        GestorTorneo.getInstancia().registrarObserver(this);

        //panel superior: configurar el torneo
        JPanel panelNorte = new JPanel(new GridLayout(4, 4, 5, 5)); //cambiamos a 3 filas
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

        //fila 1
        panelNorte.add(new JLabel("Nombre:"));
        panelNorte.add(txtNombreTorneo);
        panelNorte.add(new JLabel("Disciplina:"));
        panelNorte.add(comboDisciplina);

        //fila 2
        panelNorte.add(new JLabel("Formato:"));
        panelNorte.add(comboFormato);
        panelNorte.add(new JLabel("N° Canchas/Mesas:"));
        panelNorte.add(spinCanchas);
        panelNorte.add(new JLabel("Duración partido (min):"));
        panelNorte.add(spinIntervalo);

        //fila 3
        JPanel panelTiempo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelTiempo.add(spinHora);
        panelTiempo.add(new JLabel(" : "));
        panelTiempo.add(spinMinuto);

        panelNorte.add(new JLabel("Hora Inicio:"));
        panelNorte.add(panelTiempo);
        panelNorte.add(new JLabel("")); //espacio vacio para rellenar la grilla
        panelNorte.add(btnConfigurar);

        //panel central:inscribir participantes
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBorder(BorderFactory.createTitledBorder("2. Inscripción de Participantes"));

        JPanel panelDatosParticipante = new JPanel(new BorderLayout(10, 10));

        //datos de los jugadores/equipos
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

        //previsualizacion de la bandera del equipo
        comboCategoria = new JComboBox<>(new String[]{"Banderas", "Avatares", "Selecciones"});
        comboArchivo = new JComboBox<>();

        //actualiza los archivos disponibles segun la categoria elegida
        comboCategoria.addActionListener(e -> actualizarComboArchivos());
        actualizarComboArchivos(); // Carga inicial

        JButton btnAplicarAvatar = new JButton("Aplicar");

        JButton btnImagenPersonalizada = new JButton("Subir desde PC...");

        //panel de control para las fotos de la app
        JPanel panelSelectoresImg = new JPanel(new GridLayout(3, 1, 2, 2));
        panelSelectoresImg.add(comboCategoria);
        panelSelectoresImg.add(comboArchivo);
        panelSelectoresImg.add(btnAplicarAvatar);
        panelSelectoresImg.add(btnImagenPersonalizada);

        //vista previa física de la imagen
        lblAvatarPreview = new JLabel();
        lblAvatarPreview.setPreferredSize(new Dimension(80, 80));
        lblAvatarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        lblAvatarPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatarPreview.setVerticalAlignment(SwingConstants.CENTER);

        //cargar imagen por defecto inicial
        cargarImagenInterna("Avatares", "avatar_0.png");

        //contenedor para el bloque izquierdo
        JPanel panelAvatarCompleto = new JPanel(new BorderLayout(5, 5));
        panelAvatarCompleto.add(lblAvatarPreview, BorderLayout.CENTER);
        panelAvatarCompleto.add(panelSelectoresImg, BorderLayout.SOUTH);

        //evento para aplicar la imagen seleccionada de los combos
        btnAplicarAvatar.addActionListener(e -> {
            String cat = (String) comboCategoria.getSelectedItem();
            String arc = (String) comboArchivo.getSelectedItem();
            if (cat != null && arc != null) {
                cargarImagenInterna(cat, arc);
            }
        });


        //evento para que el usuario pueda subir una imagens desde su pc
        btnImagenPersonalizada.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecciona tu imagen personalizada");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Imágenes", "jpg", "png", "jpeg"));

            int seleccion = fileChooser.showOpenDialog(this);
            if (seleccion == JFileChooser.APPROVE_OPTION) {
                java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
                //guardamos la ruta absoluta de la imagen
                rutaAvatarActual = archivoSeleccionado.getAbsolutePath();

                //actualizamos la vista previa
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

        //panel para agregar miembros de un equipo
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

        //lista de miembros al centro
        panelCentro.add(panelDatosParticipante, BorderLayout.NORTH);
        panelCentro.add(panelMiembros, BorderLayout.CENTER);

        btnAgregarMiembro.addActionListener((ActionEvent e) -> {
            String nombreMiembro = txtMiembro.getText().trim();
            if (nombreMiembro.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el nombre del miembro.");
                return;
            }

            for (int i = 0; i < modeloMiembrosPendientes.size(); i++) {
                if (modeloMiembrosPendientes.get(i).equalsIgnoreCase(nombreMiembro)) {

                    JOptionPane.showMessageDialog(this, "Este miembro ya esta agregado.");
                    return ;
                }
            }

            modeloMiembrosPendientes.addElement(nombreMiembro);
            txtMiembro.setText("");
        });

        comboTipoParticipante.addActionListener((ActionEvent e) -> {
            String tipo = (String) comboTipoParticipante.getSelectedItem();
            panelMiembros.setVisible("Equipo".equalsIgnoreCase(tipo));
        });

        //panel sur:lista de inscritos(OBSERVER)
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

            //mandamos los nuevos datos al gestor
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

        //evento Inscribir (USA EL FACTORY PATTERN)
        btnInscribir.addActionListener((ActionEvent e) -> {
            String nombrePart = txtNombreParticipante.getText();
            String contactoPart = txtContactoParticipante.getText().trim();


            if (nombrePart.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un nombre válido.");
                return;
            }

            for (Participante p : GestorTorneo.getInstancia().getInscritos()) {

                if (p.getNombre().equalsIgnoreCase(nombrePart.trim())) {
                    JOptionPane.showMessageDialog(this, "Ya existe un participante inscrito con el nombre \"" + nombrePart.trim() + "\".");

                    return ;
                }
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
                    tipo = "Jugador"; //se pone por defecto
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

            //crear e inscribir utilizando la fábrica original
            Participante nuevo = ParticipanteFactory.crearParticipante(tipo, idRandom, nombrePart, contactoPart);

            //asignamos el avatar actual
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

        //se crea una barra superior para elegin las pestañas
        JTabbedPane pestanas = new JTabbedPane();

        //pestaña Regstro torneo
        pestanas.addTab("Inscripción y Configuración", panelRegistroCompleto);
        PanelTablaTorneo panelVisualLlaves = new PanelTablaTorneo();

        //pestaña de tabla del torneo
        pestanas.addTab(" Tabla Torneo", panelVisualLlaves);

        //pestaña calendario
        pestanas.addTab("Calendario", new PanelCalendario());

        add(pestanas, BorderLayout.CENTER);
    }
    /**
     * actualiza la lista visual de participantes inscritos y el titulo de la ventana
     * cada vez que el gestor de torneo emite una notificacion.
     */
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
    /**
     * metodo principal para ejecutar la ventana de forma independiente con fines de prueba.
     *
     * @param args argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaRegistro().setVisible(true);
        });
    }
    /**
     * bloquea o habilita el selector de tipo de participante dependiendo de si la
     * disciplina elegida tiene una modalidad fija(solo equipos o solo individuos).
     */
    private void actualizarSugerenciaParticipante() {
        //si ya se configuro el torneo, no se altera nada de la interfaz
        if (!btnConfigurar.isEnabled()) return;

        Disciplina seleccionada = (Disciplina) comboDisciplina.getSelectedItem();
        if (seleccionada == null) return;

        if (seleccionada.tieneModalidadFija()) {
            //si la disciplina es "fija" (solo individual o solo equipos) se cambia la seleccion a estas y se "congela"
            if (seleccionada.tipoParticipantePermitido("Individual")) {
                comboTipoParticipante.setSelectedItem("Jugador");
            } else if (seleccionada.tipoParticipantePermitido("Equipo")) {
                comboTipoParticipante.setSelectedItem("Equipo");
            }
            comboTipoParticipante.setEnabled(false);
        } else {
            //caso de Videojuegos,se permite que se elija libremente
            comboTipoParticipante.setEnabled(true);
        }
    }

    /**
     * llena el combo de archivos dependiendo de la categoria seleccionada.
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
            comboArchivo.addItem("avatar_1.png");
            comboArchivo.addItem("avatar_2.png");
            comboArchivo.addItem("avatar_3.png");
            comboArchivo.addItem("avatar_4.png");
            comboArchivo.addItem("avatar_5.png");
            comboArchivo.addItem("avatar_6.png");

        } else if ("Selecciones".equals(categoria)) {
            comboArchivo.addItem("s_alemania.png");
            comboArchivo.addItem("s_argentina.png");
            comboArchivo.addItem("s_brasil.png");
            comboArchivo.addItem("s_cabo_verde.png");
            comboArchivo.addItem("s_canada.png");
            comboArchivo.addItem("s_chile.png");
            comboArchivo.addItem("s_colombia.png");
            comboArchivo.addItem("s_croacia.png");
            comboArchivo.addItem("s_ecuador.png");
            comboArchivo.addItem("s_espana.png");
            comboArchivo.addItem("s_italia.png");
            comboArchivo.addItem("s_japon.png");
            comboArchivo.addItem("s_mexico.png");
            comboArchivo.addItem("s_paisesbajos.png");
            comboArchivo.addItem("s_paraguay.png");
            comboArchivo.addItem("s_peru.png");
            comboArchivo.addItem("s_uruguay.png");
        }
    }

    /**
     * carga una imagen desde los recursos internos del proyecto y la escala para
     * mostrarla en la vista previa del avatar.
     * * @param categoria la carpeta de la categoria seleccionada.
     * @param archivo el nombre exacto del archivo de imagen.
     */
    private void cargarImagenInterna(String categoria, String archivo) {
        //ruta para la imagen seleccionada
        String rutaInterna = "imagenes/" + categoria + "/" + archivo;
        java.net.URL urlImg = VentanaRegistro.class.getResource(rutaInterna);

        if (urlImg!=null) {
            ImageIcon icono = new ImageIcon(urlImg);
            Image imgEscalada = icono.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            lblAvatarPreview.setIcon(new ImageIcon(imgEscalada));
            lblAvatarPreview.setText("");

            //guardamos la ruta para asignarla al participante
            rutaAvatarActual = "src/main/resources/org/gui/" + rutaInterna;
        }
    }
}