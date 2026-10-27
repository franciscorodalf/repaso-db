package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;
import es.ejemplo.clinica.modelo.Tratamiento;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class TratamientoDao extends ConexionBD implements ITratamientoDao {

   public TratamientoDao(String url) {
      super(url);
   }

   protected Tratamiento mapRsToClass(ResultSet rs) throws SQLException {
      return new Tratamiento(rs.getInt("id"), rs.getInt("id_consulta"), rs.getString("descripcion"),
            rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario"));
   }

   @Override
   public List<Tratamiento> listar() {
      String sql = "SELECT id,id_consulta,descripcion,cantidad,precio_unitario from tratamientos";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Tratamiento> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando tratamientos", e);
      }
   }

   @Override
   public Optional<Tratamiento> buscarPorId(Integer id) {
      if (id == null) {
         return Optional.empty();
      }

      String sql = "SELECT id,id_consulta,descripcion,cantidad,precio_unitario from tratamientos WHERE id = ?";

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
         throw new DaoException("Error al buscar por id de tratamiento", e);
      }
   }

   @Override
   public int insertar(Tratamiento entidad) {
      if (entidad == null) {
         return 0;
      }
      String sql = "INSERT INTO tratamientos (id_consulta,descripcion,cantidad,precio_unitario) VALUES(?,?,?,?)";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         statement.setInt(1, entidad.getIdConsulta());
         statement.setString(2, entidad.getDescripcion());
         statement.setInt(3, entidad.getCantidad());
         statement.setBigDecimal(4, entidad.getPrecioUnitario());
         statement.executeUpdate();
         try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         throw new DaoException("Error al insertar tratamiento", e);
      }
   }

   @Override
   public int actualizar(Tratamiento entidad) {
      if (entidad == null) {
         return 0;
      }
      if (entidad.getId() == null) {
         throw new IllegalArgumentException("El tratamiento necesita un id para actualizar");
      }

      String sql = "UPDATE tratamientos SET id_consulta = ?,descripcion = ?,cantidad = ?,precio_unitario = ? WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, entidad.getIdConsulta());
         statement.setString(2, entidad.getDescripcion());
         statement.setInt(3, entidad.getCantidad());
         statement.setBigDecimal(4, entidad.getPrecioUnitario());
         statement.setInt(5, entidad.getId());

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error actualizando tratamiento", e);
      }
   }

   @Override
   public int eliminar(Integer id) {
      String sql = "DELETE FROM tratamientos WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error eliminando tratamiento", e);
      }
   }

   @Override
   public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
      String sql = "SELECT id,id_consulta,descripcion,cantidad,precio_unitario from tratamientos WHERE id_consulta = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, idConsulta);
         try (ResultSet rs = statement.executeQuery()) {
            List<Tratamiento> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando tratamientos por consulta", e);
      }
   }

   @Override
   public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
      String sql = "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) from tratamientos WHERE id_consulta = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, idConsulta);
         try (ResultSet rs = statement.executeQuery()) {
            rs.next();
            return rs.getBigDecimal(1);
         }

      } catch (SQLException e) {
         throw new DaoException("Error calculando coste de tratamientos", e);
      }
   }

}
