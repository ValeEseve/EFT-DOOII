package controller;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.PrestamoDAO;
import model.Estudiante;
import model.Libro;
import model.Prestamo;
import service.PrestamoService;
import view.PrestamoFrame;

import javax.swing.*;
import java.util.ArrayList;

public class PrestamoController {
    private final PrestamoFrame vista;
    private final PrestamoService servicio;
    private final EstudianteDAO estudianteDAO;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;

    private static class DatosCarga {
        final ArrayList<Estudiante> estudiantes;
        final ArrayList<Libro> libros;
        final ArrayList<Prestamo> prestamos;

        DatosCarga(ArrayList<Estudiante> estudiantes, ArrayList<Libro> libros, ArrayList<Prestamo> prestamos) {
            this.estudiantes = estudiantes;
            this.libros = libros;
            this.prestamos = prestamos;
        }
    }

    public PrestamoController(PrestamoFrame vista) {
        this(vista, new PrestamoService(), new EstudianteDAO(), new LibroDAO(), new PrestamoDAO());
    }

    public PrestamoController(PrestamoFrame vista, PrestamoService servicio,
                              EstudianteDAO estudianteDAO, LibroDAO libroDAO, PrestamoDAO prestamoDAO) {
        this.vista = vista;
        this.servicio = servicio;
        this.estudianteDAO = estudianteDAO;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
    }

    public void cargarDatos() {
        vista.habilitar(false);
        vista.setEstado("Cargando datos...");
        new SwingWorker<DatosCarga, Void>() {
            @Override
            protected DatosCarga doInBackground() {
                ArrayList<Estudiante> estudiantes = estudianteDAO.listarEstudiantes();
                ArrayList<Libro> libros = libroDAO.listarLibros();
                ArrayList<Prestamo> prestamos = prestamoDAO.listarPrestamos();
                return new DatosCarga(estudiantes, libros, prestamos);
            }

            @Override
            protected void done() {
                try {
                    DatosCarga datos = get();
                    vista.poblarEstudiantes(datos.estudiantes);
                    vista.poblarLibros(datos.libros);
                    vista.poblarPrestamos(datos.prestamos);
                    vista.setEstado("Préstamos cargados: " + datos.prestamos.size());
                } catch (Exception ex) {
                    vista.mostrarError("No se pudieron cargar los datos.");
                } finally {
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void registrarPrestamo(int idEstudiante, int idLibro) {
        vista.habilitar(false);
        vista.setEstado("Procesando préstamo...");
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return servicio.prestarLibro(idEstudiante, idLibro);
            }

            @Override
            protected void done() {
                try {
                    if (Boolean.TRUE.equals(get())) {
                        vista.mostrarMensaje("Operación exitosa.");
                        cargarDatos();
                    } else {
                        vista.mostrarError("No se pudo completar el préstamo. Verifica disponibilidad y datos.");
                        vista.habilitar(true);
                    }
                } catch (Exception ex) {
                    vista.mostrarError("Ocurrió un error al ejecutar la operación.");
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void registrarDevolucion(int idPrestamo) {
        vista.habilitar(false);
        vista.setEstado("Procesando devolución...");
        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return servicio.devolverLibro(idPrestamo);
            }

            @Override
            protected void done() {
                try {
                    if (Boolean.TRUE.equals(get())) {
                        vista.mostrarMensaje("Operación exitosa.");
                        cargarDatos();
                    } else {
                        vista.mostrarError("No se pudo completar la devolución. Verifica disponibilidad y datos.");
                        vista.habilitar(true);
                    }
                } catch (Exception ex) {
                    vista.mostrarError("Ocurrió un error al ejecutar la operación.");
                    vista.habilitar(true);
                }
            }
        }.execute();
    }
}
