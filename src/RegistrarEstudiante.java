import java.util.ArrayList;
import java.util.List;

public class RegistrarEstudiante {
    private List<EstudianteData> lista;

    public RegistrarEstudiante() {
        this.lista = new ArrayList<EstudianteData>();
    }

    public boolean registrarEstudiante(String nombre, String apellido, Carrera carrera, int carnet) {
        if (carrera == null) return false;
        EstudianteData estudiante = new EstudianteData(nombre, apellido, carrera, carnet);
        return lista.add(estudiante);
    }

    public boolean registrarEstudiante(String nombre, String apellido, String carreraTexto, int carnet) {
        Carrera carrera = Carrera.fromString(carreraTexto);
        if (carrera == null) return false;
        EstudianteData estudiante = new EstudianteData(nombre, apellido, carrera, carnet);
        return lista.add(estudiante);
    }

    public boolean eliminarEstudiantePorCarnet(int carnet) {
        return lista.removeIf(e -> e.getCarnet() == carnet);
    }

    public EstudianteData buscarEstudiantePorCarnet(int carnet) {
        return lista.stream()
                .filter(e -> e.getCarnet() == carnet)
                .findFirst()
                .orElse(null);
    }

    public void carnetEstudiante(int carnet) {
        EstudianteData e = lista.stream()
                .filter(est -> est.getCarnet() == carnet)
                .findFirst()
                .orElse(null);
        if (e != null) {
            System.out.println(e.toString());
        } else {
            System.out.println("No encontrado");
        }
    }

    public void carreraEstudiante(Carrera carrera) {
        List<EstudianteData> filtrados = lista.stream()
                .filter(e -> e.getCarrera() == carrera)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        for (EstudianteData e : filtrados) {
            System.out.println(e.toString());
        }
    }

    public List<EstudianteData> getListaEstudiante() {
        return lista;
    }

    public List<EstudianteData> getEstudiantes() {
        return lista;
    }
}
