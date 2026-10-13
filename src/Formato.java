import java.util.Locale;

final class Formato {

    private Formato() {
    }

    static String monto(double valor) {
        return String.format(Locale.US, "Q%,.2f", valor);
    }

    static String horas(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
}
