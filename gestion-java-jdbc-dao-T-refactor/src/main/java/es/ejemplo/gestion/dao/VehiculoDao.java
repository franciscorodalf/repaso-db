package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Vehiculo;
import java.sql.*;
import java.util.*;

/** La clave primaria es la matricula (String). */
public class VehiculoDao extends AbstractDao<Vehiculo, String> {

    public VehiculoDao(String url) {
        super(url, "Vehiculos", "matricula", "matricula,marca,modelo,anio");
    }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(rs.getString("matricula"), rs.getString("marca"),
                    rs.getString("modelo"), rs.getInt("anio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }

    /** Alias de buscarPorId: la matricula es la clave. */
    public Optional<Vehiculo> buscarPorMatricula(String matricula) {
        return buscarPorId(matricula);
    }

    public boolean insertar(Vehiculo v) {
        if (!datosValidos(v)) {
            return false;
        }
        return ejecutar("INSERT INTO Vehiculos (matricula, marca, modelo, anio) VALUES (?,?,?,?)",
                v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnio());
    }

    public boolean actualizar(Vehiculo v) {
        if (!datosValidos(v) || v.getAnio() < 1886) {
            return false;
        }
        return ejecutar("UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ? WHERE matricula = ?",
                v.getMarca(), v.getModelo(), v.getAnio(), v.getMatricula());
    }

    public List<Vehiculo> buscarPorMarca(String texto) {
        if (!textoValido(texto)) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Vehiculos WHERE marca LIKE ? ORDER BY marca, matricula",
                "%" + texto + "%");
    }

    public List<Vehiculo> buscarPorAnio(int desde, int hasta) {
        if (desde > hasta) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Vehiculos WHERE anio BETWEEN ? AND ? ORDER BY matricula",
                desde, hasta);
    }

    public Map<String, Integer> contarPorMarca() {
        return consultarAgrupado("SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca ORDER BY marca");
    }

    private boolean datosValidos(Vehiculo v) {
        return v != null && textoValido(v.getMatricula())
                && v.getMatricula().equals(v.getMatricula().trim().toUpperCase())
                && textoValido(v.getMarca()) && textoValido(v.getModelo());
    }
}
