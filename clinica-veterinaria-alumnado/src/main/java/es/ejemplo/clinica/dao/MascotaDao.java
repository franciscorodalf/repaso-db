package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.config.ConexionBD;
import es.ejemplo.clinica.modelo.Mascota;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class MascotaDao extends ConexionBD implements IMascotaDao {

   public MascotaDao(String url) {
      super(url);
   }

   protected Mascota mapRsToClass(ResultSet rs) throws SQLException {
      return new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"), rs.getString("raza"),
            rs.getString("fecha_nacimiento"), rs.getInt("id_cliente"));
   }

   @Override
   public List<Mascota> listar() {
      String sql = "SELECT id,nombre,especie,raza,fecha_nacimiento,id_cliente from mascotas";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         try (ResultSet rs = statement.executeQuery()) {
            List<Mascota> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando mascotas", e);
      }
   }

   @Override
   public Optional<Mascota> buscarPorId(Integer id) {
      if (id == null) {
         return Optional.empty();
      }

      String sql = "SELECT id,nombre,especie,raza,fecha_nacimiento,id_cliente from mascotas WHERE id = ?";

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
         throw new DaoException("Error al buscar por id de mascota", e);
      }
   }

   @Override
   public int insertar(Mascota entidad) {
      if (entidad == null) {
         return 0;
      }
      String sql = "INSERT INTO mascotas (nombre,especie,raza,fecha_nacimiento,id_cliente) VALUES(?,?,?,?,?)";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEspecie());
         statement.setString(3, entidad.getRaza());
         statement.setString(4, entidad.getFechaNacimiento());
         statement.setInt(5, entidad.getIdCliente());
         statement.executeUpdate();
         try (ResultSet rs = statement.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
         }
      } catch (SQLException e) {
         throw new DaoException("Error al insertar mascota", e);
      }
   }

   @Override
   public int actualizar(Mascota entidad) {
      if (entidad == null) {
         return 0;
      }
      if (entidad.getId() == null) {
         throw new IllegalArgumentException("La mascota necesita un id para actualizar");
      }

      String sql = "UPDATE mascotas SET nombre = ?,especie = ?,raza = ?,fecha_nacimiento = ?,id_cliente = ? WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setString(1, entidad.getNombre());
         statement.setString(2, entidad.getEspecie());
         statement.setString(3, entidad.getRaza());
         statement.setString(4, entidad.getFechaNacimiento());
         statement.setInt(5, entidad.getIdCliente());
         statement.setInt(6, entidad.getId());

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error actualizando mascota", e);
      }
   }

   @Override
   public int eliminar(Integer id) {
      String sql = "DELETE FROM mascotas WHERE id = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, id);

         return statement.executeUpdate();

      } catch (SQLException e) {
         throw new DaoException("Error eliminando mascota", e);
      }
   }

   @Override
   public List<Mascota> buscarPorCliente(Integer idCliente) {
      String sql = "SELECT id,nombre,especie,raza,fecha_nacimiento,id_cliente from mascotas WHERE id_cliente = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setInt(1, idCliente);
         try (ResultSet rs = statement.executeQuery()) {
            List<Mascota> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error buscando mascotas por cliente", e);
      }
   }

   @Override
   public List<Mascota> listarPorEspecie(String especie) {
      String sql = "SELECT id,nombre,especie,raza,fecha_nacimiento,id_cliente from mascotas WHERE especie = ?";

      try (Connection connection = obtenerConexion();
            PreparedStatement statement = connection.prepareStatement(sql)) {
         statement.setString(1, especie);
         try (ResultSet rs = statement.executeQuery()) {
            List<Mascota> list = new ArrayList<>();
            while (rs.next()) {
               list.add(mapRsToClass(rs));
            }
            return list;
         }

      } catch (SQLException e) {
         throw new DaoException("Error listando mascotas por especie", e);
      }
   }

}
