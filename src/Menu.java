import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Menu {

    private final Empresa empresa;
    private final Scanner entrada;

    public Menu(Empresa empresa, Scanner entrada) {
        this.empresa = empresa;
        this.entrada = entrada;
    }

    public void ejecutar() {
        boolean continuar = true;
        try {
            while (continuar) {
                mostrarMenu();
                int opcion = leerEntero("Opción: ");
                try {
                    continuar = procesar(opcion);
                } catch (OperacionInvalidaException e) {
                    System.out.println("Operación rechazada: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
        }
        System.out.println("Fin del programa.");
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("===== TecniHogar =====");
        System.out.println(" 1. Cotizar y registrar orden");
        System.out.println(" 2. Registrar cuadrilla");
        System.out.println(" 3. Crear jornada");
        System.out.println(" 4. Asignar orden a jornada");
        System.out.println(" 5. Retirar orden de jornada");
        System.out.println(" 6. Iniciar jornada");
        System.out.println(" 7. Registrar resultado de visita");
        System.out.println(" 8. Consultar órdenes");
        System.out.println(" 9. Consultar cuadrillas");
        System.out.println("10. Consultar jornadas");
        System.out.println("11. Reportes");
        System.out.println(" 0. Salir");
    }

    private boolean procesar(int opcion) {
        switch (opcion) {
            case 1 -> cotizarYRegistrar();
            case 2 -> registrarCuadrilla();
            case 3 -> crearJornada();
            case 4 -> asignarOrden();
            case 5 -> retirarOrden();
            case 6 -> iniciarJornada();
            case 7 -> registrarResultado();
            case 8 -> consultarOrdenes();
            case 9 -> consultarCuadrillas();
            case 10 -> consultarJornadas();
            case 11 -> mostrarReportes();
            case 0 -> {
                return false;
            }
            default -> System.out.println("Opción inválida. Elija un número del menú.");
        }
        return true;
    }

    private void cotizarYRegistrar() {
        System.out.println("Categorías:");
        System.out.println("  1) Diagnóstico");
        System.out.println("  2) Instalación");
        System.out.println("  3) Instalación eléctrica certificada");
        System.out.println("  4) Mantenimiento de aire acondicionado");
        int categoria = leerEntero("Categoría: ");
        if (categoria < 1 || categoria > 4) {
            throw new OperacionInvalidaException("La categoría debe estar entre 1 y 4.");
        }
        String codigo = leerTexto("Código de la orden: ");
        if (empresa.existeOrden(codigo)) {
            throw new OperacionInvalidaException(
                    "Ya existe una orden con el código " + codigo.trim().toUpperCase() + ".");
        }
        String cliente = leerTexto("Nombre del cliente: ");
        String direccion = leerTexto("Dirección: ");
        double horas = leerDecimal("Horas estimadas: ");
        Zona zona = leerZona();

        OrdenServicio orden = switch (categoria) {
            case 1 -> new Diagnostico(codigo, cliente, direccion, horas, zona);
            case 2 -> {
                int puntos = leerEntero("Cantidad de puntos a instalar: ");
                yield new Instalacion(codigo, cliente, direccion, horas, zona, puntos);
            }
            case 3 -> {
                int puntos = leerEntero("Cantidad de puntos a instalar: ");
                double materiales = leerDecimal("Valor declarado de los materiales (Q): ");
                yield new InstalacionElectricaCertificada(
                        codigo, cliente, direccion, horas, zona, puntos, materiales);
            }
            default -> {
                int btu = leerEntero("Capacidad del equipo (BTU): ");
                yield new MantenimientoAire(codigo, cliente, direccion, horas, zona, btu);
            }
        };

        System.out.println();
        System.out.println("--- Cotización ---");
        System.out.println("Categoría: " + orden.getCategoria());
        System.out.println("Costo: " + Formato.monto(orden.calcularCosto()));
        System.out.println("Tiempo estimado de atención: " + orden.calcularDiasAtencion()
                + " días hábiles");
        System.out.println("Visitas permitidas: " + orden.getMaxVisitas());

        if (leerSiNo("¿El cliente acepta la cotización? (s/n): ")) {
            empresa.registrarOrden(orden);
            System.out.println("Orden " + orden.getCodigo() + " registrada y cobrada por "
                    + Formato.monto(orden.getIngresoRegistro()) + ". Estado: "
                    + orden.getEstado() + ".");
        } else {
            System.out.println("Cotización descartada. No se registró nada.");
        }
    }

    private void registrarCuadrilla() {
        String codigo = leerTexto("Código de la cuadrilla: ");
        if (empresa.existeCuadrilla(codigo)) {
            throw new OperacionInvalidaException(
                    "Ya existe una cuadrilla con el código " + codigo.trim().toUpperCase() + ".");
        }
        double capacidad = leerDecimal("Capacidad máxima de horas por jornada: ");
        boolean certificada = leerSiNo("¿Está certificada en refrigeración? (s/n): ");
        Cuadrilla cuadrilla = new Cuadrilla(codigo, capacidad, certificada);
        empresa.registrarCuadrilla(cuadrilla);
        System.out.println("Cuadrilla " + cuadrilla.getCodigo() + " registrada.");
    }

    private void crearJornada() {
        String codigoCuadrilla = leerTexto("Código de la cuadrilla: ");
        Jornada jornada = empresa.crearJornada(codigoCuadrilla);
        System.out.println("Jornada " + jornada.getCodigo() + " creada para la cuadrilla "
                + jornada.getCuadrilla().getCodigo() + ". Estado: " + jornada.getEstado() + ".");
    }

    private void asignarOrden() {
        String codigoJornada = leerTexto("Código de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        empresa.asignarOrden(codigoJornada, codigoOrden);
        Jornada jornada = empresa.buscarJornada(codigoJornada);
        System.out.println("Orden " + empresa.buscarOrden(codigoOrden).getCodigo()
                + " asignada a la jornada " + jornada.getCodigo() + ". Horas asignadas: "
                + Formato.horas(jornada.horasAsignadas()) + ", capacidad disponible: "
                + Formato.horas(jornada.capacidadDisponible()) + ".");
    }

    private void retirarOrden() {
        String codigoJornada = leerTexto("Código de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        empresa.retirarOrden(codigoJornada, codigoOrden);
        Jornada jornada = empresa.buscarJornada(codigoJornada);
        System.out.println("Orden " + empresa.buscarOrden(codigoOrden).getCodigo()
                + " retirada de la jornada " + jornada.getCodigo() + " y de nuevo pendiente. "
                + "Horas asignadas: " + Formato.horas(jornada.horasAsignadas()) + ".");
    }

    private void iniciarJornada() {
        String codigoJornada = leerTexto("Código de la jornada: ");
        double antes = empresa.ingresoPorVisitasAdicionales();
        empresa.iniciarJornada(codigoJornada);
        double cargos = empresa.ingresoPorVisitasAdicionales() - antes;
        Jornada jornada = empresa.buscarJornada(codigoJornada);
        System.out.println("Jornada " + jornada.getCodigo() + " iniciada con "
                + jornada.getOrdenes().size() + " orden(es). Cargos por visitas adicionales: "
                + Formato.monto(cargos) + ".");
    }

    private void registrarResultado() {
        String codigoJornada = leerTexto("Código de la jornada: ");
        String codigoOrden = leerTexto("Código de la orden: ");
        ResultadoVisita resultado = leerResultado();
        empresa.registrarResultado(codigoJornada, codigoOrden, resultado);
        OrdenServicio orden = empresa.buscarOrden(codigoOrden);
        Jornada jornada = empresa.buscarJornada(codigoJornada);
        System.out.println("Resultado registrado. Orden " + orden.getCodigo() + ": "
                + orden.getEstado() + " (visitas " + orden.getVisitasRealizadas() + "/"
                + orden.getMaxVisitas() + "). Jornada " + jornada.getCodigo() + ": "
                + jornada.getEstado() + ".");
    }

    private void consultarOrdenes() {
        if (empresa.getOrdenes().isEmpty()) {
            System.out.println("No hay órdenes registradas.");
            return;
        }
        System.out.println();
        System.out.printf("%-7s %-36s %-18s %-13s %7s %-12s %-8s %12s %12s%n",
                "Código", "Categoría", "Cliente", "Zona", "Horas", "Estado", "Visitas",
                "Costo", "Cobrado");
        for (OrdenServicio orden : empresa.getOrdenes()) {
            System.out.printf("%-7s %-36s %-18s %-13s %7s %-12s %-8s %12s %12s%n",
                    orden.getCodigo(), orden.getCategoria(), orden.getCliente(),
                    orden.getZona(), Formato.horas(orden.getHorasEstimadas()), orden.getEstado(),
                    orden.getVisitasRealizadas() + "/" + orden.getMaxVisitas(),
                    Formato.monto(orden.calcularCosto()), Formato.monto(orden.ingresoTotal()));
            System.out.println("        " + orden.detalle() + " | Dirección: " + orden.getDireccion()
                    + " | Atención: " + orden.calcularDiasAtencion() + " días hábiles");
        }
    }

    private void consultarCuadrillas() {
        if (empresa.getCuadrillas().isEmpty()) {
            System.out.println("No hay cuadrillas registradas.");
            return;
        }
        System.out.println();
        System.out.printf("%-8s %12s %-22s %-8s%n", "Código", "Capacidad h", "Cert. refrigeración",
                "Estado");
        for (Cuadrilla cuadrilla : empresa.getCuadrillas()) {
            System.out.printf("%-8s %12s %-22s %-8s%n", cuadrilla.getCodigo(),
                    Formato.horas(cuadrilla.getCapacidadHoras()),
                    cuadrilla.isCertificadaRefrigeracion() ? "Sí" : "No",
                    cuadrilla.estaLibre() ? "Libre" : "Ocupada");
        }
    }

    private void consultarJornadas() {
        if (empresa.getJornadas().isEmpty()) {
            System.out.println("No hay jornadas creadas.");
            return;
        }
        for (Jornada jornada : empresa.getJornadas()) {
            imprimirJornada(jornada);
        }
    }

    private void imprimirJornada(Jornada jornada) {
        System.out.println(jornada.getCodigo() + " | Cuadrilla " + jornada.getCuadrilla().getCodigo()
                + " | " + jornada.getEstado() + " | Horas asignadas: "
                + Formato.horas(jornada.horasAsignadas()) + " | Capacidad disponible: "
                + Formato.horas(jornada.capacidadDisponible()));
        for (OrdenServicio orden : jornada.getOrdenes()) {
            String resultado = jornada.tieneResultado(orden)
                    ? ", resultado: " + jornada.getResultado(orden) : "";
            System.out.println("    - " + orden.getCodigo() + " (" + orden.getCategoria() + ", "
                    + Formato.horas(orden.getHorasEstimadas()) + " h) estado: "
                    + orden.getEstado() + resultado);
        }
    }

    private void mostrarReportes() {
        System.out.println();
        System.out.println("=== Órdenes por categoría y estado ===");
        Map<String, Map<EstadoOrden, Integer>> conteo = empresa.conteoPorCategoriaYEstado();
        if (conteo.isEmpty()) {
            System.out.println("No hay órdenes registradas.");
        }
        for (Map.Entry<String, Map<EstadoOrden, Integer>> entrada : conteo.entrySet()) {
            Map<EstadoOrden, Integer> porEstado = entrada.getValue();
            StringBuilder linea = new StringBuilder();
            int total = 0;
            for (EstadoOrden estado : EstadoOrden.values()) {
                int cantidad = porEstado.getOrDefault(estado, 0);
                total += cantidad;
                linea.append(estado).append(": ").append(cantidad).append("  ");
            }
            System.out.println(entrada.getKey() + " (total " + total + ")");
            System.out.println("    " + linea.toString().trim());
        }

        System.out.println();
        System.out.println("=== Ingresos ===");
        System.out.println("Ingresos por registro:            " + Formato.monto(empresa.ingresoPorRegistro()));
        System.out.println("Ingresos por visitas adicionales: "
                + Formato.monto(empresa.ingresoPorVisitasAdicionales()));
        System.out.println("Ingreso total:                    " + Formato.monto(empresa.ingresoTotal()));
        System.out.println("Ingreso por categoría:");
        Map<String, Double> porCategoria = empresa.ingresoPorCategoria();
        for (Map.Entry<String, Double> ingreso : porCategoria.entrySet()) {
            System.out.println("    " + ingreso.getKey() + ": " + Formato.monto(ingreso.getValue()));
        }

        System.out.println();
        System.out.println("=== Jornadas no cerradas ===");
        if (empresa.jornadasNoCerradas().isEmpty()) {
            System.out.println("No hay jornadas sin cerrar.");
        }
        for (Jornada jornada : empresa.jornadasNoCerradas()) {
            System.out.println(jornada.getCodigo() + " | Cuadrilla " + jornada.getCuadrilla().getCodigo()
                    + " | " + jornada.getEstado() + " | Horas asignadas: "
                    + Formato.horas(jornada.horasAsignadas()) + " | Capacidad disponible: "
                    + Formato.horas(jornada.capacidadDisponible()));
        }
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine();
    }

    private double leerDecimal(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim().replace(',', '.');
            try {
                double valor = Double.parseDouble(texto);
                if (Double.isNaN(valor) || Double.isInfinite(valor)) {
                    throw new NumberFormatException(texto);
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido: ingrese un número, por ejemplo 2.5.");
            }
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Formato inválido: ingrese un número entero.");
            }
        }
    }

    private Zona leerZona() {
        while (true) {
            String texto = leerTexto("Zona (M = metropolitana, P = periférica): ")
                    .trim().toUpperCase();
            if (texto.equals("M")) {
                return Zona.METROPOLITANA;
            }
            if (texto.equals("P")) {
                return Zona.PERIFERICA;
            }
            System.out.println("Formato inválido: escriba M o P.");
        }
    }

    private ResultadoVisita leerResultado() {
        while (true) {
            String texto = leerTexto("Resultado (C = completada, F = visita fallida): ")
                    .trim().toUpperCase();
            if (texto.equals("C")) {
                return ResultadoVisita.COMPLETADA;
            }
            if (texto.equals("F")) {
                return ResultadoVisita.FALLIDA;
            }
            System.out.println("Formato inválido: escriba C o F.");
        }
    }

    private boolean leerSiNo(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje).trim().toLowerCase();
            if (texto.equals("s") || texto.equals("si") || texto.equals("sí")) {
                return true;
            }
            if (texto.equals("n") || texto.equals("no")) {
                return false;
            }
            System.out.println("Formato inválido: escriba s o n.");
        }
    }
}
