public class InstalacionElectricaCertificada extends Instalacion {

    private static final double CARGO_CERTIFICADO = 120.0;
    private static final double PORCENTAJE_GARANTIA = 5.0;
    private static final int DIAS_ADICIONALES = 2;
    private static final int MAX_VISITAS = 2;

    private final double valorMateriales;

    public InstalacionElectricaCertificada(String codigo, String cliente, String direccion,
                                           double horasEstimadas, Zona zona, int puntos,
                                           double valorMateriales) {
        super(codigo, cliente, direccion, horasEstimadas, zona, puntos);
        this.valorMateriales = Validacion.positivo(valorMateriales,
                "Valor declarado de los materiales");
    }

    public double getValorMateriales() {
        return valorMateriales;
    }

    public double calcularGarantia() {
        return valorMateriales * PORCENTAJE_GARANTIA / 100.0;
    }

    @Override
    public double calcularCosto() {
        return super.calcularCosto() + CARGO_CERTIFICADO + calcularGarantia();
    }

    @Override
    public int calcularDiasAtencion() {
        return super.calcularDiasAtencion() + DIAS_ADICIONALES;
    }

    @Override
    public int getMaxVisitas() {
        return MAX_VISITAS;
    }

    @Override
    public String getCategoria() {
        return "Instalación eléctrica certificada";
    }

    @Override
    public String detalle() {
        return super.detalle() + ", materiales: " + Formato.monto(valorMateriales);
    }
}
