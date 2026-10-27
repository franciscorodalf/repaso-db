package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import java.sql.*;
import java.util.*;
import java.util.logging.*;

/**
 * Clase base de todos los DAO. Concentra TODO el codigo JDBC repetitivo:
 * abrir/cerrar Connection y PreparedStatement, asignar parametros, recorrer
 * el ResultSet y capturar errores (se registran y se devuelve un valor neutro:
 * lista vacia, Optional.empty(), false, 0...).
 *
 * Cada DAO hijo solo aporta: tabla, columna id, columnas, el mapeo
 * ResultSet -> entidad, la validacion y sus consultas propias.
 *
 * @param <T>  entidad que gestiona el DAO
 * @param <ID> tipo de la clave primaria (Integer o String)
 */
public abstract class AbstractDao<T, ID> extends ConexionBD {
    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    private final String tabla;
    private final String columnaId;
    protected final String columnas;

    protected AbstractDao(String url, String tabla, String columnaId, String columnas) {
        super(url);
        this.tabla = tabla;
        this.columnaId = columnaId;
        this.columnas = columnas;
    }

    /** Cada DAO captura errores de lectura y devuelve Optional.empty() si falla. */
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

    public boolean eliminar(ID id) {
        if (idInvalido(id)) {
            return false;
        }
        return ejecutar("DELETE FROM " + tabla + " WHERE " + columnaId + " = ?", id);
    }

    public int contar() {
        return consultarEntero("SELECT COUNT(*) FROM " + tabla);
    }

    // ------------------------------------------------------------------
    // Plantillas JDBC para las consultas propias de cada DAO
    // ------------------------------------------------------------------

    /** SELECT que devuelve varias filas. Si una fila no se puede mapear, lista vacia. */
    protected List<T> consultarLista(String sql, Object... params) {
        return ejecutarSql(sql, params, List.of(), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (rs.next()) {
                    Optional<T> entidad = mapRsToClass(rs);
                    if (entidad.isEmpty())
                        return List.<T>of(); // No devolver una lista parcial
                    resultado.add(entidad.get());
                }
                return resultado;
            }
        });
    }

    /** SELECT que devuelve como mucho una fila. */
    protected Optional<T> consultarUno(String sql, Object... params) {
        return ejecutarSql(sql, params, Optional.empty(), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRsToClass(rs) : Optional.<T>empty();
            }
        });
    }

    /** SELECT con un unico entero (COUNT...). */
    protected int consultarEntero(String sql, Object... params) {
        return ejecutarSql(sql, params, 0, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        });
    }

    /** SELECT con un unico decimal (AVG, SUM...). */
    protected double consultarDecimal(String sql, Object... params) {
        return ejecutarSql(sql, params, 0.0, ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        });
    }

    /** SELECT clave/cantidad (GROUP BY). Conserva el orden del SQL. */
    protected Map<String, Integer> consultarAgrupado(String sql, Object... params) {
        return ejecutarSql(sql, params, Map.<String, Integer>of(), ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                Map<String, Integer> mapa = new LinkedHashMap<>();
                while (rs.next())
                    mapa.put(rs.getString(1), rs.getInt(2));
                return mapa;
            }
        });
    }

    /** INSERT / UPDATE / DELETE. true si ha afectado a alguna fila. */
    protected boolean ejecutar(String sql, Object... params) {
        return ejecutarSql(sql, params, false, ps -> ps.executeUpdate() > 0);
    }

    // ------------------------------------------------------------------

    @FunctionalInterface
    private interface Operacion<R> {
        R aplicar(PreparedStatement ps) throws SQLException;
    }

    /** Unico sitio donde se abre la conexion, se asignan parametros y se captura el error. */
    private <R> R ejecutarSql(String sql, Object[] params, R porDefecto, Operacion<R> operacion) {
        try (Connection con = obtenerConexion()) {
            if (con == null) {
                return porDefecto;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
                return operacion.aplicar(ps);
            }
        } catch (SQLException | RuntimeException e) {
            registrar(sql, e);
            return porDefecto;
        }
    }

    private boolean idInvalido(ID id) {
        return id == null
                || (id instanceof Integer n && n <= 0)
                || (id instanceof String s && s.isBlank());
    }

    protected boolean textoValido(String texto) {
        return texto != null && !texto.isBlank();
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, "Fallo JDBC: " + operacion, e);
    }
}
