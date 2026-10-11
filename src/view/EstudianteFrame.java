package view;

import controller.EstudianteController;
import model.Estudiante;
import model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class EstudianteFrame extends JFrame {
    private final EstudianteController controlador;

    private final DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"ID", "Nombre", "RUT", "Curso", "Correo"}, 0) {
        @Override public boolean isCellEditable(int fila, int columna) { return false; }
    };
    private final JTable tabla = new JTable(modelo);
    private final JTextField nombre = new JTextField();
    private final JTextField rut = new JTextField();
    private final JTextField curso = new JTextField();
    private final JTextField correo = new JTextField();
    private final JButton agregar = new JButton("Agregar");
    private final JButton actualizar = new JButton("Actualizar");
    private final JButton eliminar = new JButton("Eliminar");
    private final JButton limpiar = new JButton("Limpiar");
    private final JButton recargar = new JButton("Recargar");
    private final JLabel estado = new JLabel("Selecciona un estudiante o agrega uno nuevo.");
    private int seleccionado = -1;

    public EstudianteFrame(Usuario usuario) {
        if (!"bibliotecario".equals(usuario.getRol())) {
            throw new SecurityException("Acceso exclusivo para bibliotecarios.");
        }
        this.controlador = new EstudianteController(this);

        setTitle("Biblioteca Escolar - Estudiantes");
        setSize(900, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel("Gestión de estudiantes", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarFila();
        });
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(8, 8));
        JPanel formulario = new JPanel(new GridLayout(2, 4, 8, 6));
        formulario.add(new JLabel("Nombre")); formulario.add(nombre);
        formulario.add(new JLabel("RUT")); formulario.add(rut);
        formulario.add(new JLabel("Curso")); formulario.add(curso);
        formulario.add(new JLabel("Correo")); formulario.add(correo);
        inferior.add(formulario, BorderLayout.NORTH);
        JPanel acciones = new JPanel(new FlowLayout());
        for (JButton boton : new JButton[]{agregar, actualizar, eliminar, limpiar, recargar}) acciones.add(boton);
        inferior.add(acciones, BorderLayout.CENTER);
        inferior.add(estado, BorderLayout.SOUTH);
        add(inferior, BorderLayout.SOUTH);

        agregar.addActionListener(e -> guardar(false));
        actualizar.addActionListener(e -> guardar(true));
        eliminar.addActionListener(e -> borrar());
        limpiar.addActionListener(e -> limpiarFormulario());
        recargar.addActionListener(e -> controlador.cargarEstudiantes());
        controlador.cargarEstudiantes();
    }

    public void habilitar(boolean valor) {
        for (JButton b : new JButton[]{agregar, actualizar, eliminar, limpiar, recargar}) b.setEnabled(valor);
        tabla.setEnabled(valor);
    }

    public void setEstado(String texto) {
        estado.setText(texto);
    }

    public void poblarEstudiantes(ArrayList<Estudiante> datos) {
        modelo.setRowCount(0);
        if (datos != null) {
            for (Estudiante e : datos) {
                modelo.addRow(new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()});
            }
        }
    }

    private void seleccionarFila() {
        int vista = tabla.getSelectedRow();
        if (vista < 0) return;
        int fila = tabla.convertRowIndexToModel(vista);
        seleccionado = (Integer) modelo.getValueAt(fila, 0);
        nombre.setText(valor(fila, 1));
        rut.setText(valor(fila, 2));
        curso.setText(valor(fila, 3));
        correo.setText(valor(fila, 4));
        estado.setText("Estudiante seleccionado: " + seleccionado);
    }

    private String valor(int fila, int columna) {
        Object dato = modelo.getValueAt(fila, columna);
        return dato == null ? "" : dato.toString();
    }

    private Estudiante leer() {
        String n = nombre.getText().trim(), r = rut.getText().trim();
        String c = curso.getText().trim(), m = correo.getText().trim();
        if (n.isEmpty() || r.isEmpty() || c.isEmpty() || m.isEmpty()) {
            mostrarError("Completa todos los campos."); return null;
        }
        if (n.length() > 100 || r.length() > 12 || c.length() > 20 || m.length() > 100) {
            mostrarError("Uno de los campos supera el largo permitido."); return null;
        }
        return new Estudiante(seleccionado, n, r, m, c);
    }

    private void guardar(boolean esActualizacion) {
        if (esActualizacion && seleccionado < 0) { mostrarError("Selecciona un estudiante."); return; }
        Estudiante estudiante = leer();
        if (estudiante == null) return;
        if (esActualizacion) {
            controlador.actualizarEstudiante(estudiante);
        } else {
            controlador.agregarEstudiante(estudiante);
        }
    }

    private void borrar() {
        if (seleccionado < 0) { mostrarError("Selecciona un estudiante."); return; }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar este estudiante?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            controlador.eliminarEstudiante(seleccionado);
        }
    }

    public void limpiarFormulario() {
        seleccionado = -1;
        tabla.clearSelection();
        nombre.setText(""); rut.setText(""); curso.setText(""); correo.setText("");
        estado.setText("Formulario limpio.");
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
