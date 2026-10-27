package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Clase base de todos los DAO. Concentra TODO el codigo JDBC repetitivo:
 * abrir/cerrar Connection y PreparedStatement, asignar parametros, recorrer
 * el ResultSet y traducir SQLException a DaoException.
 *
 * Cada DAO hijo solo aporta: tabla, columna id, columnas, el mapeo
 * ResultSet -> entidad y sus consultas propias (una linea cada una).
 *
 * @param <T> entidad que gestiona el DAO
 */
public abstract class AbstractDao<T> extends ConexionBD {

    private final String tabla;
    private final String columnaId;
    private final String columnas;

    protected AbstractDao(String url, String tabla, String columnaId, String columnas) {
        super(url);
        this.tabla = tabla;
        this.columnaId = columnaId;
        this.columnas = columnas;
    }

    /** Convierte la fila actual del ResultSet en una entidad. */
    protected abstract T mapRsToClass(ResultSet rs) throws SQLException;

    // ------------------------------------------------------------------
    // CRUD generico: los hijos lo heredan tal cual (cumple la interfaz IxxxDao)
    // ------------------------------------------------------------------

    public List<T> listar() {
        return consultarLista("SELECT " + columnas + " FROM " + tabla);
    }

    public Optional<T> buscarPorId(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return consultarUno("SELECT " + columnas + " FROM " + tabla + " WHERE " + columnaId + " = ?", id);
    }

    /** Lanza NullPointerException si id es null (comportamiento original). */
    public int eliminar(Integer id) {
        Objects.requireNonNull(id, "id");
        return modificar("DELETE FROM " + tabla + " WHERE " + columnaId + " = ?", id);
    }

    // ------------------------------------------------------------------
    // Plantillas JDBC para las consultas propias de cada DAO
    // ------------------------------------------------------------------

    /** SELECT que devuelve varias filas. */
    protected List<T> consultarLista(String sql, Object... params) {
        return ejecutarSql(sql, false, params, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapRsToClass(rs));
                }
                return lista;
            }
        });
    }

    /** SELECT que devuelve como mucho una fila. */
    protected Optional<T> consultarUno(String sql, Object... params) {
        return ejecutarSql(sql, false, params, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRsToClass(rs)) : Optional.<T>empty();
            }
        });
    }

    /** SELECT que devuelve un unico BigDecimal (SUM, AVG...). */
    protected BigDecimal consultarDecimal(String sql, Object... params) {
        return ejecutarSql(sql, false, params, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getBigDecimal(1) != null ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        });
    }

    /** INSERT / UPDATE / DELETE. Devuelve las filas afectadas. */
    protected int modificar(String sql, Object... params) {
        return ejecutarSql(sql, false, params, PreparedStatement::executeUpdate);
    }

    /** INSERT que devuelve el id autogenerado. */
    protected int insertarYObtenerId(String sql, Object... params) {
        return ejecutarSql(sql, true, params, ps -> {
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        });
    }

    // ------------------------------------------------------------------

    @FunctionalInterface
    private interface Operacion<R> {
        R aplicar(PreparedStatement ps) throws SQLException;
    }

    private <R> R ejecutarSql(String sql, boolean clavesGeneradas, Object[] params, Operacion<R> operacion) {
        try (Connection connection = obtenerConexion();
                PreparedStatement ps = clavesGeneradas
                        ? connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                        : connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return operacion.aplicar(ps);
        } catch (SQLException e) {
            throw new DaoException("Error ejecutando: " + sql, e);
        }
    }

    /** Validacion comun: texto no nulo ni en blanco. */
    protected static boolean textoValido(String texto) {
        return texto != null && !texto.isBlank();
    }
}
