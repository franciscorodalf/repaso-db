package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Vehiculo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** La clave primaria es la matricula (String). */
public class VehiculoDao extends AbstractDao<Vehiculo, String> {

    public VehiculoDao(String url) {
        super(url, "Vehiculos", "matricula", "matricula,marca,modelo,anio,id_cliente");
    }

    /** La matricula no es autogenerada: se devuelve 1 si se inserta, 0 si no. */
    public int insertar(Vehiculo v) {
        if (!datosValidos(v)) {
            return 0;
        }
        return modificar("INSERT INTO Vehiculos (matricula,marca,modelo,anio,id_cliente) VALUES (?,?,?,?,?)",
                v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnio(), v.getIdCliente());
    }

    public int actualizar(Vehiculo v) {
        if (!datosValidos(v)) {
            return 0;
        }
        return modificar("UPDATE Vehiculos SET marca = ?, modelo = ?, anio = ?, id_cliente = ? WHERE matricula = ?",
                v.getMarca(), v.getModelo(), v.getAnio(), v.getIdCliente(), v.getMatricula());
    }

    public List<Vehiculo> buscarPorCliente(Integer idCliente) {
        if (idCliente == null) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Vehiculos WHERE id_cliente = ?", idCliente);
    }

    public List<Vehiculo> listarPorMarca(String marca) {
        if (!textoValido(marca)) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Vehiculos WHERE marca = ?", marca);
    }

    private boolean datosValidos(Vehiculo v) {
        return v != null && textoValido(v.getMatricula()) && textoValido(v.getMarca())
                && textoValido(v.getModelo()) && v.getAnio() != null && v.getIdCliente() != null;
    }

    @Override
    protected Optional<Vehiculo> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Vehiculo(rs.getString("matricula"), rs.getString("marca"),
                    rs.getString("modelo"), rs.getInt("anio"), rs.getInt("id_cliente")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass vehiculo", e);
            return Optional.empty();
        }
    }
}
