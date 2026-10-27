package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Tratamiento;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class TratamientoDao extends AbstractDao<Tratamiento> implements ITratamientoDao {

   private static final String COLUMNAS = "id,id_consulta,descripcion,cantidad,precio_unitario";

   public TratamientoDao(String url) {
      super(url, "tratamientos", "id", COLUMNAS);
   }

   @Override
   protected Tratamiento mapRsToClass(ResultSet rs) throws SQLException {
      return new Tratamiento(rs.getInt("id"), rs.getInt("id_consulta"), rs.getString("descripcion"),
            rs.getInt("cantidad"), rs.getBigDecimal("precio_unitario"));
   }

   @Override
   public int insertar(Tratamiento e) {
      if (e == null) {
         return 0;
      }
      return insertarYObtenerId(
            "INSERT INTO tratamientos (id_consulta,descripcion,cantidad,precio_unitario) VALUES(?,?,?,?)",
            e.getIdConsulta(), e.getDescripcion(), e.getCantidad(), e.getPrecioUnitario());
   }

   @Override
   public int actualizar(Tratamiento e) {
      if (e == null) {
         return 0;
      }
      if (e.getId() == null) {
         throw new IllegalArgumentException("El tratamiento necesita un id para actualizar");
      }
      return modificar(
            "UPDATE tratamientos SET id_consulta = ?,descripcion = ?,cantidad = ?,precio_unitario = ? WHERE id = ?",
            e.getIdConsulta(), e.getDescripcion(), e.getCantidad(), e.getPrecioUnitario(), e.getId());
   }

   @Override
   public List<Tratamiento> listarPorConsulta(Integer idConsulta) {
      return consultarLista("SELECT " + COLUMNAS + " FROM tratamientos WHERE id_consulta = ?", idConsulta);
   }

   @Override
   public BigDecimal calcularCosteTratamientos(Integer idConsulta) {
      return consultarDecimal(
            "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM tratamientos WHERE id_consulta = ?",
            idConsulta);
   }
}
