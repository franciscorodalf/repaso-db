package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoDao extends ConexionBD {

    public ProductoDao(String url) {
        super(url);
    }

    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String query = "select id,nombre,precio from productos ORDER BY id";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement ps = connection.prepareStatement(query);) {
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    lista.add(mapeoProducto(rs));
                }
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Producto> buscarPorId(int id) {
        String sql = "SELECT id,nombre,precio FROM productos where id = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoProducto(rs));
                }

                return Optional.empty();
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {
        String sql = "UPDATE productos SET nombre = ?, precio = ? WHERE id = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, producto.getNombre());
            statement.setDouble(2, producto.getPrecio());
            statement.setInt(3, producto.getId());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM productos where id = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Producto> buscarPorNombre(String texto) {
        String sql = "Select id,nombre,precio FROM productos  WHERE nombre LIKE ? ORDER BY nombre";
        List<Producto> list = new ArrayList<>();
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + texto + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapeoProducto(rs));
                }
                return list;
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        String sql = "Select id,nombre,precio FROM productos WHERE precio BETWEEN ? and ? ORDER BY nombre";
        List<Producto> list = new ArrayList<>();

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setDouble(1, minimo);
                    statement.setDouble(2, maximo);
                    try (ResultSet rs = statement.executeQuery()){
                        while (rs.next()) {
                            list.add(mapeoProducto(rs));
                        }
                        return list;
                    } 
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) from Productos";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }

    }

    public double precioMedio() {
        String sql = "SELECT AVG(precio) from Productos";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    private Producto mapeoProducto(ResultSet rs) {

        try {
            return new Producto(rs.getInt(1), rs.getString(2), rs.getDouble(3));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
