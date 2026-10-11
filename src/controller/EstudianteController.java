package controller;

import dao.EstudianteDAO;
import model.Estudiante;
import view.EstudianteFrame;

import javax.swing.SwingWorker;
import java.util.ArrayList;

public class EstudianteController {
    private final EstudianteFrame vista;
    private final EstudianteDAO estudianteDAO;

    public EstudianteController(EstudianteFrame vista) {
        this(vista, new EstudianteDAO());
    }

    public EstudianteController(EstudianteFrame vista, EstudianteDAO estudianteDAO) {
        this.vista = vista;
        this.estudianteDAO = estudianteDAO;
    }

    public void cargarEstudiantes() {
        vista.habilitar(false);
        vista.setEstado("Cargando estudiantes...");

        new SwingWorker<ArrayList<Estudiante>, Void>() {
            @Override
            protected ArrayList<Estudiante> doInBackground() {
                return estudianteDAO.listarEstudiantes();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Estudiante> datos = get();
                    vista.poblarEstudiantes(datos);
                    vista.setEstado("Estudiantes cargados: " + datos.size());
                } catch (Exception ex) {
                    vista.mostrarError("No se pudieron cargar los estudiantes.");
                } finally {
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void agregarEstudiante(Estudiante estudiante) {
        ejecutarOperacion("agregar", () -> estudianteDAO.agregarEstudiante(estudiante));
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        ejecutarOperacion("actualizar", () -> estudianteDAO.actualizarEstudiante(estudiante));
    }

    public void eliminarEstudiante(int id) {
        ejecutarOperacion("eliminar", () -> estudianteDAO.eliminarEstudiante(id));
    }

    @FunctionalInterface
    private interface OperacionPersistencia {
        boolean ejecutar();
    }

    private void ejecutarOperacion(String accion, OperacionPersistencia operacion) {
        vista.habilitar(false);
        vista.setEstado("Procesando " + accion + "...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return operacion.ejecutar();
            }

            @Override
            protected void done() {
                try {
                    if (Boolean.TRUE.equals(get())) {
                        vista.limpiarFormulario();
                        vista.mostrarMensaje("Operación realizada correctamente.");
                        cargarEstudiantes();
                    } else {
                        vista.mostrarError("No se pudo realizar la operación. Revisa RUT único y préstamos asociados.");
                    }
                } catch (Exception ex) {
                    vista.mostrarError("Error al procesar la operación.");
                } finally {
                    vista.habilitar(true);
                }
            }
        }.execute();
    }
}
