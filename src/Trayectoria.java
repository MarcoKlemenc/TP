import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Trayectoria {

	private List<Recorrido> camino = new ArrayList<Recorrido>();
	private Set<Habitacion> habitaciones = new HashSet<Habitacion>();

	public Trayectoria() {
	}

	public Trayectoria(StringTokenizer s, Dibujo d) {
		while (s.hasMoreTokens()) {
			agregarBaldosa(d.getPiso().buscarId(Integer.parseInt(s.nextToken())), Integer.parseInt(s.nextToken()),
					Integer.parseInt(s.nextToken()));
		}
	}

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

	public String toString() {
		String string = "";
		for (Recorrido r : camino) {
			string += r.toString();
		}
		return string + "%";
	}

}