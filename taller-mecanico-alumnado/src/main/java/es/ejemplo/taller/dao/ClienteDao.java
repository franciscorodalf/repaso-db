package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Cliente;
import java.sql.*;
import java.util.*;

public class ClienteDao extends AbstractDao<Cliente> {
    private final String url;

    public ClienteDao(String url) {
        this.url = url;
    }

    public List<Cliente> listar() {
        String sql = "SELECT id_cliente,nombre,email,telefono FROM Clientes order by id_cliente";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar cliente", e);
            return List.of();
        }
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        String sql = "SELECT id_cliente, nombre,email,telefono FROM Clientes WHERE id_cliente = ? order by id_cliente";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException e) {
            registrar("Buscar por id de cliente", e);
            return Optional.empty();
        }
    }

    public int insertar(Cliente dato) {
        if (dato == null) {
            return 0;
        }
        String sql = "INSERT INTO Clientes (nombre, email, telefono) VALUES (?,?,?) ";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            return insertarId(ps);
        } catch (SQLException e) {
            registrar("Insertar cliente", e);
            return 0;
        }
    }

    public int actualizar(Cliente dato) {
          if (dato == null) {
            return 0;
        }
        String sql = "UPDATE Clientes SET  nombre = ?, email = ?, telefono = ? WHERE id_cliente = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEmail());
            ps.setString(3, dato.getTelefono());
            ps.setInt(4, dato.getIdCliente());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar cliente", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
          if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM Clientes WHERE id_cliente = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar cliente", e);
            return 0;
        }
    }

    /**
     * private Integer idCliente;
     * private String nombre;
     * private String email;
     * private String telefono;
     */
    @Override
    protected Optional<Cliente> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"), rs.getString("email"),
                    rs.getString("telefono")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
