# Versiones para el examen

Cada proyecto tiene la version original y una version `-refactor` (mismos tests, todos en verde).

| Original | Refactor | Tests |
|---|---|---|
| `clinica-veterinaria-alumnado` | `clinica-veterinaria-refactor` | 90/90 |
| `taller-mecanico-alumnado` | `taller-mecanico-refactor` | 90/90 |
| `gestion-java-jdbc-dao-T` | `gestion-java-jdbc-dao-T-refactor` | 38/38 |

## Que cambia en las versiones `-refactor`

`AbstractDao<T>` (o `<T, ID>`) concentra todo el JDBC repetitivo. El DAO hijo solo define:

1. Constructor: `super(url, "tabla", "columna_id", "col1,col2,...")`
2. `mapRsToClass(ResultSet)`
3. `insertar` / `actualizar` (validacion + una llamada a la plantilla)
4. Sus consultas propias (una linea cada una)

Plantillas de la clase base (los parametros se pasan como varargs, sin `ps.setXxx`):

| Metodo | Uso |
|---|---|
| `consultarLista(sql, params...)` | SELECT de varias filas |
| `consultarUno(sql, params...)` | SELECT de una fila -> `Optional` |
| `consultarDecimal / consultarEntero / consultarAgrupado` | SUM, COUNT, GROUP BY |
| `modificar(sql, params...)` / `ejecutar(...)` | INSERT, UPDATE, DELETE |
| `insertarYObtenerId(sql, params...)` | INSERT con id autogenerado |

Heredados sin escribir nada: `listar()`, `buscarPorId(id)`, `eliminar(id)` (y `contar()` en gestion).

Se conserva el manejo de errores de cada original: clinica lanza `DaoException`; taller y gestion
registran el error y devuelven un valor neutro (0, lista vacia, `Optional.empty()`, `false`).
