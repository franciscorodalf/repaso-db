package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;
import es.ejemplo.clinica.modelo.Consulta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConsultaDao extends ConexionBD implements IConsultaDao {

   public ConsultaDao(String url) {
      super(url);
   }

   protected Consulta mapRsToClass(ResultSet rs) throws SQLException {
      return new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
            rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado"));
   }

   @Override
   public List<Consulta> listar() {
      String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado from consultas";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Consulta> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando consultas", e);
      }
   }

   @Override
   public Optional<Consulta> buscarPorId(Integer id) {
      if (id == null) {
         return Optional.empty();
      }

      String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado from consultas WHERE id = ?";

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
         throw new DaoException("Error al buscar por id de consulta", e);
      }
   }

   @Override
   public int insertar(Consulta entidad) {
      if (entidad == null) {
         return 0;
      }
      String sql = "INSERT INTO consultas (id_mascota,id_veterinario,fecha,horas,estado) VALUES(?,?,?,?,?)";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         statement.setInt(1, entidad.getIdMascota());
         statement.setInt(2, entidad.getIdVeterinario());
         statement.setString(3, entidad.getFecha());
         statement.setBigDecimal(4, entidad.getHoras());
         statement.setString(5, entidad.getEstado());
         statement.executeUpdate();
         try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         throw new DaoException("Error al insertar consulta", e);
      }
   }

   @Override
   public int actualizar(Consulta entidad) {
      if (entidad == null) {
         return 0;
      }
      if (entidad.getId() == null) {
         throw new IllegalArgumentException("La consulta necesita un id para actualizar");
      }

      String sql = "UPDATE consultas SET id_mascota = ?,id_veterinario = ?,fecha = ?,horas = ?,estado = ? WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, entidad.getIdMascota());
         statement.setInt(2, entidad.getIdVeterinario());
         statement.setString(3, entidad.getFecha());
         statement.setBigDecimal(4, entidad.getHoras());
         statement.setString(5, entidad.getEstado());
         statement.setInt(6, entidad.getId());

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error actualizando consulta", e);
      }
   }

   @Override
   public int eliminar(Integer id) {
      String sql = "DELETE FROM consultas WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error eliminando consulta", e);
      }
   }

   @Override
   public List<Consulta> buscarPorMascota(Integer idMascota) {
      String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado from consultas WHERE id_mascota = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, idMascota);
         try (ResultSet rs = statement.executeQuery()) {
            List<Consulta> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error buscando consultas por mascota", e);
      }
   }

   @Override
   public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
      String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado from consultas WHERE id_veterinario = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, idVeterinario);
         try (ResultSet rs = statement.executeQuery()) {
            List<Consulta> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error buscando consultas por veterinario", e);
      }
   }

   @Override
   public List<Consulta> listarProgramadas() {
      String sql = "SELECT id,id_mascota,id_veterinario,fecha,horas,estado from consultas WHERE estado = 'PROGRAMADA'";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Consulta> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando consultas programadas", e);
      }
   }

}
