-- Completa cada ejercicio con UNA consulta SELECT (o WITH ... SELECT).
-- No borres las líneas: -- EJERCICIO NN
-- Termina cada consulta con punto y coma.

-- EJERCICIO 01
-- Mostrar los cinco primeros productos por identificador.
SELECT * from Productos ORDER BY id LIMIT 5;
-- SELECT id, nombre, precio FROM Productos ORDER BY id LIMIT 5;

-- EJERCICIO 02
-- Ver los cinco vehículos más recientes.
SELECT * from Vehiculos ORDER BY anio DESC LIMIT 5;


-- EJERCICIO 03
-- Filtrar productos cuyo precio sea mayor de 100.
SELECT * from Productos WHERE precio > 100 ORDER BY precio DESC LIMIT 8;

-- EJERCICIO 04
-- Localizar vehículos de la marca Toyota.
SELECT * from Vehiculos WHERE marca = 'Toyota' ORDER BY anio DESC LIMIT 8;

-- EJERCICIO 05
-- Buscar productos cuyo nombre contenga Pro.
SELECT * from Productos WHERE nombre LIKE '%Pro%' ORDER BY id ASC LIMIT 8;

-- EJERCICIO 06
-- Mostrar productos con precio comprendido entre 20 y 50.
SELECT * from Productos WHERE precio BETWEEN 20 and 50 order by precio asc LIMIT 8;

-- EJERCICIO 07
-- Listar marcas diferentes sin repetir.
SELECT DISTINCT marca FROM Vehiculos  ORDER BY marca ASC;

-- EJERCICIO 08
-- Seleccionar vehículos de dos marcas concretas.
SELECT * from Vehiculos where marca in ('Toyota', 'Ford') ORDER BY marca ASC, anio DESC limit 8;

-- EJERCICIO 09
-- Contar cuántos productos existen.
SELECT COUNT(*) from Productos ;

-- EJERCICIO 10
-- Contar cuántos vehículos están registrados.
SELECT COUNT(*) from Vehiculos;

-- EJERCICIO 11
-- Obtener precio mínimo, máximo y medio.
SELECT MIN(precio), MAX(precio), ROUND(AVG(precio),2)  from Productos;

-- EJERCICIO 12
-- Contar las marcas diferentes.
SELECT COUNT(DISTINCT marca) from Vehiculos ;

-- EJERCICIO 13
-- Calcular la suma de todos los precios del catálogo.
SELECT SUM(precio) from Productos;

-- EJERCICIO 14
-- Contar productos con precio de al menos 50.
SELECT COUNT(*) from Productos WHERE precio > 50;

-- EJERCICIO 15
-- Contar vehículos de cada marca.
SELECT marca, COUNT(*) FROM Vehiculos GROUP BY marca;

-- EJERCICIO 16
-- Encontrar los años de fabricación con más vehículos.
SELECT anio, COUNT(*) as cantidad FROM Vehiculos GROUP BY anio ORDER BY cantidad DESC LIMIT 10;

-- EJERCICIO 17
-- Calcular año más antiguo y reciente por marca.
SELECT marca, MIN(anio), MAX(anio) FROM Vehiculos GROUP BY marca ;

-- EJERCICIO 18
-- Contar productos por tramos de precio.
SELECT 
CASE
    WHEN precio < 25 THEN 'Menos de 25'
    WHEN precio >= 75 THEN '75 o más'
    ELSE '25 a menos de 75'
    END as tramo,
    COUNT(precio) as cantidad
from Productos GROUP BY tramo ORDER BY cantidad ASC;

-- EJERCICIO 19
-- Filtrar marcas con al menos cinco vehículos.
SELECT marca, COUNT(*) as cantidad from Vehiculos GROUP BY marca HAVING cantidad > 5;

-- EJERCICIO 20
-- Mostrar años en los que hay al menos tres vehículos.
SELECT anio, COUNT(*) as cantidad from Vehiculos GROUP BY anio HAVING cantidad >= 3 ORDER BY cantidad DESC LIMIT 10;


---- A PARTIR DEL EJERCICIO 21 LOS ENUNCIADOS CONTENDRAN UN BREVE REPASO DEBIDO A SU COMPLEJIDAD-------


-- EJERCICIO 21
-- La "familia" es la primera palabra del nombre ("Teclado Pro" → "Teclado").
-- instr busca la posición del primer espacio; substr corta desde el principio
-- hasta justo antes de ese espacio (por eso el -1).
-- El CASE cubre los nombres de una sola palabra (sin espacio, instr da 0).
SELECT  CASE 
       WHEN instr(nombre, ' ') = 0 THEN nombre
       ELSE substr(nombre, 1, instr(nombre, ' ') - 1)
       END AS familia,
       COUNT(*) AS productos,  
       ROUND(AVG(precio), 2) AS media        
FROM Productos GROUP BY familia ORDER BY productos DESC, familia ASC LIMIT 12;


-- EJERCICIO 22
-- WITH (CTE) crea una "tabla temporal" llamada familias para no repetir
-- el CASE largo. Después se consulta como si fuera una tabla normal.
WITH familias AS (
    SELECT CASE
               WHEN instr(nombre, ' ') = 0 THEN nombre
               ELSE substr(nombre, 1, instr(nombre, ' ') - 1)
           END AS familia,
           precio
    FROM Productos
)
SELECT familia, COUNT(*) AS productos, ROUND(AVG(precio), 2) AS media FROM familias GROUP BY familia
HAVING COUNT(*) >= 4 AND AVG(precio) > 35
ORDER BY media DESC;


-- EJERCICIO 23
-- Mostrar productos más caros que la media general.
SELECT id, nombre, precio FROM Productos WHERE precio > (SELECT AVG(precio) FROM Productos) ORDER BY precio DESC LIMIT 10;

-- EJERCICIO 24
-- Listar los modelos de los vehículos posteriores al año 2020 agrupados por marca.
SELECT marca, COUNT(*) AS recientes FROM Vehiculos WHERE anio > 2020 GROUP BY marca ORDER BY recientes DESC, marca ASC;

-- EJERCICIO 25
-- Detectar los productos empatados con el precio máximo.
SELECT id, nombre, precio FROM Productos WHERE precio = (SELECT MAX(precio) FROM Productos);

-- EJERCICIO 26
-- Clasificar productos según el precio usando un ranking.
SELECT id, nombre, precio, RANK() OVER (ORDER BY precio DESC) AS puesto FROM Productos ORDER BY puesto LIMIT 10;


-- EJERCICIO 27
-- Calcular un total acumulado de precios ordenados por identificador.
SELECT id, nombre, precio, ROUND(SUM(precio) OVER (ORDER BY id), 2) AS acumulado FROM Productos ORDER BY id LIMIT 10;

-- EJERCICIO 28
-- Comparar cada producto con el precio medio de su familia.
WITH familias AS (
    SELECT id, nombre, precio,
           CASE
               WHEN instr(nombre, ' ') = 0 THEN nombre
               ELSE substr(nombre, 1, instr(nombre, ' ') - 1)
           END AS familia
    FROM Productos
)
SELECT id, nombre, familia, precio, ROUND(AVG(precio) OVER (PARTITION BY familia), 2) AS media_familia
FROM familias
ORDER BY familia ASC, precio DESC
LIMIT 12;

-- EJERCICIO 29
-- Mostrar el vehículo más reciente de cada marca, incluyendo empates.
WITH ranking AS (
    SELECT matricula, marca, modelo, anio, RANK() OVER (PARTITION BY marca ORDER BY anio DESC) AS posicion FROM Vehiculos
)
SELECT matricula, marca, modelo, anio FROM ranking WHERE posicion = 1 ORDER BY marca;


-- EJERCICIO 30
-- Resumir por marca el total, año medio y antigüedad, conservando solo marcas con al menos tres vehículos.
SELECT marca, COUNT(*) AS total, ROUND(AVG(anio), 1) AS anio_medio,MIN(anio) AS mas_antiguo,MAX(anio) AS mas_reciente
FROM Vehiculos GROUP BY marca HAVING COUNT(*) >= 3 ORDER BY marca;
