package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;
import es.ejemplo.taller.modelo.PiezaReparacion;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * private Integer idPieza;
 * private Integer idReparacion;
 * private String nombre;
 * private Integer cantidad;
 * private BigDecimal precioUnitario;
 */
public class PiezaReparacionDao extends AbstractDao<PiezaReparacion> {
    private final String url;

    public PiezaReparacionDao(String url) {
        this.url = url;
    }

    public List<PiezaReparacion> listar() {
        String sql = "SELECT id_pieza,id_reparacion,nombre,cantidad,precio_unitario FROM piezas_reparacion order by id_pieza";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar pieza", e);
            return List.of();
        }
    }

    public Optional<PiezaReparacion> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        String sql = "SELECT id_pieza,id_reparacion,nombre,cantidad,precio_unitario FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarUno(ps);
        } catch (SQLException e) {
            registrar("Buscar por id de pieza", e);
            return Optional.empty();
        }
    }

    public int insertar(PiezaReparacion dato) {
        if (!datosValidos(dato)) {
            return 0;
        }
        String sql = "INSERT INTO piezas_reparacion (id_reparacion,nombre,cantidad,precio_unitario) VALUES (?,?,?,?)";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            return insertarId(ps);
        } catch (SQLException e) {
            registrar("Insertar pieza", e);
            return 0;
        }
    }

    public int actualizar(PiezaReparacion dato) {
        if (!datosValidos(dato) || dato.getIdPieza() == null) {
            return 0;
        }
        String sql = "UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, precio_unitario = ? WHERE id_pieza = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, dato.getIdReparacion());
            ps.setString(2, dato.getNombre());
            ps.setInt(3, dato.getCantidad());
            ps.setBigDecimal(4, dato.getPrecioUnitario());
            ps.setInt(5, dato.getIdPieza());
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Actualizar pieza", e);
            return 0;
        }
    }

    public int eliminar(Integer id) {
        if (id == null) {
            return 0;
        }
        String sql = "DELETE FROM piezas_reparacion WHERE id_pieza = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ejecutar(ps);
        } catch (SQLException e) {
            registrar("Eliminar pieza", e);
            return 0;
        }
    }

    public List<PiezaReparacion> listarPorReparacion(Integer id) {
        if (id == null) {
            return List.of();
        }
        String sql = "SELECT id_pieza,id_reparacion,nombre,cantidad,precio_unitario FROM piezas_reparacion WHERE id_reparacion = ? order by id_pieza";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultar(ps);
        } catch (SQLException e) {
            registrar("Listar piezas por reparacion", e);
            return List.of();
        }
    }

    public BigDecimal calcularCostePiezas(Integer id) {
        if (id == null) {
            return BigDecimal.ZERO;
        }
        // COALESCE: si la reparación no tiene piezas, SUM devuelve NULL y se convierte en 0
        String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM piezas_reparacion WHERE id_reparacion = ?";
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return consultarDecimal(ps);
        } catch (SQLException e) {
            registrar("Calcular coste de piezas", e);
            return BigDecimal.ZERO;
        }
    }

    private boolean datosValidos(PiezaReparacion dato) {
        return dato != null
                && dato.getIdReparacion() != null
                && textoValido(dato.getNombre())
                && dato.getCantidad() != null && dato.getCantidad() > 0
                && dato.getPrecioUnitario() != null && dato.getPrecioUnitario().signum() >= 0;
    }

    @Override
    protected Optional<PiezaReparacion> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new PiezaReparacion(rs.getInt("id_pieza"), rs.getInt("id_reparacion"),
                    rs.getString("nombre"), rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass pieza", e);
            return Optional.empty();
        }
    }
}
