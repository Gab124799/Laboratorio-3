import java.nio.charset.Charset;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Empresa empresa = new Empresa();
        DatosIniciales.cargar(empresa);
        Scanner entrada = new Scanner(System.in, charsetEntrada());
        new Menu(empresa, entrada).ejecutar();
    }

    private static Charset charsetEntrada() {
        try {
            return Charset.forName(System.getProperty("stdin.encoding",
                    Charset.defaultCharset().name()));
        } catch (RuntimeException e) {
            return Charset.defaultCharset();
        }
    }
}
