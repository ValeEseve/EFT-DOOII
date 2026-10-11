package view;

import controller.PrestamoController;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class PrestamoFrame extends JFrame {
    private final PrestamoController controlador;
    private final JComboBox<Opcion> estudiantes = new JComboBox<>();
    private final JComboBox<Opcion> libros = new JComboBox<>();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Estudiante ID", "Libro ID", "Fecha préstamo", "Vencimiento", "Devuelto"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton prestar = new JButton("Registrar préstamo");
    private final JButton devolver = new JButton("Devolver seleccionado");
    private final JButton recargar = new JButton("Recargar");
    private final JLabel estado = new JLabel("Selecciona estudiante y libro.");

    private static class Opcion {
        final int id;
        final String texto;
        Opcion(int id, String texto) { this.id = id; this.texto = texto; }
        @Override public String toString() { return texto; }
    }

    public PrestamoFrame(Usuario usuario) {
        if (!"bibliotecario".equals(usuario.getRol())) {
            throw new SecurityException("Acceso exclusivo para bibliotecarios.");
        }
        this.controlador = new PrestamoController(this);

        setTitle("Biblioteca Escolar - Préstamos y devoluciones");
        setSize(1000, 570);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Préstamos y devoluciones", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout(6, 6));
        JPanel formulario = new JPanel(new GridLayout(2, 2, 8, 6));
        formulario.add(new JLabel("Estudiante:")); formulario.add(new JLabel("Libro (stock disponible):"));
        formulario.add(estudiantes); formulario.add(libros);
        sur.add(formulario, BorderLayout.NORTH);
        JPanel botones = new JPanel(new FlowLayout());
        botones.add(prestar); botones.add(devolver); botones.add(recargar);
        sur.add(botones, BorderLayout.CENTER);
        sur.add(estado, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);

        prestar.addActionListener(e -> registrarPrestamo());
        devolver.addActionListener(e -> registrarDevolucion());
        recargar.addActionListener(e -> controlador.cargarDatos());
        controlador.cargarDatos();
    }

    public void habilitar(boolean valor) {
        prestar.setEnabled(valor); devolver.setEnabled(valor); recargar.setEnabled(valor);
        estudiantes.setEnabled(valor); libros.setEnabled(valor); tabla.setEnabled(valor);
    }

    public void setEstado(String texto) {
        estado.setText(texto);
    }

    public void poblarEstudiantes(ArrayList<Estudiante> lista) {
        estudiantes.removeAllItems();
        if (lista != null) {
            for (Estudiante e : lista) {
                estudiantes.addItem(new Opcion(e.getId(), e.getNombre() + " (" + e.getRut() + ")"));
            }
        }
    }

    public void poblarLibros(ArrayList<Libro> lista) {
        libros.removeAllItems();
        if (lista != null) {
            for (Libro l : lista) {
                if (l.getStock() > 0) {
                    libros.addItem(new Opcion(l.getId(), l.getTitulo() + " (" + l.getStock() + ")"));
                }
            }
        }
    }

    public void poblarPrestamos(ArrayList<Prestamo> lista) {
        modelo.setRowCount(0);
        if (lista != null) {
            for (Prestamo p : lista) {
                modelo.addRow(new Object[]{p.getId(), p.getIdEstudiante(), p.getIdLibro(),
                        p.getFechaPrestamo(), p.getFechaDevolucion(), p.isDevuelto() ? "Sí" : "No"});
            }
        }
    }

    public void mostrarMensaje(String texto) {
        JOptionPane.showMessageDialog(this, texto);
    }

    public void mostrarError(String texto) {
        JOptionPane.showMessageDialog(this, texto, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void registrarPrestamo() {
        Opcion estudiante = (Opcion) estudiantes.getSelectedItem();
        Opcion libro = (Opcion) libros.getSelectedItem();
        if (estudiante == null || libro == null) {
            mostrarError("Selecciona estudiante y libro disponible.");
            return;
        }
        controlador.registrarPrestamo(estudiante.id, libro.id);
    }

    private void registrarDevolucion() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) {
            mostrarError("Selecciona un préstamo de la tabla.");
            return;
        }
        int fila = tabla.convertRowIndexToModel(vista);
        int id = (Integer) modelo.getValueAt(fila, 0);
        if ("Sí".equals(modelo.getValueAt(fila, 5))) {
            mostrarError("Este préstamo ya fue devuelto.");
            return;
        }
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Registrar la devolución del préstamo " + id + "?",
                "Confirmar devolución", JOptionPane.YES_NO_OPTION);
        if (confirmar == JOptionPane.YES_OPTION) {
            controlador.registrarDevolucion(id);
        }
    }
}
