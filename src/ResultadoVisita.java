public enum ResultadoVisita {
    COMPLETADA("Completada"),
    FALLIDA("Visita fallida");

    private final String etiqueta;

    ResultadoVisita(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
