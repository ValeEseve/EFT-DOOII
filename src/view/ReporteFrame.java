package view;

import controller.ReporteController;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class ReporteFrame extends JFrame {
    private final ReporteController controlador;
    private final JComboBox<String> tipos = new JComboBox<>(new String[]{
            "Libros más prestados", "Préstamos activos", "Historial por estudiante"
    });
    private final JComboBox<EstudianteOpcion> estudiantes = new JComboBox<>();
    private final JButton consultar = new JButton("Consultar");
    private final DefaultTableModel modelo = new DefaultTableModel() {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JLabel estado = new JLabel("Selecciona un reporte.");

    private static class EstudianteOpcion {
        final int id;
        final String nombre;
        EstudianteOpcion(int id, String nombre) { this.id = id; this.nombre = nombre; }
        @Override public String toString() { return nombre; }
    }

    public ReporteFrame(Usuario usuario) {
        if (!"bibliotecario".equals(usuario.getRol()))
            throw new SecurityException("Acceso exclusivo para bibliotecarios.");
        this.controlador = new ReporteController(this);

        setTitle("Biblioteca Escolar - Reportes");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel titulo = new JLabel("Reportes de biblioteca", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel sur = new JPanel(new BorderLayout());
        JPanel controles = new JPanel(new FlowLayout());
        controles.add(tipos); controles.add(estudiantes); controles.add(consultar);
        sur.add(controles, BorderLayout.NORTH);
        sur.add(estado, BorderLayout.SOUTH);
        add(sur, BorderLayout.SOUTH);
        tipos.addActionListener(e -> actualizarControles());
        consultar.addActionListener(e -> {
            int tipo = tipos.getSelectedIndex();
            EstudianteOpcion seleccionado = (EstudianteOpcion) estudiantes.getSelectedItem();
            controlador.cargarReporte(tipo, seleccionado != null ? seleccionado.id : -1);
        });
        actualizarControles();
        controlador.cargarEstudiantes();
    }

    private void actualizarControles() {
        estudiantes.setEnabled(tipos.getSelectedIndex() == 2);
    }

    public void habilitarConsultar(boolean valor) {
        consultar.setEnabled(valor);
    }

    public void setEstado(String texto) {
        estado.setText(texto);
    }

    public void poblarEstudiantes(ArrayList<Estudiante> lista) {
        estudiantes.removeAllItems();
        if (lista != null) {
            for (Estudiante e : lista) {
                estudiantes.addItem(new EstudianteOpcion(e.getId(), e.getNombre()));
            }
        }
    }

    public void mostrarReporte(String[] columnas, ArrayList<Object[]> filas) {
        modelo.setColumnIdentifiers(columnas);
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
