
package dao;

import config.DatabaseConnection;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthDAO {

    public Usuario iniciarSesion(String rut, String contraseña) {

        String sql = "SELECT id, nombre, rut, correo, rol " +
                "FROM usuarios " +
                "WHERE rut = ? AND contraseña = ?";

        try (Connection conexion =
                     DatabaseConnection.getInstance().conectar();

             PreparedStatement statement =
                     conexion.prepareStatement(sql)) {

            statement.setString(1, rut);
            statement.setString(2, contraseña);

            try (ResultSet resultado = statement.executeQuery()) {

                if (resultado.next()) {

                    return new Usuario(
                            resultado.getInt("id"),
                            resultado.getString("nombre"),
                            resultado.getString("rut"),
                            resultado.getString("correo"),
                            null,
                            resultado.getString("rol")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al iniciar sesión: " + e.getMessage()
            );
        }

        return null;
    }
}
