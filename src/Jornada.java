import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Jornada {

    private static final double TOLERANCIA = 1e-9;

    private final String codigo;
    private final Cuadrilla cuadrilla;
    private final List<OrdenServicio> ordenes;
    private final Map<OrdenServicio, ResultadoVisita> resultados;
    private EstadoJornada estado;

    Jornada(String codigo, Cuadrilla cuadrilla) {
        this.codigo = Validacion.codigo(codigo, "Código de la jornada");
        if (cuadrilla == null) {
            throw new OperacionInvalidaException("La cuadrilla es obligatoria.");
        }
        cuadrilla.ocupar();
        this.cuadrilla = cuadrilla;
        this.ordenes = new ArrayList<>();
        this.resultados = new LinkedHashMap<>();
        this.estado = EstadoJornada.EN_PREPARACION;
    }

    public String getCodigo() {
        return codigo;
    }

    public Cuadrilla getCuadrilla() {
        return cuadrilla;
    }

    public EstadoJornada getEstado() {
        return estado;
    }

    public List<OrdenServicio> getOrdenes() {
        return Collections.unmodifiableList(ordenes);
    }

    public boolean estaCerrada() {
        return estado == EstadoJornada.CERRADA;
    }

    public double horasAsignadas() {
        double total = 0.0;
        for (OrdenServicio orden : ordenes) {
            total += orden.getHorasEstimadas();
        }
        return total;
    }

    public double capacidadDisponible() {
        return cuadrilla.getCapacidadHoras() - horasAsignadas();
    }

    public boolean tieneResultado(OrdenServicio orden) {
        return resultados.containsKey(orden);
    }

    public ResultadoVisita getResultado(OrdenServicio orden) {
        return resultados.get(orden);
    }

    public List<OrdenServicio> ordenesSinResultado() {
        List<OrdenServicio> sinResultado = new ArrayList<>();
        for (OrdenServicio orden : ordenes) {
            if (!resultados.containsKey(orden)) {
                sinResultado.add(orden);
            }
        }
        return sinResultado;
    }

    void asignar(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "asignar órdenes");
        if (orden == null) {
            throw new OperacionInvalidaException("La orden es obligatoria.");
        }
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s está en estado %s; solo las órdenes pendientes pueden asignarse.",
                    orden.getCodigo(), orden.getEstado()));
        }
        if (!orden.admiteCuadrilla(cuadrilla)) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s (%s) no es compatible con la cuadrilla %s.",
                    orden.getCodigo(), orden.getCategoria(), cuadrilla.getCodigo()));
        }
        double horasConOrden = horasAsignadas() + orden.getHorasEstimadas();
        if (horasConOrden > cuadrilla.getCapacidadHoras() + TOLERANCIA) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s excede la capacidad de la cuadrilla %s: %s h con la orden, capacidad %s h.",
                    orden.getCodigo(), cuadrilla.getCodigo(),
                    Formato.horas(horasConOrden), Formato.horas(cuadrilla.getCapacidadHoras())));
        }
        orden.asignar();
        ordenes.add(orden);
    }

    void retirar(OrdenServicio orden) {
        exigirEstado(EstadoJornada.EN_PREPARACION, "retirar órdenes");
        if (orden == null || !ordenes.contains(orden)) {
            throw new OperacionInvalidaException(
                    "La orden no pertenece a la jornada " + codigo + ".");
        }
        orden.retirar();
        ordenes.remove(orden);
    }

    double iniciar() {
        exigirEstado(EstadoJornada.EN_PREPARACION, "iniciarse");
        if (ordenes.isEmpty()) {
            throw new OperacionInvalidaException("No se puede iniciar una jornada vacía.");
        }
        double cargos = 0.0;
        for (OrdenServicio orden : ordenes) {
            cargos += orden.iniciarVisita();
        }
        estado = EstadoJornada.EN_ATENCION;
        return cargos;
    }

    void registrarResultado(OrdenServicio orden, ResultadoVisita resultado) {
        exigirEstado(EstadoJornada.EN_ATENCION, "registrar resultados");
        if (resultado == null) {
            throw new OperacionInvalidaException("El resultado es obligatorio.");
        }
        if (orden == null || !ordenes.contains(orden)) {
            throw new OperacionInvalidaException(
                    "La orden no pertenece a la jornada " + codigo + ".");
        }
        if (resultados.containsKey(orden)) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s ya tiene resultado registrado en la jornada %s.",
                    orden.getCodigo(), codigo));
        }
        orden.registrarResultado(resultado);
        resultados.put(orden, resultado);
        if (resultados.size() == ordenes.size()) {
            estado = EstadoJornada.CERRADA;
            cuadrilla.liberar();
        }
    }

    private void exigirEstado(EstadoJornada esperado, String accion) {
        if (estado != esperado) {
            throw new OperacionInvalidaException(String.format(
                    "La jornada %s está en estado %s y no admite %s.", codigo, estado, accion));
        }
    }
}
