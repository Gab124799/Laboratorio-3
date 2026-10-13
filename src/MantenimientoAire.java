public class MantenimientoAire extends OrdenServicio {

    private static final int BTU_MINIMO = 9000;
    private static final int BTU_MAXIMO = 60000;
    private static final double TARIFA_POR_HORA = 110.0;
    private static final double CARGO_GAS_REFRIGERANTE = 80.0;
    private static final int DIAS_ATENCION = 2;
    private static final int MAX_VISITAS = 1;

    private final int capacidadBtu;

    public MantenimientoAire(String codigo, String cliente, String direccion,
                             double horasEstimadas, Zona zona, int capacidadBtu) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (!esMetropolitana()) {
            throw new OperacionInvalidaException(
                    "El mantenimiento de aire acondicionado solo se acepta en la zona metropolitana.");
        }
        this.capacidadBtu = Validacion.enteroEnRango(capacidadBtu, BTU_MINIMO, BTU_MAXIMO,
                "Capacidad en BTU");
    }

    public int getCapacidadBtu() {
        return capacidadBtu;
    }

    @Override
    public double calcularCosto() {
        return tarifaBase() + TARIFA_POR_HORA * getHorasEstimadas() + CARGO_GAS_REFRIGERANTE;
    }

    @Override
    public int calcularDiasAtencion() {
        return DIAS_ATENCION;
    }

    @Override
    public int getMaxVisitas() {
        return MAX_VISITAS;
    }

    @Override
    public String getCategoria() {
        return "Mantenimiento de aire acondicionado";
    }

    @Override
    public boolean admiteCuadrilla(Cuadrilla cuadrilla) {
        return cuadrilla.isCertificadaRefrigeracion();
    }

    @Override
    public String detalle() {
        return "Capacidad: " + String.format(java.util.Locale.US, "%,d", capacidadBtu) + " BTU";
    }
}
