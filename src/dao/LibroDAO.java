
package dao;

import config.DatabaseConnection;
import model.Libro;

import java.sql.*;
import java.util.ArrayList;

public class LibroDAO {

    public ArrayList<Libro> listarLibros() {

        ArrayList<Libro> libros = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                Statement statement = conexion.createStatement();
                ResultSet resultado = statement.executeQuery(sql)
        ) {

            while (resultado.next()) {

                Libro libro = new Libro(
                        resultado.getInt("id"),
                        resultado.getString("titulo"),
                        resultado.getString("autor"),
                        resultado.getString("isbn"),
                        resultado.getString("editorial"),
                        resultado.getInt("stock"),
                        resultado.getInt("id_categoria")
                );

                libros.add(libro);
            }

        } catch (SQLException e) {
            System.out.println("Error al listar libros: " + e.getMessage());
        }

        return libros;
    }


    public boolean agregarLibro(Libro libro) {

        String sql = "INSERT INTO libros " +
                "(titulo, autor, isbn, editorial, stock, id_categoria) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getIsbn());
            statement.setString(4, libro.getEditorial());
            statement.setInt(5, libro.getStock());
            statement.setInt(6, libro.getIdCategoria());

            int filasAfectadas = statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al agregar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarLibro(Libro libro) {

        String sql = "UPDATE libros SET " +
                "titulo = ?, autor = ?, isbn = ?, " +
                "editorial = ?, stock = ?, id_categoria = ? " +
                "WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setString(1, libro.getTitulo());
            statement.setString(2, libro.getAutor());
            statement.setString(3, libro.getIsbn());
            statement.setString(4, libro.getEditorial());
            statement.setInt(5, libro.getStock());
            statement.setInt(6, libro.getIdCategoria());
            statement.setInt(7, libro.getId());

            int filasAfectadas = statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar libro: " + e.getMessage());
            return false;
        }
    }


    public boolean eliminarLibro(int id) {

        String sql = "DELETE FROM libros WHERE id = ?";

        try (
                Connection conexion = DatabaseConnection.getInstance().conectar();
                PreparedStatement statement = conexion.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            int filasAfectadas = statement.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

}
