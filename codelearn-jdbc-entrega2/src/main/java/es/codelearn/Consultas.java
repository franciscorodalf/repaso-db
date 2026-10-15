package es.codelearn;


import java.util.List;
import java.util.ArrayList;

/** Ejercicios del alumnado: completa los 12 métodos sin cambiar sus firmas. */
public final class Consultas {
    private Consultas() {
    }

    /** Ejercicio: COUNT(*) de Productos; debe devolver 0 si la tabla está vacía. */
    public static int contarProductos() {
        String sql = "SELECT COUNT(*) from Productos";
        try (var conectar = BaseDatos.conectar();
                var statement = conectar.prepareStatement(sql)) {
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return -1;
        }
    }

    /** Ejercicio: COUNT(*) de Vehiculos. */
    public static int contarVehiculos() {
        String sql = "Select COUNT(*) FROM Vehiculos";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return -1;
        }
    }

    /** Ejercicio: EXISTS o COUNT de Productos filtrando por id con ?. */
    public static boolean existeProducto(int id) {
        String sql = "Select COUNT(1) from Productos where id = ?";
        boolean existe = false;
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
                return existe;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    /** Ejercicio: EXISTS o COUNT de Vehiculos filtrando por matrícula con ?. */
    public static boolean existeVehiculo(String matricula) {
        String sql = "Select COUNT(1) from Vehiculos where matricula = ?";
        boolean existe = false;
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, matricula);
            try (var rs = statement.executeQuery()) {
                if (rs.next()) {
                    existe = rs.getInt(1) > 0;
                }
                return existe;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return false;
        }
    }

    /** Ejercicio: COUNT(*) con precio estrictamente MAYOR que el parámetro. */
    public static int contarProductosCaros(double precio) {
        String sql = "Select COUNT(*) from Productos where precio > ?";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, precio);
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return -1;
        }

    }

    /** Ejercicio: COUNT(*) de la marca exacta proporcionada. */
    public static int contarVehiculosMarca(String marca) {
        String sql = "Select COUNT(*) from Vehiculos where marca = ?";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            statement.setString(1, marca);
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return -1;
        }

    }

    /** Ejercicio: AVG(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMedio() {
        String sql = "Select AVG(precio) from Productos";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return Double.NaN;
        }
    }

    /** Ejercicio: MAX(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMaximo() {
        String sql = "Select MAX(precio) from Productos";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return Double.NaN;
        }
    }

    /** Ejercicio: MIN(precio); para tabla vacía devolver 0.0. */
    public static double obtenerPrecioMinimo() {
        String sql = "Select MIN(precio) from Productos";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            try (var rs = statement.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return Double.NaN;
        }
    }

    /** Ejercicio: DISTINCT marca de Vehiculos ordenadas alfabéticamente ASC. */
    public static List<String> listarMarcas() {
        String sql = "Select DISTINCT marca from Vehiculos ORDER BY marca asc";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            List<String> list = new ArrayList<>();
            try (var rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
                return list;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Ejercicio: Nombres de Productos con precio > parámetro, ORDER BY precio DESC,
     * nombre ASC.
     */
    public static List<String> listarProductosCaros(double precio) {
        String sql = "Select nombre from Productos where precio > ? ORDER BY precio DESC,nombre ASC";
        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            List<String> list = new ArrayList<>();
            statement.setDouble(1, precio);
            try (var rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
                return list;
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return List.of();
        }

    }

    /**
     * Ejercicio: Marcas agrupadas con COUNT(*) >= minimo; ORDER BY cantidad DESC,
     * marca ASC.
     */
    public static List<String> listarMarcasFrecuentes(int minimo) {
        String sql = "SELECT marca, COUNT(*) AS cantidad FROM Vehiculos GROUP BY marca HAVING COUNT(*) >= ? ORDER BY cantidad DESC, marca ASC";

        try (var connection = BaseDatos.conectar();
                var statement = connection.prepareStatement(sql)) {
            List<String> list = new ArrayList<>();
            statement.setInt(1, minimo);
            try (var rs = statement.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString(1));
                }
                return list;
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            return List.of();
        }
    }

}