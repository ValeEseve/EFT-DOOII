package controller;

import dao.EstudianteDAO;
import dao.LibroDAO;
import dao.ReporteDAO;
import model.Estudiante;
import model.Libro;
import service.PrestamoService;
import view.CatalogoFrame;

import javax.swing.SwingWorker;
import java.util.ArrayList;

public class CatalogoController {
    private final CatalogoFrame vista;
    private final LibroDAO libroDAO;
    private final ReporteDAO reporteDAO;
    private final EstudianteDAO estudianteDAO;
    private final PrestamoService prestamoService;

    public CatalogoController(CatalogoFrame vista) {
        this(vista, new LibroDAO(), new ReporteDAO(), new EstudianteDAO(), new PrestamoService());
    }

    public CatalogoController(CatalogoFrame vista, LibroDAO libroDAO, ReporteDAO reporteDAO) {
        this(vista, libroDAO, reporteDAO, new EstudianteDAO(), new PrestamoService());
    }

    public CatalogoController(CatalogoFrame vista, LibroDAO libroDAO, ReporteDAO reporteDAO,
                              EstudianteDAO estudianteDAO, PrestamoService prestamoService) {
        this.vista = vista;
        this.libroDAO = libroDAO;
        this.reporteDAO = reporteDAO;
        this.estudianteDAO = estudianteDAO;
        this.prestamoService = prestamoService;
    }

    public void cargarCatalogo() {
        vista.habilitar(false);
        vista.setEstado("Cargando catálogo...");

        new SwingWorker<ArrayList<Libro>, Void>() {
            @Override
            protected ArrayList<Libro> doInBackground() {
                return libroDAO.listarLibros();
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Libro> libros = get();
                    vista.mostrarCatalogo(libros);
                    vista.setEstado("Libros en catálogo: " + libros.size());
                } catch (Exception ex) {
                    vista.mostrarError("No se pudo cargar el catálogo.");
                } finally {
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void cargarHistorial(String rut) {
        vista.habilitar(false);
        vista.setEstado("Consultando préstamos...");

        new SwingWorker<ArrayList<Object[]>, Void>() {
            @Override
            protected ArrayList<Object[]> doInBackground() throws Exception {
                return reporteDAO.historialPorRut(rut);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Object[]> filas = get();
                    vista.mostrarHistorial(filas);
                    vista.setEstado("Préstamos encontrados: " + filas.size() + " (según RUT de estudiante)");
                } catch (Exception ex) {
                    vista.mostrarError("No se pudo consultar el historial.");
                } finally {
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void solicitarPrestamo(String rutEstudiante, int idLibro) {
        vista.habilitar(false);
        vista.setEstado("Procesando solicitud de préstamo...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Estudiante estudiante = estudianteDAO.buscarPorRut(rutEstudiante);
                if (estudiante == null) {
                    throw new IllegalStateException("No se encontró un estudiante asociado al RUT: " + rutEstudiante);
                }
                return prestamoService.prestarLibro(estudiante.getId(), idLibro);
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (Boolean.TRUE.equals(exito)) {
                        vista.mostrarMensaje("Préstamo realizado con éxito.");
                        cargarCatalogo();
                    } else {
                        vista.mostrarError("No se pudo completar el préstamo. Verifica la disponibilidad del libro.");
                        vista.habilitar(true);
                    }
                } catch (Exception ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    if (mensaje == null || mensaje.isBlank()) {
                        mensaje = "Ocurrió un error al procesar el préstamo.";
                    }
                    vista.mostrarError(mensaje);
                    vista.habilitar(true);
                }
            }
        }.execute();
    }

    public void devolverPrestamo(int idPrestamo, String rutEstudiante) {
        vista.habilitar(false);
        vista.setEstado("Procesando devolución...");

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return prestamoService.devolverLibro(idPrestamo);
            }

            @Override
            protected void done() {
                try {
                    Boolean exito = get();
                    if (Boolean.TRUE.equals(exito)) {
                        vista.mostrarMensaje("Devolución realizada con éxito.");
                        cargarHistorial(rutEstudiante);
                    } else {
                        vista.mostrarError("No se pudo completar la devolución. Verifica el estado del préstamo.");
                        vista.habilitar(true);
                    }
                } catch (Exception ex) {
                    String mensaje = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    if (mensaje == null || mensaje.isBlank()) {
                        mensaje = "Ocurrió un error al procesar la devolución.";
                    }
                    vista.mostrarError(mensaje);
                    vista.habilitar(true);
                }
            }
        }.execute();
    }
}
