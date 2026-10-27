package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Mascota;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class MascotaDao extends AbstractDao<Mascota> implements IMascotaDao {

   private static final String COLUMNAS = "id,nombre,especie,raza,fecha_nacimiento,id_cliente";

   public MascotaDao(String url) {
      super(url, "mascotas", "id", COLUMNAS);
   }

   @Override
   protected Mascota mapRsToClass(ResultSet rs) throws SQLException {
      return new Mascota(rs.getInt("id"), rs.getString("nombre"), rs.getString("especie"), rs.getString("raza"),
            rs.getString("fecha_nacimiento"), rs.getInt("id_cliente"));
   }

   @Override
   public int insertar(Mascota e) {
      if (e == null) {
         return 0;
      }
      return insertarYObtenerId(
            "INSERT INTO mascotas (nombre,especie,raza,fecha_nacimiento,id_cliente) VALUES(?,?,?,?,?)",
            e.getNombre(), e.getEspecie(), e.getRaza(), e.getFechaNacimiento(), e.getIdCliente());
   }

   @Override
   public int actualizar(Mascota e) {
      if (e == null) {
         return 0;
      }
      if (e.getId() == null) {
         throw new IllegalArgumentException("La mascota necesita un id para actualizar");
      }
      return modificar(
            "UPDATE mascotas SET nombre = ?,especie = ?,raza = ?,fecha_nacimiento = ?,id_cliente = ? WHERE id = ?",
            e.getNombre(), e.getEspecie(), e.getRaza(), e.getFechaNacimiento(), e.getIdCliente(), e.getId());
   }

   @Override
   public List<Mascota> buscarPorCliente(Integer idCliente) {
      return consultarLista("SELECT " + COLUMNAS + " FROM mascotas WHERE id_cliente = ?", idCliente);
   }

   @Override
   public List<Mascota> listarPorEspecie(String especie) {
      return consultarLista("SELECT " + COLUMNAS + " FROM mascotas WHERE especie = ?", especie);
   }
}
