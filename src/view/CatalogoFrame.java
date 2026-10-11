package view;

import controller.CatalogoController;
import model.Libro;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class CatalogoFrame extends JFrame {
    private final Usuario usuario;
    private final CatalogoController controlador;
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton catalogo = new JButton("Ver catálogo");
    private final JButton historial = new JButton("Mis préstamos");
    private final JButton pedirPrestamo = new JButton("Solicitar préstamo");
    private final JButton devolverLibro = new JButton("Devolver libro");
    private final JLabel estado = new JLabel("Consulta los libros disponibles y tu historial.");

    public CatalogoFrame(Usuario usuario) {
        this.usuario = usuario;
        if (!"estudiante".equals(usuario.getRol()))
            throw new SecurityException("Esta vista es para estudiantes.");
        this.controlador = new CatalogoController(this);

        setTitle("Biblioteca Escolar - Catálogo y mis préstamos");
        setSize(950, 540);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel titulo = new JLabel("Catálogo de biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(6, 6));
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(catalogo);
        botones.add(historial);
        botones.add(pedirPrestamo);
        botones.add(devolverLibro);
        inferior.add(botones, BorderLayout.NORTH);
        inferior.add(estado, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);

        pedirPrestamo.addActionListener(e -> accionarPrestamo());
        devolverLibro.addActionListener(e -> accionarDevolucion());
        catalogo.addActionListener(e -> controlador.cargarCatalogo());
        historial.addActionListener(e -> controlador.cargarHistorial(usuario.getRut()));

        devolverLibro.setVisible(false);
        controlador.cargarCatalogo();
    }

    public void habilitar(boolean valor) {
        catalogo.setEnabled(valor);
        historial.setEnabled(valor);
        pedirPrestamo.setEnabled(valor);
        devolverLibro.setEnabled(valor);
        tabla.setEnabled(valor);
    }

    public void setEstado(String texto) {
        estado.setText(texto);
    }

    public void mostrarCatalogo(ArrayList<Libro> libros) {
        pedirPrestamo.setVisible(true);
        devolverLibro.setVisible(false);
        modelo.setColumnIdentifiers(new String[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Stock"});
        modelo.setRowCount(0);
        if (libros != null) {
            for (Libro l : libros) {
                modelo.addRow(new Object[]{l.getId(), l.getTitulo(),
                        l.getAutor(), l.getIsbn(), l.getEditorial(), l.getStock()});
            }
        }
    }

    public void mostrarHistorial(ArrayList<Object[]> filas) {
        pedirPrestamo.setVisible(false);
        devolverLibro.setVisible(true);
        modelo.setColumnIdentifiers(new String[]{"ID", "Libro", "Préstamo", "Vencimiento", "Estado"});
        modelo.setRowCount(0);
        if (filas != null) {
            for (Object[] fila : filas) {
                modelo.addRow(fila);
            }
        }
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void accionarPrestamo() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            mostrarError("Selecciona un libro del catálogo.");
            return;
        }
        int fila = tabla.convertRowIndexToModel(vista);
        int idLibro = (Integer) modelo.getValueAt(fila, 0);
        String titulo = String.valueOf(modelo.getValueAt(fila, 1));
        Object stockObj = modelo.getValueAt(fila, 5);
        int stock = (stockObj instanceof Number) ? ((Number) stockObj).intValue() : Integer.parseInt(stockObj.toString());

        if (stock <= 0) {
            mostrarError("El libro seleccionado no tiene stock disponible.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Deseas solicitar el préstamo del libro \"" + titulo + "\"?",
                "Confirmar préstamo",
                JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            controlador.solicitarPrestamo(usuario.getRut(), idLibro);
        }
    }

    private void accionarDevolucion() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            mostrarError("Selecciona un préstamo de tu historial.");
            return;
        }
        int fila = tabla.convertRowIndexToModel(vista);
        int idPrestamo = (Integer) modelo.getValueAt(fila, 0);
        String tituloLibro = String.valueOf(modelo.getValueAt(fila, 1));
        String estadoPrestamo = String.valueOf(modelo.getValueAt(fila, 4));

        if ("Devuelto".equalsIgnoreCase(estadoPrestamo) || "Sí".equalsIgnoreCase(estadoPrestamo)) {
            mostrarError("Este préstamo ya fue devuelto.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this,
                "¿Deseas devolver el libro \"" + tituloLibro + "\" (Préstamo #" + idPrestamo + ")?",
                "Confirmar devolución",
                JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            controlador.devolverPrestamo(idPrestamo, usuario.getRut());
        }
    }
}
