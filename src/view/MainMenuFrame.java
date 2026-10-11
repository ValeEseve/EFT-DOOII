package view;

import model.Usuario;
import javax.swing.*;
import java.awt.*;

public class MainMenuFrame extends JFrame {
    private final Usuario usuario;

    public MainMenuFrame(Usuario usuario) {
        this.usuario = usuario;
        setTitle("Biblioteca Escolar - Menú principal");
        setSize(590, 460);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        crearComponentes();
    }

    private void crearComponentes() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        JPanel encabezado = new JPanel(new GridLayout(3, 1, 4, 4));
        JLabel titulo = new JLabel("Biblioteca Escolar", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        encabezado.add(titulo);
        encabezado.add(new JLabel("Bienvenido/a, " + usuario.getNombre(), SwingConstants.CENTER));
        encabezado.add(new JLabel("Rol: " + usuario.getRol(), SwingConstants.CENTER));
        panel.add(encabezado, BorderLayout.NORTH);

        JPanel acciones = new JPanel(new GridLayout(0, 1, 10, 10));
        if ("bibliotecario".equals(usuario.getRol())) {
            JButton libros = new JButton("Gestionar libros");
            JButton estudiantes = new JButton("Gestionar estudiantes");
            JButton prestamos = new JButton("Préstamos y devoluciones");
            JButton reportes = new JButton("Reportes");
            acciones.add(libros); acciones.add(estudiantes); acciones.add(prestamos); acciones.add(reportes);
            libros.addActionListener(e -> new LibroFrame().setVisible(true));
            estudiantes.addActionListener(e -> new EstudianteFrame(usuario).setVisible(true));
            prestamos.addActionListener(e -> new PrestamoFrame(usuario).setVisible(true));
            reportes.addActionListener(e -> new ReporteFrame(usuario).setVisible(true));
        } else if ("estudiante".equals(usuario.getRol())) {
            acciones.setLayout(new GridBagLayout());
            JButton catalogo = new JButton("Ver catálogo y mis préstamos");
            catalogo.setPreferredSize(new Dimension(300, 42));
            acciones.add(catalogo);

            catalogo.addActionListener(e -> new CatalogoFrame(usuario).setVisible(true));
        } else {
            acciones.add(new JLabel("Rol no reconocido.", SwingConstants.CENTER));
        }
        panel.add(acciones, BorderLayout.CENTER);

        JButton salir = new JButton("Cerrar sesión");
        salir.addActionListener(e -> {
            // Cerrar ventanas secundarias antes de regresar al login
            for (Window ventana : Window.getWindows()) {
                if (ventana instanceof LibroFrame || ventana instanceof EstudianteFrame
                        || ventana instanceof PrestamoFrame || ventana instanceof ReporteFrame
                        || ventana instanceof CatalogoFrame) ventana.dispose();
            }
            new LoginFrame().setVisible(true);
            dispose();
        });
        JPanel pie = new JPanel();
        pie.add(salir);
        panel.add(pie, BorderLayout.SOUTH);
        add(panel);
    }
}
