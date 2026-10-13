public class Cuadrilla {

    private final String codigo;
    private final double capacidadHoras;
    private final boolean certificadaRefrigeracion;
    private boolean libre;

    public Cuadrilla(String codigo, double capacidadHoras, boolean certificadaRefrigeracion) {
        this.codigo = Validacion.codigo(codigo, "Código de la cuadrilla");
        this.capacidadHoras = Validacion.positivo(capacidadHoras, "Capacidad de la cuadrilla");
        this.certificadaRefrigeracion = certificadaRefrigeracion;
        this.libre = true;
    }

    public String getCodigo() {
        return codigo;
    }

    public double getCapacidadHoras() {
        return capacidadHoras;
    }

    public boolean isCertificadaRefrigeracion() {
        return certificadaRefrigeracion;
    }

    public boolean estaLibre() {
        return libre;
    }

    void ocupar() {
        if (!libre) {
            throw new OperacionInvalidaException(
                    "La cuadrilla " + codigo + " ya tiene una jornada sin cerrar.");
        }
        libre = false;
    }

    void liberar() {
        libre = true;
    }
}
