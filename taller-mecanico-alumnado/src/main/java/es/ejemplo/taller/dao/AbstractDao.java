package es.ejemplo.taller.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractDao<T> {

    private static final Logger LOG = Logger.getLogger(AbstractDao.class.getName());

    protected abstract Optional<T> mapRsToClass(ResultSet rs);

    protected List<T> consultar(PreparedStatement ps) {
        if (ps == null)
            return List.of();
        try (ResultSet rs = ps.executeQuery()) {
            List<T> resultado = new ArrayList<>();
            while (rs.next()) {
                Optional<T> entidad = mapRsToClass(rs);
                if (entidad.isEmpty())
                    return List.of();
                resultado.add(entidad.get());
            }
            return resultado;
        } catch (SQLException e) {
            registrar("Fallo en la consulta general: ", e);
            return List.of();
        }
    }

    protected Optional<T> consultarUno(PreparedStatement ps) {
        if (ps == null)
            return Optional.empty();
        try (ResultSet rs = ps.executeQuery()) {
            return rs.next() ? mapRsToClass(rs) : Optional.empty();
        } catch (SQLException e) {
            registrar("Fallo en la consulta unica: ", e);
            return Optional.empty();
        }
    }

    protected int ejecutar(PreparedStatement ps) {
        if (ps == null)
            return 0;
        try {
            return ps.executeUpdate();
        } catch (SQLException e) {
            registrar("Fallo en la consulta de Ejecutar: ", e);
            return 0;
        }
    }

    protected int insertarId(PreparedStatement ps) {
        try {
            if (ps == null) {
                return 0;
            }

            int filas = ps.executeUpdate();
            if (filas == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            registrar("Fallo en la consulta de Insertar: ", e);
            return 0;
        }
    }

    protected BigDecimal consultarDecimal(PreparedStatement ps) {
        if (ps == null)
            return BigDecimal.ZERO;
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            registrar("consultarDecimal", e);
            return BigDecimal.ZERO;
        }
    }

    protected boolean textoValido(String texto) {
        return texto != null && !texto.isBlank();
    }

    protected void registrar(String operacion, Exception e) {
        LOG.log(Level.WARNING, operacion, e);
    }
}
