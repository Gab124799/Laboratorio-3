public final class DatosIniciales {

    private DatosIniciales() {
    }

    public static void cargar(Empresa empresa) {
        empresa.cargarOrdenInicial(new Diagnostico(
                "D-001", "Ana Morales", "6a Avenida 12-30 zona 1", 1.0, Zona.METROPOLITANA));
        empresa.cargarOrdenInicial(new Diagnostico(
                "D-002", "Luis Pérez", "Km 18 Carretera a El Salvador", 1.5, Zona.PERIFERICA));

        empresa.cargarOrdenInicial(new Instalacion(
                "I-001", "Marta López", "12 Calle 5-40 zona 10", 2.5, Zona.METROPOLITANA, 5));
        empresa.cargarOrdenInicial(new Instalacion(
                "I-002", "Carlos Ruiz", "Aldea San José Pinula", 3.0, Zona.PERIFERICA, 2));
        empresa.cargarOrdenInicial(new Instalacion(
                "I-003", "Sofía Díaz", "3a Avenida 8-15 zona 4", 1.0, Zona.METROPOLITANA, 6));

        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada(
                "E-001", "Jorge Castillo", "7a Avenida 20-11 zona 9", 2.5, Zona.METROPOLITANA, 5, 2000.0));
        empresa.cargarOrdenInicial(new InstalacionElectricaCertificada(
                "E-002", "Elena Gómez", "Km 25 Carretera a Palencia", 4.0, Zona.PERIFERICA, 3, 1500.0));

        empresa.cargarOrdenInicial(new MantenimientoAire(
                "M-001", "Roberto Salazar", "Diagonal 6 10-65 zona 10", 2.0, Zona.METROPOLITANA, 12000));
        empresa.cargarOrdenInicial(new MantenimientoAire(
                "M-002", "Patricia Vega", "5a Calle 3-22 zona 15", 1.5, Zona.METROPOLITANA, 24000));

        empresa.registrarCuadrilla(new Cuadrilla("C-01", 8.0, true));
        empresa.registrarCuadrilla(new Cuadrilla("C-02", 6.0, false));
        empresa.registrarCuadrilla(new Cuadrilla("C-03", 4.0, false));
    }
}
