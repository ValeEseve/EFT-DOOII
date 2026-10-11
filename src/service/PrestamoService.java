
package service;

import config.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

public class PrestamoService {

    // Registrar un préstamo y descontar el stock
    public synchronized boolean prestarLibro(int idEstudiante, int idLibro) {

        try (Connection conexion = DatabaseConnection.getInstance().conectar()) {

            conexion.setAutoCommit(false);

            try {
                // Verificar que el estudiante exista
                String sqlEstudiante =
                        "SELECT id FROM estudiantes WHERE id = ?";

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlEstudiante)) {

                    statement.setInt(1, idEstudiante);

                    try (ResultSet resultado = statement.executeQuery()) {
                        if (!resultado.next()) {
                            System.out.println("El estudiante no existe.");
                            conexion.rollback();
                            return false;
                        }
                    }
                }

                // Consultar y bloquear el stock del libro
                String sqlStock =
                        "SELECT stock FROM libros WHERE id = ? FOR UPDATE";

                int stock;

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlStock)) {

                    statement.setInt(1, idLibro);

                    try (ResultSet resultado = statement.executeQuery()) {
                        if (!resultado.next()) {
                            System.out.println("El libro no existe.");
                            conexion.rollback();
                            return false;
                        }

                        stock = resultado.getInt("stock");
                    }
                }

                if (stock <= 0) {
                    System.out.println("No hay ejemplares disponibles.");
                    conexion.rollback();
                    return false;
                }

                // Registrar el préstamo
                String sqlPrestamo =
                        "INSERT INTO prestamos " +
                                "(id_estudiante, id_libro, fecha_prestamo, " +
                                "fecha_devolucion, devuelto) " +
                                "VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlPrestamo)) {

                    LocalDate hoy = LocalDate.now();

                    statement.setInt(1, idEstudiante);
                    statement.setInt(2, idLibro);
                    statement.setDate(3, Date.valueOf(hoy));
                    statement.setDate(4, Date.valueOf(hoy.plusDays(7)));
                    statement.setBoolean(5, false);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("No se pudo registrar el préstamo.");
                    }
                }

                // Descontar una unidad del stock
                String sqlActualizar =
                        "UPDATE libros SET stock = stock - 1 " +
                                "WHERE id = ? AND stock > 0";

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlActualizar)) {

                    statement.setInt(1, idLibro);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("No se pudo descontar el stock.");
                    }
                }

                conexion.commit();

                System.out.println("Préstamo registrado correctamente.");
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                System.out.println("Error en el préstamo: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    // Registrar una devolución y recuperar el stock
    public synchronized boolean devolverLibro(int idPrestamo) {

        try (Connection conexion = DatabaseConnection.getInstance().conectar()) {

            conexion.setAutoCommit(false);

            try {
                // Buscar y bloquear el préstamo
                String sqlBuscar =
                        "SELECT id_libro, devuelto FROM prestamos " +
                                "WHERE id = ? FOR UPDATE";

                int idLibro;
                boolean devuelto;

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlBuscar)) {

                    statement.setInt(1, idPrestamo);

                    try (ResultSet resultado = statement.executeQuery()) {

                        if (!resultado.next()) {
                            System.out.println("El préstamo no existe.");
                            conexion.rollback();
                            return false;
                        }

                        idLibro = resultado.getInt("id_libro");
                        devuelto = resultado.getBoolean("devuelto");
                    }
                }

                // Evitar devoluciones duplicadas
                if (devuelto) {
                    System.out.println("Este préstamo ya fue devuelto.");
                    conexion.rollback();
                    return false;
                }

                // Marcar el préstamo como devuelto
                String sqlDevolucion =
                        "UPDATE prestamos SET devuelto = TRUE " +
                                "WHERE id = ? AND devuelto = FALSE";

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlDevolucion)) {

                    statement.setInt(1, idPrestamo);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("No se pudo registrar la devolución.");
                    }
                }

                // Recuperar una unidad de stock
                String sqlStock =
                        "UPDATE libros SET stock = stock + 1 WHERE id = ?";

                try (PreparedStatement statement =
                             conexion.prepareStatement(sqlStock)) {

                    statement.setInt(1, idLibro);

                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("No se pudo recuperar el stock.");
                    }
                }

                conexion.commit();

                System.out.println("Devolución registrada correctamente.");
                return true;

            } catch (SQLException e) {
                conexion.rollback();
                System.out.println("Error en la devolución: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
