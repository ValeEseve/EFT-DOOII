package controller;

import dao.EstudianteDAO;
import dao.ReporteDAO;
import model.Estudiante;
import view.ReporteFrame;

import javax.swing.SwingWorker;
import java.util.ArrayList;

public class ReporteController {
    private final ReporteFrame vista;
    private final ReporteDAO reporteDAO;
    private final EstudianteDAO estudianteDAO;

    public ReporteController(ReporteFrame vista) {
        this(vista, new ReporteDAO(), new EstudianteDAO());
    }

    public ReporteController(ReporteFrame vista, ReporteDAO reporteDAO, EstudianteDAO estudianteDAO) {
        this.vista = vista;
        this.reporteDAO = reporteDAO;
        this.estudianteDAO = estudianteDAO;
    }

    public void cargarEstudiantes() {
        vista.habilitarConsultar(false);
        new SwingWorker<ArrayList<Estudiante>, Void>() {
            @Override
            protected ArrayList<Estudiante> doInBackground() {
                return estudianteDAO.listarEstudiantes();
            }

            @Override
            protected void done() {
                try {
                    vista.poblarEstudiantes(get());
                } catch (Exception ex) {
                    vista.mostrarError("No se pudieron cargar los estudiantes.");
                } finally {
                    vista.habilitarConsultar(true);
                    cargarReporte(0, -1);
                }
            }
        }.execute();
    }

    public void cargarReporte(int tipo, int idEstudiante) {
        if (tipo == 2 && idEstudiante <= 0) {
            vista.setEstado("No hay estudiante seleccionado.");
            return;
        }

        vista.habilitarConsultar(false);
        vista.setEstado("Consultando reporte...");

        new SwingWorker<ArrayList<Object[]>, Void>() {
            @Override
            protected ArrayList<Object[]> doInBackground() throws Exception {
                if (tipo == 0) {
                    return reporteDAO.librosMasPrestados();
                } else if (tipo == 1) {
                    return reporteDAO.prestamosActivos();
                } else {
                    return reporteDAO.historialEstudiante(idEstudiante);
                }
            }

            @Override
            protected void done() {
                try {
                    String[] columnas;
                    if (tipo == 0) {
                        columnas = new String[]{"Libro", "Total de préstamos"};
                    } else if (tipo == 1) {
                        columnas = new String[]{"ID", "Estudiante", "Libro", "Préstamo", "Vencimiento"};
                    } else {
                        columnas = new String[]{"ID", "Libro", "Préstamo", "Vencimiento", "Estado"};
                    }

                    ArrayList<Object[]> filas = get();
                    vista.mostrarReporte(columnas, filas);
                    vista.setEstado("Filas encontradas: " + filas.size());
                } catch (Exception ex) {
                    vista.mostrarError("Error al consultar el reporte.");
                } finally {
                    vista.habilitarConsultar(true);
                }
            }
        }.execute();
    }
}
