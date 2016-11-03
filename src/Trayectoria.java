import java.util.ArrayList;
import java.util.List;

public class Trayectoria {

	private Baldosa origen;
	private Baldosa destino;
	private List<Baldosa> camino;
	private Habitacion habitacion;

	public Trayectoria(Baldosa origen, Baldosa destino, Habitacion habitacion) {
		this.origen = origen;
		this.destino = destino;
		this.camino = new ArrayList<Baldosa>();
		this.habitacion = habitacion;
	}

	public void agregarBaldosa(Baldosa baldosa) {
		this.camino.add(baldosa);
	}

	public Baldosa getOrigen() {
		return origen;
	}

	public Baldosa getDestino() {
		return destino;
	}

	public List<Baldosa> getCamino() {
		return camino;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

}