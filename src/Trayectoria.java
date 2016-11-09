import java.util.ArrayList;
import java.util.List;

public class Trayectoria {

	private List<Baldosa> camino;
	private Habitacion habitacion;

	public Trayectoria(Habitacion habitacion) {
		this.camino = new ArrayList<Baldosa>();
		this.habitacion = habitacion;
	}

	public void agregarBaldosa(Baldosa baldosa) {
		this.camino.add(baldosa);
	}

	public List<Baldosa> getCamino() {
		return camino;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

}