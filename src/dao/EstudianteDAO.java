
package dao;

import config.DatabaseConnection;
import model.Estudiante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstudianteDAO {

    // CREATE
    public boolean agregarEstudiante(Estudiante estudiante) {

        String sql = "INSERT INTO estudiantes " +
                "(nombre, rut, curso, correo) VALUES (?, ?, ?, ?)";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getRut());
            statement.setString(3, estudiante.getCurso());
            statement.setString(4, estudiante.getCorreo());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar estudiante: " + e.getMessage());
            return false;
        }
    }

    // READ
    public ArrayList<Estudiante> listarEstudiantes() {

        ArrayList<Estudiante> estudiantes = new ArrayList<>();

        String sql = "SELECT * FROM estudiantes ORDER BY id";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {

            while (resultado.next()) {

                Estudiante estudiante = new Estudiante(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("rut"),
                        resultado.getString("correo"),
                        resultado.getString("curso")
                );

                estudiantes.add(estudiante);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar estudiantes: " + e.getMessage());
        }

        return estudiantes;
    }

    public Estudiante buscarPorRut(String rut) {
        String sql = "SELECT * FROM estudiantes WHERE rut = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, rut);

            try (ResultSet resultado = statement.executeQuery()) {
                if (resultado.next()) {
                    return new Estudiante(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("rut"),
                            resultado.getString("correo"),
                            resultado.getString("curso")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por RUT: " + e.getMessage());
        }

        return null;
    }

    // UPDATE
    public boolean actualizarEstudiante(Estudiante estudiante) {

        String sql = "UPDATE estudiantes SET " +
                "nombre = ?, rut = ?, curso = ?, correo = ? " +
                "WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, estudiante.getNombre());
            statement.setString(2, estudiante.getRut());
            statement.setString(3, estudiante.getCurso());
            statement.setString(4, estudiante.getCorreo());
            statement.setInt(5, estudiante.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estudiante: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminarEstudiante(int id) {

        String sql = "DELETE FROM estudiantes WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar estudiante: " + e.getMessage());
            return false;
        }
    }
}
