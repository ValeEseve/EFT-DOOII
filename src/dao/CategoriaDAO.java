
package dao;

import config.DatabaseConnection;
import model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriaDAO {

    // READ: Obtener todas las categorías
    public ArrayList<Categoria> listarCategorias() {

        ArrayList<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY id";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql);
                ResultSet resultado = statement.executeQuery()
        ) {

            while (resultado.next()) {
                Categoria categoria = new Categoria(
                        resultado.getInt("id"),
                        resultado.getString("nombre")
                );

                categorias.add(categoria);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar categorías: " + e.getMessage());
        }

        return categorias;
    }

    // CREATE: Agregar una nueva categoría
    public boolean agregarCategoria(Categoria categoria) {

        String sql = "INSERT INTO categorias (nombre) VALUES (?)";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, categoria.getNombre());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar categoría: " + e.getMessage());
            return false;
        }
    }

    // UPDATE: Modificar una categoría existente
    public boolean actualizarCategoria(Categoria categoria) {

        String sql = "UPDATE categorias SET nombre = ? WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, categoria.getNombre());
            statement.setInt(2, categoria.getId());

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Eliminar una categoría por su ID
    public boolean eliminarCategoria(int id) {

        String sql = "DELETE FROM categorias WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            int filasAfectadas = statement.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }
}
