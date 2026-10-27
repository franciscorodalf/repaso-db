package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Veterinario;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class VeterinarioDao extends AbstractDao<Veterinario> implements IVeterinarioDao {

   private static final String COLUMNAS = "id,nombre,especialidad,tarifa_hora";

   public VeterinarioDao(String url) {
      super(url, "veterinarios", "id", COLUMNAS);
   }

   @Override
   protected Veterinario mapRsToClass(ResultSet rs) throws SQLException {
      return new Veterinario(rs.getInt("id"), rs.getString("nombre"), rs.getString("especialidad"),
            rs.getBigDecimal("tarifa_hora"));
   }

   @Override
   public int insertar(Veterinario e) {
      if (e == null) {
         return 0;
      }
      return insertarYObtenerId("INSERT INTO veterinarios (nombre,especialidad,tarifa_hora) VALUES(?,?,?)",
            e.getNombre(), e.getEspecialidad(), e.getTarifaHora());
   }

   @Override
   public int actualizar(Veterinario e) {
      if (e == null) {
         return 0;
      }
      if (e.getId() == null) {
         throw new IllegalArgumentException("El veterinario necesita un id para actualizar");
      }
      return modificar("UPDATE veterinarios SET nombre = ?,especialidad = ?,tarifa_hora = ? WHERE id = ?",
            e.getNombre(), e.getEspecialidad(), e.getTarifaHora(), e.getId());
   }

   @Override
   public List<Veterinario> buscarPorEspecialidad(String especialidad) {
      return consultarLista("SELECT " + COLUMNAS + " FROM veterinarios WHERE especialidad = ? ORDER BY tarifa_hora",
            especialidad);
   }
}
