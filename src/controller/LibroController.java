package controller;

import dao.LibroDAO;
import model.Libro;
import view.LibroFrame;

import javax.swing.SwingWorker;
import java.util.ArrayList;

public class LibroController {
    private final LibroFrame vista;
    private final LibroDAO libroDAO;

    public LibroController(LibroFrame vista) {
        this(vista, new LibroDAO());
    }

    public LibroController(LibroFrame vista, LibroDAO libroDAO) {
        this.vista = vista;
        this.libroDAO = libroDAO;
    }

    public void cargarLibros() {
        vista.cambiarEstadoBotones(false);
        vista.setEstado("Cargando libros...");

        new SwingWorker<ArrayList<Libro>, Void>() {
            @Override
            protected ArrayList<Libro> doInBackground() {
                return libroDAO.listarLibros();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Libro> libros = get();
                    vista.limpiarFormulario();
                    vista.poblarLibros(libros);
                    vista.setEstado("Libros cargados: " + libros.size());
                } catch (Exception e) {
                    vista.mostrarError("No se pudieron cargar los libros.");
                    vista.setEstado("Error al cargar libros.");
                } finally {
                    vista.cambiarEstadoBotones(true);
                }
            }
        }.execute();
    }

    public void agregarLibro(Libro libro) {
        ejecutarOperacion("agregar", () -> libroDAO.agregarLibro(libro));
    }

    public void actualizarLibro(Libro libro) {
        ejecutarOperacion("actualizar", () -> libroDAO.actualizarLibro(libro));
    }

    public void eliminarLibro(int id) {
        ejecutarOperacion("eliminar", () -> libroDAO.eliminarLibro(id));
    }

    @FunctionalInterface
    private interface OperacionPersistencia {
        boolean ejecutar();
    }

    private void ejecutarOperacion(String operacion, OperacionPersistencia accion) {
        vista.cambiarEstadoBotones(false);
        vista.setEstado("Procesando " + operacion + "...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return accion.ejecutar();
            }

            @Override
            protected void done() {
                boolean correcto = false;
                try {
                    correcto = get();
                } catch (Exception e) {
                    vista.mostrarError("Ocurrió un error al procesar la operación.");
                }

                vista.cambiarEstadoBotones(true);

                if (correcto) {
                    vista.mostrarMensaje("Operación realizada correctamente.");
                    vista.limpiarFormulario();
                    cargarLibros();
                } else {
                    vista.mostrarError("No se pudo realizar la operación.\nComprueba los datos y las relaciones del libro.");
                    vista.setEstado("Operación no realizada.");
                }
            }
        }.execute();
    }
}
