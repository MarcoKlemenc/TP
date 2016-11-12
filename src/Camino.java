import java.awt.Point;

public class Camino {
	
	private Habitacion habitacion;
	private Point punto;
	
	public Camino(Habitacion habitacion, Point punto){
		this.habitacion = habitacion;
		this.punto = punto;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

	public void setHabitacion(Habitacion habitacion) {
		this.habitacion = habitacion;
	}

	public Point getPunto() {
		return punto;
	}

	public void setPunto(Point punto) {
		this.punto = punto;
	}

}
