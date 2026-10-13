public class Diagnostico extends OrdenServicio {

    private static final double MAX_HORAS_ESTIMADAS = 1.5;
    private static final double COSTO_METROPOLITANA = 150.0;
    private static final double COSTO_PERIFERICA = 225.0;
    private static final int DIAS_METROPOLITANA = 1;
    private static final int DIAS_PERIFERICA = 2;
    private static final int MAX_VISITAS = 3;

    public Diagnostico(String codigo, String cliente, String direccion,
                       double horasEstimadas, Zona zona) {
        super(codigo, cliente, direccion, horasEstimadas, zona);
        if (horasEstimadas > MAX_HORAS_ESTIMADAS) {
            throw new OperacionInvalidaException(
                    "Un diagnóstico no puede superar 1.5 horas estimadas.");
        }
    }

    @Override
    public double calcularCosto() {
        return esMetropolitana() ? COSTO_METROPOLITANA : COSTO_PERIFERICA;
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
        return "Diagnóstico";
    }
}
