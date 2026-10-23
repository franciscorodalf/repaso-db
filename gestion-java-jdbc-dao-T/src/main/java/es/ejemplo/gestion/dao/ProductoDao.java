package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.*;
import java.util.*;

public class ProductoDao extends AbstractDao<Producto> {
    public ProductoDao(String url) {
        super(url);
    }

    /**
     * EJEMPLO COMPLETO: transformar ResultSet a Producto sin propagar excepciones.
     */
    @Override
    protected Optional<Producto> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass producto", e);
            return Optional.empty();
        }
    }

    /** EJEMPLO COMPLETO: listar productos mediante PreparedStatement. */
    public List<Producto> listar() {
        String sql = "SELECT id,nombre,precio FROM Productos ORDER BY id";
        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar productos", e);
            return List.of();
        }
    }P

    // TODO: implementa cada método con PreparedStatement y try-with-resources.
    // IMPORTANTE: buscarPorId y eliminar usan Integer; la clase base NO necesita
    // ID.
    public Optional<Producto> buscarPorId(Integer id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        String sql = "SELECT id,nombre,precio FROM Productos WHERE id = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return consultarUno(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("consultar por id", e);
            return Optional.empty();
        }

    }

    public boolean eliminar(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        String sql = "DELETE FROM Productos WHERE id = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return ejecutar(statement);

        } catch (SQLException | RuntimeException e) {
            registrar("eliminar por id", e);
            return false;
        }
    }

    public boolean insertar(Producto p) {
        if (p == null || p.getId() <= 0 || p.getNombre() == null || p.getNombre().isBlank() || p.getPrecio() < 0) {
            return false;
        }
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?,?,?)";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, p.getId());
            statement.setString(2, p.getNombre());
            statement.setDouble(3, p.getPrecio());

            return ejecutar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("insertar", e);
            return false;
        }
    }

    public boolean actualizar(Producto p) {
        if (p == null || p.getId() <= 0 || p.getNombre() == null || p.getNombre().isBlank() || p.getPrecio() < 0) {
            return false;
        }
        String sql = "UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, p.getNombre());
            statement.setDouble(2, p.getPrecio());
            statement.setInt(3, p.getId());
            return ejecutar(statement);

        } catch (SQLException | RuntimeException e) {
            registrar("actualizar", e);
            return false;
        }
    }

    public List<Producto> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        String sql = "SELECT id,nombre,precio FROM Productos WHERE nombre LIKE ? ORDER BY nombre, id";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + texto + "%");
            return consultar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar por nombre", e);
            return List.of();
        }

    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        if (minimo > maximo) {
            return List.of();
        }
        String sql = "SELECT id,nombre,precio FROM Productos WHERE precio BETWEEN ? AND ? ORDER BY id";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, minimo);
            statement.setDouble(2, maximo);

            return consultar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar por precio", e);
            return List.of();
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Productos";
        try (Connection connection = this.obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            return consultarEntero(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("contar", e);
           return 0;
        }
    }

    public double precioMedio() {
       String sql = "SELECT COALESCE(AVG(precio), 0) FROM Productos";
       try (Connection connection = this.obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
        return consultarDecimal(statement);
       } catch (SQLException | RuntimeException e) {
            registrar("precio medio", e);
           return 0;
        }
    }
}
