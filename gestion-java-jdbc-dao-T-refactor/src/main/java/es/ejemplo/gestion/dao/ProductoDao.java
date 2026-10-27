package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.model.Producto;
import java.sql.*;
import java.util.*;

public class ProductoDao extends AbstractDao<Producto, Integer> {

    public ProductoDao(String url) {
        super(url, "Productos", "id", "id,nombre,precio");
    }

    @Override
    protected Optional<Producto> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Producto(rs.getInt("id"), rs.getString("nombre"), rs.getDouble("precio")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass producto", e);
            return Optional.empty();
        }
    }

    public boolean insertar(Producto p) {
        if (!datosValidos(p)) {
            return false;
        }
        return ejecutar("INSERT INTO Productos (id, nombre, precio) VALUES (?,?,?)",
                p.getId(), p.getNombre(), p.getPrecio());
    }

    public boolean actualizar(Producto p) {
        if (!datosValidos(p)) {
            return false;
        }
        return ejecutar("UPDATE Productos SET nombre = ?, precio = ? WHERE id = ?",
                p.getNombre(), p.getPrecio(), p.getId());
    }

    public List<Producto> buscarPorNombre(String texto) {
        if (!textoValido(texto)) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Productos WHERE nombre LIKE ? ORDER BY nombre, id",
                "%" + texto + "%");
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        if (minimo > maximo) {
            return List.of();
        }
        return consultarLista("SELECT " + columnas + " FROM Productos WHERE precio BETWEEN ? AND ? ORDER BY id",
                minimo, maximo);
    }

    public double precioMedio() {
        return consultarDecimal("SELECT COALESCE(AVG(precio), 0) FROM Productos");
    }

    private boolean datosValidos(Producto p) {
        return p != null && p.getId() > 0 && textoValido(p.getNombre()) && p.getPrecio() >= 0;
    }
}
