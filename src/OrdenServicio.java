public abstract class OrdenServicio {

    private static final double CARGO_VISITA_ADICIONAL = 40.0;
    private static final double TARIFA_BASE_METROPOLITANA = 100.0;
    private static final double TARIFA_BASE_PERIFERICA = 175.0;

    private final String codigo;
    private final String cliente;
    private final String direccion;
    private final double horasEstimadas;
    private final Zona zona;
    private EstadoOrden estado;
    private int visitasRealizadas;
    private double ingresoRegistro;
    private double ingresoVisitasAdicionales;

    protected OrdenServicio(String codigo, String cliente, String direccion,
                            double horasEstimadas, Zona zona) {
        this.codigo = Validacion.codigo(codigo, "Código de la orden");
        this.cliente = Validacion.texto(cliente, "Nombre del cliente");
        this.direccion = Validacion.texto(direccion, "Dirección");
        this.horasEstimadas = Validacion.positivo(horasEstimadas, "Horas estimadas");
        if (zona == null) {
            throw new OperacionInvalidaException("La zona es obligatoria.");
        }
        this.zona = zona;
        this.estado = EstadoOrden.PENDIENTE;
        this.visitasRealizadas = 0;
        this.ingresoRegistro = 0.0;
        this.ingresoVisitasAdicionales = 0.0;
    }

    public abstract double calcularCosto();

    public abstract int calcularDiasAtencion();

    public abstract int getMaxVisitas();

    public abstract String getCategoria();

    public boolean admiteCuadrilla(Cuadrilla cuadrilla) {
        return true;
    }

    public String detalle() {
        return "-";
    }

    protected double cargoVisitaAdicional() {
        return CARGO_VISITA_ADICIONAL;
    }

    protected double tarifaBase() {
        return esMetropolitana() ? TARIFA_BASE_METROPOLITANA : TARIFA_BASE_PERIFERICA;
    }

    protected boolean esMetropolitana() {
        return zona == Zona.METROPOLITANA;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getCliente() {
        return cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getHorasEstimadas() {
        return horasEstimadas;
    }

    public Zona getZona() {
        return zona;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public int getVisitasRealizadas() {
        return visitasRealizadas;
    }

    public int visitasRestantes() {
        return getMaxVisitas() - visitasRealizadas;
    }

    public double getIngresoRegistro() {
        return ingresoRegistro;
    }

    public double getIngresoVisitasAdicionales() {
        return ingresoVisitasAdicionales;
    }

    public double ingresoTotal() {
        return ingresoRegistro + ingresoVisitasAdicionales;
    }

    void cobrarRegistro() {
        if (ingresoRegistro > 0) {
            throw new OperacionInvalidaException("La orden " + codigo + " ya fue cobrada.");
        }
        ingresoRegistro = calcularCosto();
    }

    void asignar() {
        exigirEstado(EstadoOrden.PENDIENTE, "asignarse; solo las órdenes pendientes pueden asignarse");
        estado = EstadoOrden.ASIGNADA;
    }

    void retirar() {
        exigirEstado(EstadoOrden.ASIGNADA, "retirarse; solo las órdenes asignadas pueden retirarse");
        estado = EstadoOrden.PENDIENTE;
    }

    double iniciarVisita() {
        exigirEstado(EstadoOrden.ASIGNADA, "iniciar una visita; debe estar asignada");
        estado = EstadoOrden.EN_ATENCION;
        visitasRealizadas++;
        double cargo = visitasRealizadas > 1 ? cargoVisitaAdicional() : 0.0;
        ingresoVisitasAdicionales += cargo;
        return cargo;
    }

    void registrarResultado(ResultadoVisita resultado) {
        exigirEstado(EstadoOrden.EN_ATENCION, "registrar resultado; debe estar en atención");
        if (resultado == ResultadoVisita.COMPLETADA) {
            estado = EstadoOrden.COMPLETADA;
        } else if (visitasRealizadas >= getMaxVisitas()) {
            estado = EstadoOrden.CANCELADA;
        } else {
            estado = EstadoOrden.PENDIENTE;
        }
    }

    private void exigirEstado(EstadoOrden esperado, String accion) {
        if (estado != esperado) {
            throw new OperacionInvalidaException(String.format(
                    "La orden %s está en estado %s y no puede %s.", codigo, estado, accion));
        }
    }
}
