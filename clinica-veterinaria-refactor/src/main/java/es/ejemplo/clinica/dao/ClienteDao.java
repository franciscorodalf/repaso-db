package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ClienteDao extends AbstractDao<Cliente> implements IClienteDao {

   public ClienteDao(String url) {
      super(url, "clientes", "id", "id,nombre,email,telefono");
   }

   @Override
   protected Cliente mapRsToClass(ResultSet rs) throws SQLException {
      return new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"), rs.getString("telefono"));
   }

   @Override
   public int insertar(Cliente e) {
      if (e == null) {
         return 0;
      }
      return insertarYObtenerId("INSERT INTO clientes (nombre,email,telefono) VALUES(?,?,?)",
            e.getNombre(), e.getEmail(), e.getTelefono());
   }

   @Override
   public int actualizar(Cliente e) {
      if (e == null) {
         return 0;
      }
      if (e.getId() == null) {
         throw new IllegalArgumentException("El cliente necesita un id para actualizar");
      }
      return modificar("UPDATE clientes SET nombre = ?,email = ?,telefono = ? WHERE id = ?",
            e.getNombre(), e.getEmail(), e.getTelefono(), e.getId());
   }
}
