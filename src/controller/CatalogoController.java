package controller;

import dao.LibroDAO;
import dao.ReporteDAO;
import model.Libro;
import view.CatalogoFrame;

import javax.swing.SwingWorker;
import java.util.ArrayList;

public class CatalogoController {
    private final CatalogoFrame vista;
    private final LibroDAO libroDAO;
    private final ReporteDAO reporteDAO;

    public CatalogoController(CatalogoFrame vista) {
        this(vista, new LibroDAO(), new ReporteDAO());
    }

    public CatalogoController(CatalogoFrame vista, LibroDAO libroDAO, ReporteDAO reporteDAO) {
        this.vista = vista;
        this.libroDAO = libroDAO;
        this.reporteDAO = reporteDAO;
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
}
