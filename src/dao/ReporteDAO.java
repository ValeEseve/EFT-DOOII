package dao;

import config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;

public class ReporteDAO {
    private ArrayList<Object[]> consultar(String sql, Integer idEstudiante) throws SQLException {
        ArrayList<Object[]> filas = new ArrayList<>();
        try (Connection conexion = DatabaseConnection.getInstance().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            if (idEstudiante != null) ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                int columnas = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] fila = new Object[columnas];
                    for (int i = 0; i < columnas; i++) fila[i] = rs.getObject(i + 1);
                    filas.add(fila);
                }
            }
        }
        return filas;
    }

    public ArrayList<Object[]> librosMasPrestados() throws SQLException {
        String sql = "SELECT l.titulo AS libro, COUNT(p.id) AS total " +
                "FROM libros l LEFT JOIN prestamos p ON p.id_libro = l.id " +
                "GROUP BY l.id, l.titulo ORDER BY total DESC, l.titulo";
        return consultar(sql, null);
    }

    public ArrayList<Object[]> prestamosActivos() throws SQLException {
        String sql = "SELECT p.id, e.nombre AS estudiante, l.titulo AS libro, " +
                "p.fecha_prestamo, p.fecha_devolucion AS vencimiento " +
                "FROM prestamos p JOIN estudiantes e ON e.id = p.id_estudiante " +
                "JOIN libros l ON l.id = p.id_libro " +
                "WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion, p.id";
        return consultar(sql, null);
    }

    public ArrayList<Object[]> historialEstudiante(int idEstudiante) throws SQLException {
        String sql = "SELECT p.id, l.titulo AS libro, p.fecha_prestamo, " +
                "p.fecha_devolucion AS vencimiento, " +
                "CASE WHEN p.devuelto THEN 'Devuelto' ELSE 'Activo' END AS estado " +
                "FROM prestamos p JOIN libros l ON l.id = p.id_libro " +
                "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC, p.id DESC";
        return consultar(sql, idEstudiante);
    }

    public ArrayList<Object[]> historialPorRut(String rut) throws SQLException {
        String sql = "SELECT p.id, l.titulo AS libro, p.fecha_prestamo, " +
                "p.fecha_devolucion AS vencimiento, " +
                "CASE WHEN p.devuelto THEN 'Devuelto' ELSE 'Activo' END AS estado " +
                "FROM prestamos p JOIN libros l ON l.id = p.id_libro " +
                "JOIN estudiantes e ON e.id = p.id_estudiante " +
                "WHERE e.rut = ? ORDER BY p.fecha_prestamo DESC, p.id DESC";
        ArrayList<Object[]> filas = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().conectar();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, rut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) filas.add(new Object[]{rs.getInt(1), rs.getString(2),
                        rs.getDate(3), rs.getDate(4), rs.getString(5)});
            }
        }
        return filas;
    }
}
