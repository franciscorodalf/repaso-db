package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Vehiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VehiculoDao extends ConexionBD {

    public VehiculoDao(String url) {
        super(url);
    }

    public List<Vehiculo> listar() {
        String query = "select  matricula,marca,modelo,anio from vehiculos ORDER BY matricula";
        List<Vehiculo> lista = new ArrayList<>();

        try (Connection connection = this.obtenerConexion();
                PreparedStatement ps = connection.prepareStatement(query);) {
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    lista.add(mapeoVehiculo(rs));
                }
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        String sql = "SELECT matricula,marca,modelo,anio FROM vehiculos where matricula = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matricula);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapeoVehiculo(rs));
                }

                return Optional.empty();
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean insertar(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (matricula,marca,modelo,anio) VALUES (?, ?, ?, ?)";
        try (Connection con = this.obtenerConexion();
                PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, vehiculo.getMatricula());
            statement.setString(2, vehiculo.getMarca());
            statement.setString(3, vehiculo.getModelo());
            statement.setInt(4, vehiculo.getAnio());
            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException("Error al insertar el vehiculo", e);
        }
    }

    public boolean actualizar(Vehiculo vehiculo) {
        String sql = "UPDATE vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, vehiculo.getMarca());
            statement.setString(2, vehiculo.getModelo());
            statement.setInt(3, vehiculo.getAnio());
            statement.setString(4, vehiculo.getMatricula());

            return statement.executeUpdate() > 0;
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public boolean eliminar(String matricula) {
        String sql = "DELETE FROM vehiculos where matricula = ?";

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, matricula);

            return statement.executeUpdate() > 0;

        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        String sql = "Select matricula,marca,modelo,anio FROM vehiculos WHERE marca LIKE ? ORDER BY matricula";
        List<Vehiculo> list = new ArrayList<>();
        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + texto + "%");

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapeoVehiculo(rs));
                }
                return list;
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        String sql = "Select matricula,marca,modelo,anio FROM vehiculos WHERE anio BETWEEN ? and ? ORDER BY modelo";
        List<Vehiculo> list = new ArrayList<>();

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, desde);
            statement.setInt(2, hasta);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(mapeoVehiculo(rs));
                }
                return list;
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
    }

    public int contar() {
        String sql = "SELECT COUNT(*) from vehiculos";
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

    public Map<String, Integer> contarPorMarca() {
        String sql = "Select marca,COUNT(marca) from vehiculos GROUP BY marca ORDER BY marca";
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        try (Connection connection = this.obtenerConexion();
                PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt(2));
                }
                return map;
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }

    }

    private Vehiculo mapeoVehiculo(ResultSet rs) {
        try {
            return new Vehiculo(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
