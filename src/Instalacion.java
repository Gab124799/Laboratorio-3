public class Instalacion extends OrdenServicio {

    private static final double TARIFA_POR_HORA = 90.0;
    private static final double HORAS_POR_PUNTO = 0.75;
    private static final double TOLERANCIA = 1e-9;
    private static final int DIAS_METROPOLITANA = 3;
    private static final int DIAS_PERIFERICA = 5;
    private static final int MAX_VISITAS = 3;

    private final int puntos;

    public Instalacion(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona, int puntos) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        this.puntos = Validacion.enteroPositivo(puntos, "Cantidad de puntos");
    }

    public int getPuntos() {
        return puntos;
    }

    public double horasPorPuntos() {
        return puntos * HORAS_POR_PUNTO;
    }

    public double horasFacturables() {
        return Math.max(getHorasEstimadas(), horasPorPuntos());
    }

    public double horasCobradas() {
        return Math.ceil(horasFacturables() - TOLERANCIA);
    }

    @Override
    public double calcularCosto() {
        return tarifaBase() + TARIFA_POR_HORA * horasCobradas();
    }

    @Override
    public int calcularDiasAtencion() {
        return esMetropolitana() ? DIAS_METROPOLITANA : DIAS_PERIFERICA;
    }

    @Override
    public int getMaxVisitas() {
        return MAX_VISITAS;
    }

    @Override
    public String getCategoria() {
        return "Instalación";
    }

    @Override
    public String detalle() {
        return "Puntos: " + puntos + ", horas por puntos: " + Formato.horas(horasPorPuntos());
    }
}
