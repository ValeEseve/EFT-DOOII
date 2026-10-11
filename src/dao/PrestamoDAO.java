
package dao;

import config.DatabaseConnection;
import model.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;

public class PrestamoDAO {

    // CREATE: Guardar un registro de préstamo
    public boolean agregarPrestamo(Prestamo prestamo) {

        String sql = "INSERT INTO prestamos " +
                "(id_estudiante, id_libro, fecha_prestamo, " +
                "fecha_devolucion, devuelto) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, prestamo.getIdEstudiante());
            statement.setInt(2, prestamo.getIdLibro());

            statement.setDate(3,
                    Date.valueOf(prestamo.getFechaPrestamo()));

            if (prestamo.getFechaDevolucion() != null) {
                statement.setDate(4,
                        Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }

            statement.setBoolean(5, prestamo.isDevuelto());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar préstamo: " + e.getMessage());
            return false;
        }
    }

    // READ: Obtener todos los préstamos
    public ArrayList<Prestamo> listarPrestamos() {

        ArrayList<Prestamo> prestamos = new ArrayList<>();

        String sql = "SELECT * FROM prestamos ORDER BY id";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {

            while (resultado.next()) {

                Date fechaDevolucion =
                        resultado.getDate("fecha_devolucion");

                Prestamo prestamo = new Prestamo(
                        resultado.getInt("id"),
                        resultado.getInt("id_estudiante"),
                        resultado.getInt("id_libro"),
                        resultado.getDate("fecha_prestamo").toLocalDate(),
                        fechaDevolucion != null
                                ? fechaDevolucion.toLocalDate()
                                : null,
                        resultado.getBoolean("devuelto")
                );

                prestamos.add(prestamo);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar préstamos: " + e.getMessage());
        }

        return prestamos;
    }

    // UPDATE: Modificar un registro de préstamo
    public boolean actualizarPrestamo(Prestamo prestamo) {

        String sql = "UPDATE prestamos SET " +
                "id_estudiante = ?, id_libro = ?, " +
                "fecha_prestamo = ?, fecha_devolucion = ?, " +
                "devuelto = ? WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, prestamo.getIdEstudiante());
            statement.setInt(2, prestamo.getIdLibro());

            statement.setDate(3,
                    Date.valueOf(prestamo.getFechaPrestamo()));

            if (prestamo.getFechaDevolucion() != null) {
                statement.setDate(4,
                        Date.valueOf(prestamo.getFechaDevolucion()));
            } else {
                statement.setNull(4, java.sql.Types.DATE);
            }

            statement.setBoolean(5, prestamo.isDevuelto());
            statement.setInt(6, prestamo.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Eliminar un registro por su ID
    public boolean eliminarPrestamo(int id) {

        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }
}
