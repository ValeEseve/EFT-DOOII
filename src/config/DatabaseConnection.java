
package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instancia;

    private DatabaseConnection() {
    }

    public static synchronized DatabaseConnection getInstance() {

        if (instancia == null) {
            instancia = new DatabaseConnection();
        }

        return instancia;
    }

    public Connection conectar() throws SQLException {

        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        if (url == null || usuario == null || password == null) {
            throw new SQLException("Faltan variables de entorno.");
        }

        return DriverManager.getConnection(url, usuario, password);
    }
}
