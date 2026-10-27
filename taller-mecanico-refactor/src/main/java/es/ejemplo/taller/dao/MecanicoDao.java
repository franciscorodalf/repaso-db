package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Mecanico;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MecanicoDao extends AbstractDao<Mecanico, Integer> {

    public MecanicoDao(String url) {
        super(url, "Mecanicos", "id_mecanico", "id_mecanico,nombre,especialidad,tarifa_hora");
    }

    public int insertar(Mecanico m) {
        if (!datosValidos(m)) {
            return 0;
        }
        return insertarYObtenerId("INSERT INTO Mecanicos (nombre,especialidad,tarifa_hora) VALUES (?,?,?)",
                m.getNombre(), m.getEspecialidad(), m.getTarifaHora());
    }

    public int actualizar(Mecanico m) {
        if (!datosValidos(m) || m.getIdMecanico() == null) {
            return 0;
        }
        return modificar("UPDATE Mecanicos SET nombre = ?, especialidad = ?, tarifa_hora = ? WHERE id_mecanico = ?",
                m.getNombre(), m.getEspecialidad(), m.getTarifaHora(), m.getIdMecanico());
    }

    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        if (!textoValido(especialidad)) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Mecanicos WHERE especialidad = ?", especialidad);
    }

    private boolean datosValidos(Mecanico m) {
        return m != null && textoValido(m.getNombre()) && textoValido(m.getEspecialidad())
                && m.getTarifaHora() != null && m.getTarifaHora().signum() >= 0;
    }

    @Override
    protected Optional<Mecanico> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Mecanico(rs.getInt("id_mecanico"), rs.getString("nombre"),
                    rs.getString("especialidad"), rs.getBigDecimal("tarifa_hora")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass mecanico", e);
            return Optional.empty();
        }
    }
}
