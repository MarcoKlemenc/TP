import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Trayectoria {

	private List<Point> camino;
	private Habitacion habitacion;

	public Trayectoria(Habitacion habitacion) {
		this.camino = new ArrayList<Point>();
		this.habitacion = habitacion;
	}

	public void agregarBaldosa(Point p) {
		this.camino.add(p);
	}

	public void agregarBaldosa(int f, int c) {
		this.camino.add(new Point(f, c));
	}

	public List<Point> getCamino() {
		return camino;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

}