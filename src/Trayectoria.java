import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Trayectoria {

	private List<Camino> camino = new ArrayList<Camino>();
	private Set<Habitacion> habitaciones = new HashSet<Habitacion>();

	public void agregarBaldosa(Habitacion h, Point p) {
		camino.add(new Camino(h, p));
		habitaciones.add(h);
	}

	public void agregarBaldosa(Habitacion h, int f, int c) {
		agregarBaldosa(h, new Point(f, c));
	}

	public List<Camino> getCamino() {
		return camino;
	}

	public void setCamino(List<Camino> camino) {
		this.camino = camino;
	}

	public Set<Habitacion> getHabitaciones() {
		return habitaciones;
	}

	public void setHabitaciones(Set<Habitacion> habitaciones) {
		this.habitaciones = habitaciones;
	}

}