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
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel inferior = new JPanel(new BorderLayout());
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(catalogo); botones.add(historial);
        inferior.add(botones, BorderLayout.NORTH);
        inferior.add(estado, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);
        catalogo.addActionListener(e -> controlador.cargarCatalogo());
        historial.addActionListener(e -> controlador.cargarHistorial(usuario.getRut()));
        controlador.cargarCatalogo();
    }

    public void habilitar(boolean valor) {
        catalogo.setEnabled(valor);
        historial.setEnabled(valor);
    }

    public void setEstado(String texto) {
        estado.setText(texto);
    }

    public void mostrarCatalogo(ArrayList<Libro> libros) {
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
        modelo.setColumnIdentifiers(new String[]{"ID", "Libro", "Préstamo", "Vencimiento", "Estado"});
        modelo.setRowCount(0);
        if (filas != null) {
            for (Object[] fila : filas) {
                modelo.addRow(fila);
            }
        }
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
