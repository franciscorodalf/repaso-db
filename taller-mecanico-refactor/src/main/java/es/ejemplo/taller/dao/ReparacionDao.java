package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Reparacion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class ReparacionDao extends AbstractDao<Reparacion, Integer> {
    private static final List<String> ESTADOS = List.of("PENDIENTE", "EN_PROCESO", "FINALIZADA");

    public ReparacionDao(String url) {
        super(url, "Reparaciones", "id_reparacion",
                "id_reparacion,matricula,id_mecanico,descripcion,fecha,horas_trabajo,estado");
    }

    public int insertar(Reparacion r) {
        if (!datosValidos(r)) {
            return 0;
        }
        return insertarYObtenerId(
                "INSERT INTO Reparaciones (matricula,id_mecanico,descripcion,fecha,horas_trabajo,estado) VALUES (?,?,?,?,?,?)",
                r.getMatricula(), r.getIdMecanico(), r.getDescripcion(), r.getFecha(), r.getHorasTrabajo(),
                r.getEstado());
    }

    public int actualizar(Reparacion r) {
        if (!datosValidos(r) || r.getIdReparacion() == null) {
            return 0;
        }
        return modificar(
                "UPDATE Reparaciones SET matricula = ?, id_mecanico = ?, descripcion = ?, fecha = ?, horas_trabajo = ?, estado = ? WHERE id_reparacion = ?",
                r.getMatricula(), r.getIdMecanico(), r.getDescripcion(), r.getFecha(), r.getHorasTrabajo(),
                r.getEstado(), r.getIdReparacion());
    }

    public List<Reparacion> buscarPorVehiculo(String matricula) {
        if (!textoValido(matricula)) {
            return List.of();
        }
        return consultarLista(
                "SELECT " + columnas + " FROM Reparaciones WHERE matricula = ? ORDER BY id_reparacion", matricula);
    }

    public List<Reparacion> buscarPorMecanico(Integer idMecanico) {
        if (idMecanico == null) {
            return List.of();
        }
        return consultarLista(
                "SELECT " + columnas + " FROM Reparaciones WHERE id_mecanico = ? ORDER BY id_reparacion",
                idMecanico);
    }

    public List<Reparacion> listarPendientes() {
        return consultarLista(
                "SELECT " + columnas + " FROM Reparaciones WHERE estado = 'PENDIENTE' ORDER BY fecha, id_reparacion");
    }

    private boolean datosValidos(Reparacion r) {
        return r != null
                && textoValido(r.getMatricula())
                && r.getIdMecanico() != null
                && textoValido(r.getDescripcion())
                && fechaValida(r.getFecha())
                && r.getHorasTrabajo() != null && r.getHorasTrabajo().signum() >= 0
                && r.getEstado() != null && ESTADOS.contains(r.getEstado());
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
