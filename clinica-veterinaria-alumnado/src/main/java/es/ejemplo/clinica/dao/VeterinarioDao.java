package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;
import es.ejemplo.clinica.modelo.Veterinario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VeterinarioDao extends ConexionBD implements IVeterinarioDao {

   public VeterinarioDao(String url) {
      super(url);
   }

   protected Veterinario mapRsToClass(ResultSet rs) throws SQLException {
      return new Veterinario(rs.getInt("id"), rs.getString("nombre"), rs.getString("especialidad"),
            rs.getBigDecimal("tarifa_hora"));
   }

   @Override
   public List<Veterinario> listar() {
      String sql = "SELECT id,nombre,especialidad,tarifa_hora from veterinarios";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Veterinario> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando veterinarios", e);
      }
   }

   @Override
   public Optional<Veterinario> buscarPorId(Integer id) {
      if (id == null) {
         return Optional.empty();
      }

      String sql = "SELECT id,nombre,especialidad,tarifa_hora from veterinarios WHERE id = ?";

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
         throw new DaoException("Error al buscar por id de veterinario", e);
      }
   }

   @Override
   public int insertar(Veterinario entidad) {
      if (entidad == null) {
         return 0;
      }
      String sql = "INSERT INTO veterinarios (nombre,especialidad,tarifa_hora) VALUES(?,?,?)";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEspecialidad());
         statement.setBigDecimal(3, entidad.getTarifaHora());
         statement.executeUpdate();
         try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         throw new DaoException("Error al insertar veterinario", e);
      }
   }

   @Override
   public int actualizar(Veterinario entidad) {
      if (entidad == null) {
         return 0;
      }
      if (entidad.getId() == null) {
         throw new IllegalArgumentException("El veterinario necesita un id para actualizar");
      }

      String sql = "UPDATE veterinarios SET nombre = ?,especialidad = ?,tarifa_hora = ? WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEspecialidad());
         statement.setBigDecimal(3, entidad.getTarifaHora());
         statement.setInt(4, entidad.getId());

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error actualizando veterinario", e);
      }
   }

   @Override
   public int eliminar(Integer id) {
      String sql = "DELETE FROM veterinarios WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error eliminando veterinario", e);
      }
   }

   @Override
   public List<Veterinario> buscarPorEspecialidad(String especialidad) {
      String sql = "SELECT id,nombre,especialidad,tarifa_hora from veterinarios WHERE especialidad = ? ORDER BY tarifa_hora";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setString(1, especialidad);
         try (ResultSet rs = statement.executeQuery()) {
            List<Veterinario> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error buscando veterinarios por especialidad", e);
      }
   }

}
