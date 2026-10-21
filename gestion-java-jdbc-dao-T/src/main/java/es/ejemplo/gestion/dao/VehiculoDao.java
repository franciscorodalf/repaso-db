package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

public class VehiculoDao extends AbstractDao<Vehiculo> {
    public VehiculoDao(String url) {
        super(url);
    }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(rs.getString("matricula"), rs.getString("marca"), rs.getString("modelo"),
                    rs.getInt("anio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }

    public List<Vehiculo> listar() {
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos ORDER BY matricula";
        try (Connection con = obtenerConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException | RuntimeException e) {
            registrar("listar vehiculos", e);
            return List.of();
        }
    }

    public Optional<Vehiculo> buscarPorId(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return Optional.empty();
        }
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos WHERE matricula = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matricula);
            return consultarUno(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("consultar por id", e);
            return Optional.empty();
        }

    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        return buscarPorId(matricula);
    }

    public boolean eliminar(String matricula) {
        if (matricula == null || matricula.isBlank()) {
            return false;
        }
        String sql = "DELETE FROM Vehiculos WHERE matricula = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matricula);
            return ejecutar(statement);

        } catch (SQLException | RuntimeException e) {
            registrar("eliminar por matricula", e);
            return false;
        }
    }

    public boolean insertar(Vehiculo v) {
        if (v == null || v.getMatricula() == null || v.getMatricula().isBlank()
                || !v.getMatricula().equals(v.getMatricula().trim().toUpperCase())
                || v.getMarca() == null || v.getMarca().isBlank()
                || v.getModelo() == null || v.getModelo().isBlank()) {
            return false;
        }
        String sql = "INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?,?,?,?)";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, v.getMatricula());
            statement.setString(2, v.getMarca());
            statement.setString(3, v.getModelo());
            statement.setInt(4, v.getAnio());

            return ejecutar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("insertar", e);
            return false;
        }
    }

    public boolean actualizar(Vehiculo v) {
        if (v == null || v.getMatricula() == null || v.getMatricula().isBlank()
                || !v.getMatricula().equals(v.getMatricula().trim().toUpperCase())
                || v.getMarca() == null || v.getMarca().isBlank()
                || v.getModelo() == null || v.getModelo().isBlank() || v.getAnio() < 1886) {
            return false;
        }
        String sql = "UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, v.getMarca());
            statement.setString(2, v.getModelo());
            statement.setInt(3, v.getAnio());
            statement.setString(4, v.getMatricula());

            return ejecutar(statement);

        } catch (SQLException | RuntimeException e) {
            registrar("actualizar", e);
            return false;
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        if (texto == null || texto.isBlank()) {
            return List.of();
        }
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos WHERE marca LIKE ? ORDER BY marca, matricula";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + texto + "%");
            return consultar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar por marca", e);
            return List.of();
        }

    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        if (desde > hasta) {
            return List.of();
        }
        String sql = "SELECT matricula,marca,modelo,anio FROM Vehiculos WHERE anio BETWEEN ? AND ? ORDER BY matricula";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, desde);
            statement.setInt(2, hasta);

            return consultar(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("buscar por anio", e);
            return List.of();
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) FROM Vehiculos";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            return consultarEntero(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("contar", e);
            return 0;
        }
    }

    public Map<String, Integer> contarPorMarca() {
        String sql = "SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca";
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            return consultarAgrupado(statement);
        } catch (SQLException | RuntimeException e) {
            registrar("contar por marca", e);
            return Map.of();
        }

    }
}
