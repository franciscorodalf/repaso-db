-- 1. Lista los clientes ordenados por nombre.
-- ORDER BY ordena el resultado; por defecto es ascendente (A-Z).
SELECT id_cliente, nombre, email, telefono
FROM clientes
ORDER BY nombre;

-- 2. Consulta matrícula, marca, modelo y año de los vehículos de una marca elegida.
-- La marca elegida es 'Seat'. El filtro es una igualdad exacta (distingue mayúsculas en el valor).
SELECT matricula, marca, modelo, anio
FROM vehiculos
WHERE marca = 'Seat';

-- 3. Obtén los vehículos de un cliente utilizando su identificador.
-- Cliente con id_cliente = 1.
SELECT matricula, marca, modelo, anio, id_cliente
FROM vehiculos
WHERE id_cliente = 1;

-- 4. Lista los mecánicos de una especialidad y ordénalos por tarifa.
-- Especialidad 'Motor'; ORDER BY tarifa_hora (menor a mayor).
SELECT id_mecanico, nombre, especialidad, tarifa_hora
FROM mecanicos
WHERE especialidad = 'Motor'
ORDER BY tarifa_hora;

-- 5. Obtén las reparaciones pendientes, ordenadas por fecha.
-- Las fechas se guardan como AAAA-MM-DD, así que ordenar el texto equivale a ordenar por fecha.
SELECT id_reparacion, matricula, id_mecanico, descripcion, fecha, horas_trabajo, estado
FROM reparaciones
WHERE estado = 'PENDIENTE'
ORDER BY fecha;

-- 6a. Matrícula de cada vehículo y nombre de su propietario, relacionando con WHERE.
-- Producto cartesiano de las dos tablas filtrado por la condición de relación.
SELECT v.matricula, c.nombre
FROM vehiculos v, clientes c
WHERE v.id_cliente = c.id_cliente;

-- 6b. La misma consulta con INNER JOIN.
-- Devuelve la misma información; la condición de relación va en ON.
SELECT v.matricula, c.nombre
FROM vehiculos v
INNER JOIN clientes c ON v.id_cliente = c.id_cliente;

-- 7. Reparaciones con el nombre del cliente, la matrícula y el nombre del mecánico.
-- Se encadenan tres JOIN: reparaciones -> vehiculos -> clientes, y reparaciones -> mecanicos.
SELECT r.id_reparacion, c.nombre AS cliente, v.matricula, m.nombre AS mecanico
FROM reparaciones r
INNER JOIN vehiculos v ON r.matricula = v.matricula
INNER JOIN clientes c ON v.id_cliente = c.id_cliente
INNER JOIN mecanicos m ON r.id_mecanico = m.id_mecanico
ORDER BY r.id_reparacion;

-- 8. Cuenta los vehículos de cada cliente, incluyendo clientes sin vehículos.
-- LEFT JOIN conserva todos los clientes aunque no tengan vehículos (v.matricula queda NULL).
-- COUNT(v.matricula) no cuenta los NULL, así que esos clientes salen con 0.
SELECT c.id_cliente, c.nombre, COUNT(v.matricula) AS num_vehiculos
FROM clientes c
LEFT JOIN vehiculos v ON v.id_cliente = c.id_cliente
GROUP BY c.id_cliente, c.nombre
ORDER BY c.id_cliente;

-- 9. Coste de piezas por reparación, incluyendo las reparaciones sin piezas con coste cero.
-- LEFT JOIN para no perder las reparaciones sin piezas; SUM(cantidad * precio_unitario) por reparación.
-- Para una reparación sin piezas SUM devuelve NULL: COALESCE lo sustituye por 0.
SELECT r.id_reparacion,
       COALESCE(SUM(p.cantidad * p.precio_unitario), 0) AS coste_piezas
FROM reparaciones r
LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
GROUP BY r.id_reparacion
ORDER BY r.id_reparacion;

-- 10. Reparaciones que no tienen piezas asociadas.
-- LEFT JOIN y nos quedamos con las filas donde no hubo coincidencia (p.id_pieza IS NULL).
SELECT r.id_reparacion, r.matricula, r.descripcion, r.estado
FROM reparaciones r
LEFT JOIN piezas_reparacion p ON p.id_reparacion = r.id_reparacion
WHERE p.id_pieza IS NULL;

-- ----------------------------------------------------------------------------
-- Explicaciones
--
-- INNER JOIN vs LEFT JOIN:
--   INNER JOIN solo devuelve las filas que tienen pareja en las dos tablas.
--   LEFT JOIN devuelve todas las filas de la tabla de la izquierda; si no hay pareja
--   en la derecha, sus columnas salen a NULL. Por eso sirve para "incluir los que no tienen".
--
-- GROUP BY, SUM y COALESCE:
--   GROUP BY agrupa las filas por un valor (un grupo por cliente, por reparación...).
--   SUM suma una columna dentro de cada grupo; COUNT cuenta las filas no nulas.
--   COALESCE(x, 0) devuelve x, o 0 si x es NULL (SUM de un conjunto vacío es NULL, no 0).
--
-- Integridad referencial (investigación):
--   JOIN o WHERE relacionan tablas solo por la condición que se escribe, aunque las claves
--   foráneas estén desactivadas. Con PRAGMA foreign_keys = ON (por conexión), SQLite rechaza
--   insertar un vehículo con un cliente inexistente, asignar una reparación a un mecánico
--   inexistente y borrar un cliente con vehículos (ON DELETE RESTRICT).
--   Con las claves desactivadas, un vehículo huérfano no aparece en un INNER JOIN con clientes,
--   pero sí en un LEFT JOIN desde vehiculos (con el nombre del cliente a NULL).
