package es.ejemplo.taller.dao;

import es.ejemplo.taller.modelo.Cliente;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClienteDao extends AbstractDao<Cliente, Integer> {

    public ClienteDao(String url) {
        super(url, "Clientes", "id_cliente", "id_cliente,nombre,email,telefono");
    }

    public int insertar(Cliente c) {
        if (c == null || !textoValido(c.getNombre()) || !textoValido(c.getEmail())) {
            return 0;
        }
        return insertarYObtenerId("INSERT INTO Clientes (nombre,email,telefono) VALUES (?,?,?)",
                c.getNombre(), c.getEmail(), c.getTelefono());
    }

    public int actualizar(Cliente c) {
        if (c == null || c.getIdCliente() == null || !textoValido(c.getNombre()) || !textoValido(c.getEmail())) {
            return 0;
        }
        return modificar("UPDATE Clientes SET nombre = ?, email = ?, telefono = ? WHERE id_cliente = ?",
                c.getNombre(), c.getEmail(), c.getTelefono(), c.getIdCliente());
    }

    @Override
    protected Optional<Cliente> mapRsToClass(ResultSet rs) {
        try {
            return Optional.of(new Cliente(rs.getInt("id_cliente"), rs.getString("nombre"),
                    rs.getString("email"), rs.getString("telefono")));
        } catch (SQLException | RuntimeException e) {
            registrar("mapRsToClass cliente", e);
            return Optional.empty();
        }
    }
}
