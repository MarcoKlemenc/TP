import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trayectoria {

	private List<Camino> camino = new ArrayList<Camino>();
	private Set<Habitacion> habitaciones = new HashSet<Habitacion>();

	public void anexar(Trayectoria t) {
		for (Camino c : t.getCamino()) {
			camino.add(c);
		}
		habitaciones.addAll(t.getHabitaciones());
	}

	public void agregarBaldosa(Habitacion h, Point p) {
		camino.add(new Camino(h, p));
		habitaciones.add(h);
	}

	public void agregarBaldosa(Habitacion h, int f, int c) {
		agregarBaldosa(h, new Point(f, c));
	}

	public boolean buscar(Habitacion h, Point p) {
		for (Camino c : camino) {
			if (c.getHabitacion() == h && c.getPunto().getX() == p.getX() && c.getPunto().getY() == p.getY()) {
				return true;
			}
		}
		return false;
	}

	public List<Camino> getCamino() {
		return camino;
	}

	public Set<Habitacion> getHabitaciones() {
		return habitaciones;
	}

}