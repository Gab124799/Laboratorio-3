import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Empresa {

    private final Map<String, OrdenServicio> ordenes = new LinkedHashMap<>();
    private final Map<String, Cuadrilla> cuadrillas = new LinkedHashMap<>();
    private final Map<String, Jornada> jornadas = new LinkedHashMap<>();
    private int contadorJornadas = 0;

    public boolean existeOrden(String codigo) {
        return ordenes.containsKey(Validacion.normalizarCodigo(codigo));
    }

    public boolean existeCuadrilla(String codigo) {
        return cuadrillas.containsKey(Validacion.normalizarCodigo(codigo));
    }

    public void registrarOrden(OrdenServicio orden) {
        agregarOrden(orden);
        orden.cobrarRegistro();
    }

    public void cargarOrdenInicial(OrdenServicio orden) {
        agregarOrden(orden);
    }

    public void registrarCuadrilla(Cuadrilla cuadrilla) {
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La cuadrilla es obligatoria.");
        }
        if (cuadrillas.containsKey(cuadrilla.getCodigo())) {
            throw new OperacionInvalidaException(
                    "Ya existe una cuadrilla con el código " + cuadrilla.getCodigo() + ".");
        }
        cuadrillas.put(cuadrilla.getCodigo(), cuadrilla);
    }

    public Jornada crearJornada(String codigoCuadrilla) {
        Cuadrilla cuadrilla = buscarCuadrilla(codigoCuadrilla);
        String codigo = String.format("J-%03d", contadorJornadas + 1);
        Jornada jornada = new Jornada(codigo, cuadrilla);
        contadorJornadas++;
        jornadas.put(codigo, jornada);
        return jornada;
    }

    public void asignarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(codigoJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.asignar(orden);
    }

    public void retirarOrden(String codigoJornada, String codigoOrden) {
        Jornada jornada = buscarJornada(codigoJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.retirar(orden);
    }

    public void iniciarJornada(String codigoJornada) {
        buscarJornada(codigoJornada).iniciar();
    }

    public void registrarResultado(String codigoJornada, String codigoOrden,
                                   ResultadoVisita resultado) {
        Jornada jornada = buscarJornada(codigoJornada);
        OrdenServicio orden = buscarOrden(codigoOrden);
        jornada.registrarResultado(orden, resultado);
    }

    public OrdenServicio buscarOrden(String codigo) {
        OrdenServicio orden = ordenes.get(Validacion.normalizarCodigo(codigo));
        if (orden == null) {
            throw new OperacionInvalidaException("No existe la orden '" + codigo.trim() + "'.");
        }
        return orden;
    }

    public Cuadrilla buscarCuadrilla(String codigo) {
        Cuadrilla cuadrilla = cuadrillas.get(Validacion.normalizarCodigo(codigo));
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("No existe la cuadrilla '" + codigo.trim() + "'.");
        }
        return cuadrilla;
    }

    public Jornada buscarJornada(String codigo) {
        Jornada jornada = jornadas.get(Validacion.normalizarCodigo(codigo));
        if (jornada == null) {
            throw new OperacionInvalidaException("No existe la jornada '" + codigo.trim() + "'.");
        }
        return jornada;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(new ArrayList<>(ordenes.values()));
    }

    public List<Cuadrilla> getCuadrillas() {
        return Collections.unmodifiableList(new ArrayList<>(cuadrillas.values()));
    }

    public List<Jornada> getJornadas() {
        return Collections.unmodifiableList(new ArrayList<>(jornadas.values()));
    }

    public Map<String, Map<EstadoOrden, Integer>> conteoPorCategoriaYEstado() {
        Map<String, Map<EstadoOrden, Integer>> conteo = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            Map<EstadoOrden, Integer> porEstado = conteo.computeIfAbsent(
                    orden.getCategoria(), categoria -> new EnumMap<>(EstadoOrden.class));
            porEstado.merge(orden.getEstado(), 1, Integer::sum);
        }
        return conteo;
    }

    public double ingresoPorRegistro() {
        double total = 0.0;
        for (OrdenServicio orden : ordenes.values()) {
            total += orden.getIngresoRegistro();
        }
        return total;
    }

    public double ingresoPorVisitasAdicionales() {
        double total = 0.0;
        for (OrdenServicio orden : ordenes.values()) {
            total += orden.getIngresoVisitasAdicionales();
        }
        return total;
    }

    public double ingresoTotal() {
        return ingresoPorRegistro() + ingresoPorVisitasAdicionales();
    }

    public Map<String, Double> ingresoPorCategoria() {
        Map<String, Double> ingresos = new LinkedHashMap<>();
        for (OrdenServicio orden : ordenes.values()) {
            ingresos.merge(orden.getCategoria(), orden.ingresoTotal(), Double::sum);
        }
        return ingresos;
    }

    public List<Jornada> jornadasNoCerradas() {
        List<Jornada> abiertas = new ArrayList<>();
        for (Jornada jornada : jornadas.values()) {
            if (!jornada.estaCerrada()) {
                abiertas.add(jornada);
            }
        }
        return abiertas;
    }

    private void agregarOrden(OrdenServicio orden) {
        if (orden == null) {
            throw new OperacionInvalidaException("La orden es obligatoria.");
        }
        if (ordenes.containsKey(orden.getCodigo())) {
            throw new OperacionInvalidaException(
                    "Ya existe una orden con el código " + orden.getCodigo() + ".");
        }
        ordenes.put(orden.getCodigo(), orden);
    }
}
