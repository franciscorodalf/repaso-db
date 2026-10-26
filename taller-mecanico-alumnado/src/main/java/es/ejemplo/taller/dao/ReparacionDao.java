package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Reparacion;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * private Integer idReparacion;
 * private String matricula;
 * private Integer idMecanico;
 * private String descripcion;
 * private String fecha;
 * private BigDecimal horasTrabajo;
 * private String estado;
 */
public class ReparacionDao extends AbstractDao<Reparacion> {
    private static final List<String> ESTADOS = List.of("PENDIENTE", "EN_PROCESO", "FINALIZADA");
    private static final String COLUMNAS = "id_reparacion,matricula,id_mecanico,descripcion,fecha,horas_trabajo,estado";

    private final String url;

    public ReparacionDao(String url) {
        this.url = url;
    }

    public List<Reparacion> listar() {
        String sql = "SELECT " + COLUMNAS + " FROM Reparaciones order by id_reparacion";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar reparacion", e);
            return List.of();
        }
    }

    public Optional<Reparacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT " + COLUMNAS + " FROM Reparaciones WHERE id_reparacion = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException e) {
            registrar("Buscar por id de reparacion", e);
            return Optional.empty();
        }
    }

    public int insertar(Reparacion dato) {
        if (!datosValidos(dato)) {
            return 0;
        }
        String sql = "INSERT INTO Reparaciones (matricula,id_mecanico,descripcion,fecha,horas_trabajo,estado) VALUES (?,?,?,?,?,?)";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());
            return insertarId(ps);
        } catch (SQLException e) {
            registrar("Insertar reparacion", e);
            return 0;
        }
    }

    public int actualizar(Reparacion dato) {
        if (!datosValidos(dato) || dato.getIdReparacion() == null) {
            return 0;
        }
        String sql = "UPDATE Reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getMatricula());
            ps.setInt(2, dato.getIdMecanico());
            ps.setString(3, dato.getDescripcion());
            ps.setString(4, dato.getFecha());
            ps.setBigDecimal(5, dato.getHorasTrabajo());
            ps.setString(6, dato.getEstado());
            ps.setInt(7, dato.getIdReparacion());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar reparacion", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM Reparaciones WHERE id_reparacion = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar reparacion", e);
            return 0;
        }
    }

    public List<Reparacion> buscarPorVehiculo(String valor) {
        if (!textoValido(valor)) {
            return List.of();
        }
        String sql = "SELECT " + COLUMNAS + " FROM Reparaciones WHERE matricula = ? order by id_reparacion";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, valor);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Buscar reparaciones por vehiculo", e);
            return List.of();
        }
    }

    public List<Reparacion> buscarPorMecanico(Integer valor) {
        if (valor == null) {
            return List.of();
        }
        String sql = "SELECT " + COLUMNAS + " FROM Reparaciones WHERE id_mecanico = ? order by id_reparacion";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, valor);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Buscar reparaciones por mecanico", e);
            return List.of();
        }
    }

    public List<Reparacion> listarPendientes() {
        String sql = "SELECT " + COLUMNAS + " FROM Reparaciones WHERE estado = 'PENDIENTE' order by fecha, id_reparacion";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar reparaciones pendientes", e);
            return List.of();
        }
    }

    private boolean datosValidos(Reparacion dato) {
        return dato != null
                && textoValido(dato.getMatricula())
                && dato.getIdMecanico() != null
                && textoValido(dato.getDescripcion())
                && fechaValida(dato.getFecha())
                && dato.getHorasTrabajo() != null && dato.getHorasTrabajo().signum() >= 0
                && dato.getEstado() != null && ESTADOS.contains(dato.getEstado());
    }

    private boolean fechaValida(String fecha) {
        if (!textoValido(fecha)) {
            return false;
        }
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    @Override
    protected Optional<Reparacion> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Reparacion(rs.getInt("id_reparacion"), rs.getString("matricula"),
                    rs.getInt("id_mecanico"), rs.getString("descripcion"), rs.getString("fecha"),
                    rs.getBigDecimal("horas_trabajo"), rs.getString("estado")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass reparacion", e);
            return Optional.empty();
        }
    }
}
