package es.ejemplo.clinica.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Abre conexiones SQLite sin cerrarlas antes de devolverlas. */
public class ConexionBD {
    private final String url;

    public ConexionBD(String url) {
        this.url = url;
    }

    public Connection obtenerConexion() throws SQLException {
        if (url == null || !url.startsWith("jdbc:sqlite:"))
            throw new IllegalArgumentException("URL SQLite no válida");
        String ruta = url.substring("jdbc:sqlite:".length());
        if (!ruta.equals(":memory:") && !ruta.startsWith("file:") &&
                !Files.isRegularFile(Path.of(ruta)))
            throw new IllegalArgumentException("La base de datos no existe: " +
                    ruta);
        Connection cn = DriverManager.getConnection(url);
        try (Statement st = cn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        } catch (SQLException e) {
            cn.close();
            throw e;
        }
        return cn;
    }
}
