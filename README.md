# TecniHogar - Lab 3 Herencia (CC2008)

Aplicación de consola en Java para gestionar órdenes de servicio técnico a domicilio, cuadrillas y jornadas, usando herencia y polimorfismo.

## Requisitos

- JDK 17 o superior (probado con JDK 21)

## Compilar

Desde la carpeta del proyecto:

```
javac -encoding UTF-8 -d bin src/*.java
```

## Ejecutar

```
java -cp bin Main
```

El menú se mantiene disponible hasta elegir la opción `0` (Salir). Los datos viven solo en memoria y el programa inicia con 9 órdenes pendientes (al menos dos por categoría) y 3 cuadrillas (una certificada en refrigeración), sin ingresos registrados.

## Estructura

| Archivo | Rol |
|---|---|
| `OrdenServicio` | Clase abstracta base de las órdenes |
| `Diagnostico`, `Instalacion`, `MantenimientoAire` | Categorías que extienden `OrdenServicio` |
| `InstalacionElectricaCertificada` | Extiende `Instalacion` |
| `Cuadrilla`, `Jornada` | Cuadrillas y jornadas de trabajo |
| `Empresa` | Coordina órdenes, cuadrillas, jornadas y reportes |
| `Menu` | Interfaz de consola |
| `Main` | Contiene el método `main` |
| `DatosIniciales` | Datos precargados |
| `Zona`, `EstadoOrden`, `EstadoJornada`, `ResultadoVisita` | Enumeraciones |
| `OperacionInvalidaException`, `Validacion`, `Formato` | Rechazo de operaciones inválidas, validaciones y formato de montos |

## Notas de uso

- Los códigos no distinguen mayúsculas de minúsculas (`d-001` equivale a `D-001`).
- Las jornadas reciben código automático: `J-001`, `J-002`, etc.
- En la opción 1 se muestra la cotización y solo se registra y cobra la orden si el cliente acepta.
