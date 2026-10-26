package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.*;
import java.util.*;

/**
 * private String matricula;
 * private String marca;
 * private String modelo;
 * private Integer anio;
 * private Integer idCliente;
 */
public class VehiculoDao extends AbstractDao<Vehiculo> {
    private final String url;

    public VehiculoDao(String url) {
        this.url = url;
    }

    public List<Vehiculo> listar() {
        String sql = "SELECT matricula,marca,modelo,anio,id_cliente FROM Vehiculos order by matricula";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar vehiculo", e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String id) {
        if (!textoValido(id)) {
            return Optional.empty();
        }
        String sql = "SELECT matricula,marca,modelo,anio,id_cliente FROM Vehiculos WHERE matricula = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return consultarUno(ps);
        } catch (SQLException e) {
            registrar("Buscar por matricula de vehiculo", e);
            return Optional.empty();
        }
    }

    public int insertar(Vehiculo dato) {
        if (dato == null || !textoValido(dato.getMatricula()) || !textoValido(dato.getMarca())
                || !textoValido(dato.getModelo()) || dato.getAnio() == null || dato.getIdCliente() == null) {
            return 0;
        }
        String sql = "INSERT INTO Vehiculos (matricula,marca,modelo,anio,id_cliente) VALUES (?,?,?,?,?)";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setString(2, dato.getMarca());
            ps.setString(3, dato.getModelo());
            ps.setInt(4, dato.getAnio());
            ps.setInt(5, dato.getIdCliente());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Insertar vehiculo", e);
            return 0;
        }
    }

    public int actualizar(Vehiculo dato) {
        if (dato == null || !textoValido(dato.getMatricula()) || !textoValido(dato.getMarca())
                || !textoValido(dato.getModelo()) || dato.getAnio() == null || dato.getIdCliente() == null) {
            return 0;
        }
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? WHERE matricula = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getMarca());
            ps.setString(2, dato.getModelo());
            ps.setInt(3, dato.getAnio());
            ps.setInt(4, dato.getIdCliente());
            ps.setString(5, dato.getMatricula());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar vehiculo", e);
            return 0;
        }
    }

    public int eliminar(String id) {
        if (!textoValido(id)) {
            return 0;
        }
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar vehiculo", e);
            return 0;
        }
    }

    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }

        String sql = "SELECT matricula,marca,modelo,anio,id_cliente FROM Vehiculos WHERE id_cliente = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Buscar vehiculos por cliente", e);
            return List.of();
        }
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        if (!textoValido(marca)) {
            return List.of();
        }
        String sql = "SELECT matricula,marca,modelo,anio,id_cliente FROM Vehiculos WHERE marca = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, marca);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar vehiculo por marca", e);
            return List.of();
        }
    }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"),
                    rs.getInt("anio"), rs.getInt("id_cliente")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }
}
