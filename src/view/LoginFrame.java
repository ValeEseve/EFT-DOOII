
package view;

import controller.LoginController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class LoginFrame extends JFrame {

    private JTextField campoRut;
    private JPasswordField campoContraseña;
    private JButton botonIngresar;
    private JLabel etiquetaEstado;

    private final LoginController controlador;

    public LoginFrame() {

        this.controlador = new LoginController(this);

        setTitle("Biblioteca Escolar - Inicio de sesión");
        setSize(430, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        crearComponentes();
    }

    private void crearComponentes() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.gridx = 0;
        gbc.gridwidth = 2;

        JLabel titulo = new JLabel(
                "Biblioteca Escolar",
                SwingConstants.CENTER
        );

        titulo.setFont(new Font("SansSerif", Font.BOLD, 23));

        gbc.gridy = 0;
        panel.add(titulo, gbc);

        JLabel subtitulo = new JLabel(
                "Inicia sesión para continuar",
                SwingConstants.CENTER
        );

        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        gbc.gridwidth = 1;

        JLabel etiquetaRut = new JLabel("RUT:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(etiquetaRut, gbc);

        campoRut = new JTextField(18);
        gbc.gridx = 1;
        panel.add(campoRut, gbc);

        JLabel etiquetaContraseña =
                new JLabel("Contraseña:");

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(etiquetaContraseña, gbc);

        campoContraseña = new JPasswordField(18);
        gbc.gridx = 1;
        panel.add(campoContraseña, gbc);

        botonIngresar = new JButton("Ingresar");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        panel.add(botonIngresar, gbc);

        etiquetaEstado = new JLabel(
                " ",
                SwingConstants.CENTER
        );

        gbc.gridy = 5;
        panel.add(etiquetaEstado, gbc);

        botonIngresar.addActionListener(e -> iniciarSesion());

        getRootPane().setDefaultButton(botonIngresar);

        add(panel);
    }

    private void iniciarSesion() {

        String rut = campoRut.getText().trim();

        char[] caracteres = campoContraseña.getPassword();

        if (rut.isEmpty() || caracteres.length == 0) {

            Arrays.fill(caracteres, '\0');

            JOptionPane.showMessageDialog(
                    this,
                    "Debes ingresar tu RUT y contraseña.",
                    "Campos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String contraseña = new String(caracteres);
        Arrays.fill(caracteres, '\0');

        controlador.iniciarSesion(rut, contraseña);
    }

    public void habilitarBoton(boolean habilitado) {
        botonIngresar.setEnabled(habilitado);
    }

    public void setEstado(String texto) {
        etiquetaEstado.setText(texto);
    }

    public void limpiarContrasena() {
        campoContraseña.setText("");
    }

    public void mostrarErrorAutenticacion(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void mostrarError(String mensaje, String titulo) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                titulo,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
