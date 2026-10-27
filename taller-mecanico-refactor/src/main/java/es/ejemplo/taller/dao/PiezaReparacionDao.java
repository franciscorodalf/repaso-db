package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.PiezaReparacion;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PiezaReparacionDao extends AbstractDao<PiezaReparacion, Integer> {

    public PiezaReparacionDao(String url) {
        super(url, "piezas_reparacion", "id_pieza", "id_pieza,id_reparacion,nombre,cantidad,precio_unitario");
    }

    public int insertar(PiezaReparacion p) {
        if (!datosValidos(p)) {
            return 0;
        }
        return insertarYObtenerId(
                "INSERT INTO piezas_reparacion (id_reparacion,nombre,cantidad,precio_unitario) VALUES (?,?,?,?)",
                p.getIdReparacion(), p.getNombre(), p.getCantidad(), p.getPrecioUnitario());
    }

    public int actualizar(PiezaReparacion p) {
        if (!datosValidos(p) || p.getIdPieza() == null) {
            return 0;
        }
        return modificar(
                "UPDATE piezas_reparacion SET id_reparacion = ?, nombre = ?, cantidad = ?, precio_unitario = ? WHERE id_pieza = ?",
                p.getIdReparacion(), p.getNombre(), p.getCantidad(), p.getPrecioUnitario(), p.getIdPieza());
    }

    public List<PiezaReparacion> listarPorReparacion(Integer idReparacion) {
        if (idReparacion == null) {
            return List.of();
        }
        return consultarLista(
                "SELECT " + columnas + " FROM piezas_reparacion WHERE id_reparacion = ? ORDER BY id_pieza",
                idReparacion);
    }

    public BigDecimal calcularCostePiezas(Integer idReparacion) {
        if (idReparacion == null) {
            return BigDecimal.ZERO;
        }
        // COALESCE: sin piezas, SUM devuelve NULL y se convierte en 0
        return consultarDecimal(
                "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM piezas_reparacion WHERE id_reparacion = ?",
                idReparacion);
    }

    private boolean datosValidos(PiezaReparacion p) {
        return p != null
                && p.getIdReparacion() != null
                && textoValido(p.getNombre())
                && p.getCantidad() != null && p.getCantidad() > 0
                && p.getPrecioUnitario() != null && p.getPrecioUnitario().signum() >= 0;
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
