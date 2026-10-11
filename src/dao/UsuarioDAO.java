
package dao;

import config.DatabaseConnection;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {

    // CREATE
    public boolean agregarUsuario(Usuario usuario) {

        String sql = "INSERT INTO usuarios " +
                "(nombre, rut, correo, contraseña, rol) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getRut());
            statement.setString(3, usuario.getCorreo());
            statement.setString(4, usuario.getContrasena());
            statement.setString(5, usuario.getRol());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    // READ
    public ArrayList<Usuario> listarUsuarios() {

        ArrayList<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT * FROM usuarios ORDER BY id";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {

            while (resultado.next()) {

                Usuario usuario = new Usuario(
                        resultado.getInt("id"),
                        resultado.getString("nombre"),
                        resultado.getString("rut"),
                        resultado.getString("correo"),
                        resultado.getString("contraseña"),
                        resultado.getString("rol")
                );

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    // UPDATE
    public boolean actualizarUsuario(Usuario usuario) {

        String sql = "UPDATE usuarios SET " +
                "nombre = ?, rut = ?, correo = ?, " +
                "contraseña = ?, rol = ? WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, usuario.getNombre());
            statement.setString(2, usuario.getRut());
            statement.setString(3, usuario.getCorreo());
            statement.setString(4, usuario.getContrasena());
            statement.setString(5, usuario.getRol());
            statement.setInt(6, usuario.getId());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminarUsuario(int id) {

        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
