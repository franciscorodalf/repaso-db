package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class ProductoDao extends ConexionBD{
    //private  ConexionBD conexionBD;

    public ProductoDao(String url) {
        super(url);
    }


    public List<Producto> listar() {
        List<Producto> lista = new ArrayList<>();
        String query = "select id,nombre,precio from productos";

        try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapeoProducto(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        //conexion
        // sql
        // preparateStatement
        //ResultSet rs
        //rs.next
        /**
         * while(rs.next()) {
         * producto = new Producto(rs.get(id). rs.get(no
         *
         * }
         *
         *
         */
        return lista;
    }

    public Optional<Producto> buscarPorId(int id) {
        return null;
    }

    public boolean insertar(Producto producto) {
        String sql = "INSERT INTO Productos (id, nombre, precio) VALUES (?, ?, ?)";
        try (Connection con = this.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, producto.getId());
            ps.setString(2, producto.getNombre());
            ps.setDouble(3, producto.getPrecio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AplicationException("Error al insertar el producto", e);
        }
    }

    public boolean actualizar(Producto producto) {
        return false;
    }

    public boolean eliminar(int id) {
        return false;
    }

    public List<Producto> buscarPorNombre(String texto) {
       return null;
    }

    public List<Producto> buscarPorPrecio(double minimo, double maximo) {
        return null;
    }

    public int contar() {
       return 0;
    }

    public double precioMedio() {
        return 0;
    }

    private Producto mapeoProducto(ResultSet rs) {

        try {
            return new Producto(rs.getInt(1), rs.getString(2),rs.getDouble(3) );
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e.getCause());
        }
    }
}
