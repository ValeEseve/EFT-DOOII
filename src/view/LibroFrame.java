
package view;

import controller.LibroController;
import model.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class LibroFrame extends JFrame {

    private final LibroController controlador;

    private JTable tablaLibros;
    private DefaultTableModel modeloTabla;

    private JTextField campoTitulo;
    private JTextField campoAutor;
    private JTextField campoIsbn;
    private JTextField campoEditorial;
    private JTextField campoStock;
    private JTextField campoCategoria;

    private JButton botonAgregar;
    private JButton botonActualizar;
    private JButton botonEliminar;
    private JButton botonLimpiar;
    private JButton botonRecargar;

    private JLabel etiquetaEstado;

    private int idSeleccionado = -1;
    private boolean ocupado = false;

    public LibroFrame() {
        this.controlador = new LibroController(this);

        setTitle("Biblioteca Escolar - Gestión de libros");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        crearComponentes();
        controlador.cargarLibros();
    }

    private void crearComponentes() {

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        panelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JLabel titulo = new JLabel(
                "Gestión de libros",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));

        panelPrincipal.add(titulo, BorderLayout.NORTH);

        // TABLA DE LIBROS
        String[] columnas = {
                "ID", "Título", "Autor", "ISBN",
                "Editorial", "Stock", "ID categoría"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        tablaLibros = new JTable(modeloTabla);
        tablaLibros.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        tablaLibros.setAutoCreateRowSorter(true);

        tablaLibros.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting() && !ocupado) {
                seleccionarLibro();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaLibros);

        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // FORMULARIO
        JPanel panelInferior = new JPanel(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(3, 4, 10, 8));

        campoTitulo = new JTextField();
        campoAutor = new JTextField();
        campoIsbn = new JTextField();
        campoEditorial = new JTextField();
        campoStock = new JTextField();
        campoCategoria = new JTextField();

        formulario.add(new JLabel("Título:"));
        formulario.add(campoTitulo);

        formulario.add(new JLabel("Autor:"));
        formulario.add(campoAutor);

        formulario.add(new JLabel("ISBN:"));
        formulario.add(campoIsbn);

        formulario.add(new JLabel("Editorial:"));
        formulario.add(campoEditorial);

        formulario.add(new JLabel("Stock:"));
        formulario.add(campoStock);

        formulario.add(new JLabel("ID categoría:"));
        formulario.add(campoCategoria);

        panelInferior.add(formulario, BorderLayout.CENTER);

        // BOTONES
        JPanel panelBotones = new JPanel(new FlowLayout());

        botonAgregar = new JButton("Agregar");
        botonActualizar = new JButton("Actualizar");
        botonEliminar = new JButton("Eliminar");
        botonLimpiar = new JButton("Limpiar");
        botonRecargar = new JButton("Recargar");

        panelBotones.add(botonAgregar);
        panelBotones.add(botonActualizar);
        panelBotones.add(botonEliminar);
        panelBotones.add(botonLimpiar);
        panelBotones.add(botonRecargar);

        botonAgregar.addActionListener(e -> agregarLibro());
        botonActualizar.addActionListener(e -> actualizarLibro());
        botonEliminar.addActionListener(e -> eliminarLibro());
        botonLimpiar.addActionListener(e -> limpiarFormulario());
        botonRecargar.addActionListener(e -> controlador.cargarLibros());

        etiquetaEstado = new JLabel(
                "Selecciona un libro o agrega uno nuevo."
        );

        JPanel panelAcciones = new JPanel(new BorderLayout());

        panelAcciones.add(panelBotones, BorderLayout.CENTER);
        panelAcciones.add(etiquetaEstado, BorderLayout.SOUTH);

        panelInferior.add(panelAcciones, BorderLayout.SOUTH);

        panelPrincipal.add(panelInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    public void poblarLibros(ArrayList<Libro> libros) {
        modeloTabla.setRowCount(0);
        if (libros != null) {
            for (Libro libro : libros) {
                Object[] fila = {
                        libro.getId(),
                        libro.getTitulo(),
                        libro.getAutor(),
                        libro.getIsbn(),
                        libro.getEditorial(),
                        libro.getStock(),
                        libro.getIdCategoria()
                };
                modeloTabla.addRow(fila);
            }
        }
    }

    public void setEstado(String texto) {
        etiquetaEstado.setText(texto);
    }

    private void seleccionarLibro() {

        int filaVista = tablaLibros.getSelectedRow();

        if (filaVista == -1) {
            return;
        }

        int fila = tablaLibros.convertRowIndexToModel(filaVista);

        idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);

        campoTitulo.setText(
                modeloTabla.getValueAt(fila, 1).toString()
        );

        campoAutor.setText(
                modeloTabla.getValueAt(fila, 2).toString()
        );

        campoIsbn.setText(
                modeloTabla.getValueAt(fila, 3).toString()
        );

        campoEditorial.setText(
                modeloTabla.getValueAt(fila, 4).toString()
        );

        campoStock.setText(
                modeloTabla.getValueAt(fila, 5).toString()
        );

        campoCategoria.setText(
                modeloTabla.getValueAt(fila, 6).toString()
        );

        etiquetaEstado.setText(
                "Libro seleccionado: ID " + idSeleccionado
        );
    }

    private Libro leerFormulario() {

        String titulo = campoTitulo.getText().trim();
        String autor = campoAutor.getText().trim();
        String isbn = campoIsbn.getText().trim();
        String editorial = campoEditorial.getText().trim();

        if (titulo.isEmpty()
                || autor.isEmpty()
                || isbn.isEmpty()
                || editorial.isEmpty()
                || campoStock.getText().trim().isEmpty()
                || campoCategoria.getText().trim().isEmpty()) {

            mostrarError("Debes completar todos los campos.");
            return null;
        }

        if (titulo.length() > 200
                || autor.length() > 100
                || isbn.length() > 20
                || editorial.length() > 100) {

            mostrarError("Uno de los textos supera el largo permitido.");
            return null;
        }

        int stock;
        int idCategoria;

        try {

            stock = Integer.parseInt(
                    campoStock.getText().trim()
            );

            idCategoria = Integer.parseInt(
                    campoCategoria.getText().trim()
            );

        } catch (NumberFormatException e) {

            mostrarError(
                    "Stock e ID categoría deben ser números enteros."
            );
            return null;
        }

        if (stock < 0) {

            mostrarError("El stock no puede ser negativo.");
            return null;
        }

        if (idCategoria <= 0) {

            mostrarError(
                    "El ID de categoría debe ser mayor que cero."
            );
            return null;
        }

        return new Libro(
                idSeleccionado,
                titulo,
                autor,
                isbn,
                editorial,
                stock,
                idCategoria
        );
    }

    private void agregarLibro() {

        if (ocupado) {
            return;
        }

        Libro libro = leerFormulario();

        if (libro == null) {
            return;
        }

        controlador.agregarLibro(libro);
    }

    private void actualizarLibro() {

        if (ocupado) {
            return;
        }

        if (idSeleccionado == -1) {

            mostrarError("Selecciona un libro de la tabla.");
            return;
        }

        Libro libro = leerFormulario();

        if (libro == null) {
            return;
        }

        controlador.actualizarLibro(libro);
    }

    private void eliminarLibro() {

        if (ocupado) {
            return;
        }

        if (idSeleccionado == -1) {

            mostrarError("Selecciona un libro para eliminar.");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar el libro seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        controlador.eliminarLibro(idSeleccionado);
    }

    public void limpiarFormulario() {

        idSeleccionado = -1;

        tablaLibros.clearSelection();

        campoTitulo.setText("");
        campoAutor.setText("");
        campoIsbn.setText("");
        campoEditorial.setText("");
        campoStock.setText("");
        campoCategoria.setText("");

        etiquetaEstado.setText("Formulario limpio.");
    }

    public void cambiarEstadoBotones(boolean habilitados) {

        ocupado = !habilitados;

        botonAgregar.setEnabled(habilitados);
        botonActualizar.setEnabled(habilitados);
        botonEliminar.setEnabled(habilitados);
        botonLimpiar.setEnabled(habilitados);
        botonRecargar.setEnabled(habilitados);

        tablaLibros.setEnabled(habilitados);

        campoTitulo.setEnabled(habilitados);
        campoAutor.setEnabled(habilitados);
        campoIsbn.setEnabled(habilitados);
        campoEditorial.setEnabled(habilitados);
        campoStock.setEnabled(habilitados);
        campoCategoria.setEnabled(habilitados);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void mostrarError(String mensaje) {

        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
