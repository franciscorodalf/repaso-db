package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;
import es.ejemplo.clinica.modelo.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ClienteDao extends ConexionBD implements IClienteDao {

   public ClienteDao(String url) {
      super(url);
   }

   protected Cliente mapRsToClass(ResultSet rs) throws SQLException {
      return new Cliente(rs.getInt("id"), rs.getString("nombre"), rs.getString("email"), rs.getString("telefono"));
   }

   @Override
   public List<Cliente> listar() {
      String sql = "SELECT id,nombre,email,telefono from Clientes";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Cliente> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando clientes", e);
      }
   }

   @Override
   public Optional<Cliente> buscarPorId(Integer id) {
      if (id == null) {
         return Optional.empty();
      }

      String sql = "SELECT id,nombre,email,telefono from clientes WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);
         try (ResultSet rs = statement.executeQuery()) {
            if (rs.next()) {
               return Optional.of(mapRsToClass(rs));
            }
            return Optional.empty();
         }

      } catch (SQLException e) {
         throw new DaoException("Error al buscar por id de cliente", e);
      }
   }

   @Override
   public int insertar(Cliente entidad) {
      if (entidad == null) {
         return 0;
      }
      String sql = "INSERT INTO Clientes (nombre,email,telefono) VALUES(?,?,?)";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEmail());
         statement.setString(3, entidad.getTelefono());
         statement.executeUpdate();
         try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         throw new DaoException("Error al insertar cliente", e);
      }
   }

   @Override
   public int actualizar(Cliente entidad) {
      if (entidad == null) {
         return 0;
      }
      if (entidad.getId() == null) {
         throw new IllegalArgumentException("El cliente necesita un id para actualizar");
      }

      String sql = "UPDATE clientes SET nombre = ?,email = ?,telefono = ? WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEmail());
         statement.setString(3, entidad.getTelefono());
         statement.setInt(4, entidad.getId());

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error actualizando cliente", e);
      }
   }

   @Override
   public int eliminar(Integer id) {
      String sql = "DELETE FROM clientes WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error eliminando cliente", e);
      }
   }

}
