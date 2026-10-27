package es.ejemplo.clinica.dao;

import es.ejemplo.clinica.modelo.Consulta;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public final class ConsultaDao extends AbstractDao<Consulta> implements IConsultaDao {

   private static final String COLUMNAS = "id,id_mascota,id_veterinario,fecha,horas,estado";

   public ConsultaDao(String url) {
      super(url, "consultas", "id", COLUMNAS);
   }

   @Override
   protected Consulta mapRsToClass(ResultSet rs) throws SQLException {
      return new Consulta(rs.getInt("id"), rs.getInt("id_mascota"), rs.getInt("id_veterinario"),
            rs.getString("fecha"), rs.getBigDecimal("horas"), rs.getString("estado"));
   }

   @Override
   public int insertar(Consulta e) {
      if (e == null) {
         return 0;
      }
      return insertarYObtenerId(
            "INSERT INTO consultas (id_mascota,id_veterinario,fecha,horas,estado) VALUES(?,?,?,?,?)",
            e.getIdMascota(), e.getIdVeterinario(), e.getFecha(), e.getHoras(), e.getEstado());
   }

   @Override
   public int actualizar(Consulta e) {
      if (e == null) {
         return 0;
      }
      if (e.getId() == null) {
         throw new IllegalArgumentException("La consulta necesita un id para actualizar");
      }
      return modificar(
            "UPDATE consultas SET id_mascota = ?,id_veterinario = ?,fecha = ?,horas = ?,estado = ? WHERE id = ?",
            e.getIdMascota(), e.getIdVeterinario(), e.getFecha(), e.getHoras(), e.getEstado(), e.getId());
   }

   @Override
   public List<Consulta> buscarPorMascota(Integer idMascota) {
      return consultarLista("SELECT " + COLUMNAS + " FROM consultas WHERE id_mascota = ?", idMascota);
   }

   @Override
   public List<Consulta> buscarPorVeterinario(Integer idVeterinario) {
      return consultarLista("SELECT " + COLUMNAS + " FROM consultas WHERE id_veterinario = ?", idVeterinario);
   }

   @Override
   public List<Consulta> listarProgramadas() {
      return consultarLista("SELECT " + COLUMNAS + " FROM consultas WHERE estado = 'PROGRAMADA'");
   }
}
