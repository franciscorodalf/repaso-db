package es.ejemplo.taller.dao;

import es.ejemplo.taller.infra.ConexionBD;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase base de todos los DAO. Concentra TODO el codigo JDBC repetitivo:
 * abrir/cerrar Connection y PreparedStatement, asignar parametros, recorrer
 * el ResultSet y capturar SQLException (se registra y se devuelve un valor
 * neutro: lista vacia, Optional.empty(), 0...).
 *
 * Cada DAO hijo solo aporta: tabla, columna id, columnas, el mapeo
 * ResultSet -> entidad, la validacion y sus consultas propias.
 *
 * @param <T>  entidad que gestiona el DAO
 * @param <ID> tipo de la clave primaria (Integer o String)
 */
public abstract class AbstractDao<T, ID> {

    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    protected final String url;
    private final String tabla;
    private final String columnaId;
    protected final String columnas;

    protected AbstractDao(String url, String tabla, String columnaId, String columnas) {
        this.url = url;
        this.tabla = tabla;
        this.columnaId = columnaId;
        this.columnas = columnas;
    }

    /** Convierte la fila actual del ResultSet en una entidad (vacio si falla). */
    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    // ------------------------------------------------------------------
    // CRUD generico: los hijos lo heredan tal cual
    // ------------------------------------------------------------------

    public List<T> listar() {
        return consultarLista("SELECT " + columnas + " FROM " + tabla + " ORDER BY " + columnaId);
    }

    public Optional<T> buscarPorId(ID id) {
        if (idInvalido(id)) {
            return Optional.empty();
        }
        return consultarUno("SELECT " + columnas + " FROM " + tabla + " WHERE " + columnaId + " = ?", id);
    }

    public int eliminar(ID id) {
        if (idInvalido(id)) {
            return 0;
        }
        return modificar("DELETE FROM " + tabla + " WHERE " + columnaId + " = ?", id);
    }

    // ------------------------------------------------------------------
    // Plantillas JDBC para las consultas propias de cada DAO
    // ------------------------------------------------------------------

    /** SELECT que devuelve varias filas. Si una fila no se puede mapear, lista vacia. */
    protected List<T> consultarLista(String sql, Object... params) {
        return ejecutarSql(sql, false, params, List.of(), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (rs.next()) {
                    Optional<T> entidad = mapRsToClass(rs);
                    if (entidad.isEmpty()) {
                        return List.<T>of(); // nunca devolver una lista parcial
                    }
                    resultado.add(entidad.get());
                }
                return resultado;
            }
        });
    }

    /** SELECT que devuelve como mucho una fila. */
    protected Optional<T> consultarUno(String sql, Object... params) {
        return ejecutarSql(sql, false, params, Optional.empty(), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRsToClass(rs) : Optional.<T>empty();
            }
        });
    }

    /** SELECT que devuelve un unico BigDecimal (SUM, AVG...). */
    protected BigDecimal consultarDecimal(String sql, Object... params) {
        return ejecutarSql(sql, false, params, BigDecimal.ZERO, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        });
    }

    /** INSERT / UPDATE / DELETE. Devuelve las filas afectadas (0 si falla). */
    protected int modificar(String sql, Object... params) {
        return ejecutarSql(sql, false, params, 0, PreparedStatement::executeUpdate);
    }

    /** INSERT que devuelve el id autogenerado (0 si falla). */
    protected int insertarYObtenerId(String sql, Object... params) {
        return ejecutarSql(sql, true, params, 0, ps -> {
            if (ps.executeUpdate() == 0) {
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    // ------------------------------------------------------------------

    @FunctionalInterface
    private interface Operacion<R> {
        R aplicar(PreparedStatement ps) throws SQLException;
    }

    /** Unico sitio donde se abre la conexion, se asignan parametros y se captura el error. */
    private <R> R ejecutarSql(String sql, boolean clavesGeneradas, Object[] params, R porDefecto,
            Operacion<R> operacion) {
        try (Connection connection = ConexionBD.abrir(url);
                PreparedStatement ps = clavesGeneradas
                        ? connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                        : connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return operacion.aplicar(ps);
        } catch (SQLException e) {
            registrar(sql, e);
            return porDefecto;
        }
    }

    private boolean idInvalido(ID id) {
        return id == null || (id instanceof String s && s.isBlank());
    }

    protected boolean textoValido(String texto) {
        return texto != null && !texto.isBlank();
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, operacion, e);
    }
}
