package controller;

import dao.AuthDAO;
import model.Usuario;
import view.LoginFrame;
import view.MainMenuFrame;

import javax.swing.SwingWorker;

public class LoginController {
    private final LoginFrame vista;
    private final AuthDAO authDAO;

    public LoginController(LoginFrame vista) {
        this(vista, new AuthDAO());
    }

    public LoginController(LoginFrame vista, AuthDAO authDAO) {
        this.vista = vista;
        this.authDAO = authDAO;
    }

    public void iniciarSesion(String rut, String contrasena) {
        vista.habilitarBoton(false);
        vista.setEstado("Verificando credenciales...");

        new SwingWorker<Usuario, Void>() {
            @Override
            protected Usuario doInBackground() {
                return authDAO.iniciarSesion(rut, contrasena);
            }

            @Override
            protected void done() {
                try {
                    Usuario usuario = get();
                    if (usuario != null) {
                        MainMenuFrame menu = new MainMenuFrame(usuario);
                        menu.setVisible(true);
                        vista.dispose();
                    } else {
                        vista.mostrarErrorAutenticacion(
                                "RUT o contraseña incorrectos.",
                                "Error de inicio de sesión"
                        );
                    }
                } catch (Exception e) {
                    vista.mostrarError(
                            "No se pudo completar el inicio de sesión.",
                            "Error"
                    );
                } finally {
                    vista.limpiarContrasena();
                    vista.habilitarBoton(true);
                    vista.setEstado(" ");
                }
            }
        }.execute();
    }
}
