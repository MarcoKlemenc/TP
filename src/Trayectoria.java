import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trayectoria {

	private List<Recorrido> camino = new ArrayList<Recorrido>();
	private Set<Habitacion> habitaciones = new HashSet<Habitacion>();

	public void anexar(Trayectoria t) {
		for (Recorrido c : t.getCamino()) {
			camino.add(c);
		}
		habitaciones.addAll(t.getHabitaciones());
	}

	public void agregarBaldosa(Habitacion h, int f, int c) {
		camino.add(new Recorrido(h, new Point(f, c)));
		habitaciones.add(h);
	}

	public boolean buscar(Habitacion h, Point p) {
		for (Recorrido r : camino) {
			if (r.getHabitacion() == h && r.getCoordenadas().equals(p)) {
				return true;
			}
		}
		return false;
	}

	public List<Recorrido> getCamino() {
		return camino;
	}

	public Set<Habitacion> getHabitaciones() {
		return habitaciones;
	}

}