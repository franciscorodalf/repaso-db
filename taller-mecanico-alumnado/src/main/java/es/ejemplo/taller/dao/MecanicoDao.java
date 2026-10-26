package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.Mecanico;
import java.sql.*;
import java.util.*;

public class MecanicoDao extends AbstractDao<Mecanico> {
    private final String url;

    public MecanicoDao(String url) {
        this.url = url;
    }

    public List<Mecanico> listar() {
        String sql = "SELECT id_mecanico,nombre,especialidad,tarifa_hora FROM Mecanicos order by id_mecanico";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar mecanico", e);
            return List.of();
        }
    }

    public Optional<Mecanico> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_mecanico,nombre,especialidad,tarifa_hora FROM Mecanicos WHERE id_mecanico = ? ";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException e) {
            registrar("Buscar por id de mecanico", e);
            return Optional.empty();
        }
    }

    public int insertar(Mecanico dato) {
        if (dato == null || !textoValido(dato.getNombre()) || !textoValido(dato.getEspecialidad())
                || dato.getTarifaHora() == null || dato.getTarifaHora().signum() < 0) {
            return 0;
        }
        String sql = "INSERT INTO Mecanicos (nombre,especialidad,tarifa_hora) VALUES (?,?,?)";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            return insertarId(ps);
        } catch (SQLException e) {
            registrar("Insertar mecanico", e);
            return 0;
        }
    }

    public int actualizar(Mecanico dato) {
        if (dato == null || dato.getIdMecanico() == null || !textoValido(dato.getNombre())
                || !textoValido(dato.getEspecialidad()) || dato.getTarifaHora() == null
                || dato.getTarifaHora().signum() < 0) {
            return 0;
        }
        String sql = "UPDATE Mecanicos SET  nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, dato.getNombre());
            ps.setString(2, dato.getEspecialidad());
            ps.setBigDecimal(3, dato.getTarifaHora());
            ps.setInt(4, dato.getIdMecanico());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar mecanico", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM Mecanicos WHERE id_mecanico = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar mecanico", e);
            return 0;
        }
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            return List.of();
        }

        String sql = "SELECT id_mecanico,nombre,especialidad,tarifa_hora FROM Mecanicos WHERE especialidad = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, especialidad);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Buscar por especialidad de mecanico", e);
            return List.of();
        }
    }

    /**
     * private Integer idMecanico;
     * private String nombre;
     * private String especialidad;
     * private BigDecimal tarifaHora;
     */
    @Override
    protected Optional<Mecanico> mapRsToClass(ResultSet rs) {
        try {
            return Optional
                    .of(new Mecanico(rs.getInt("id_mecanico"), rs.getString("nombre"), rs.getString("especialidad"),
                            rs.getBigDecimal("tarifa_hora")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass mecanico", e);
            return Optional.empty();
        }
    }
}
