import java.awt.Point;

public class Camino {

	private Habitacion habitacion;
	private Point punto;

	public Camino(Habitacion habitacion, Point punto) {
		this.habitacion = habitacion;
		this.punto = punto;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

	public Point getPunto() {
		return punto;
	}

	public int getFila() {
		return (int) punto.getX();
	}

	public int getColumna() {
		return (int) punto.getY();
	}

	public boolean equals(Camino c) {
		return habitacion == c.habitacion && punto.getX() == c.punto.getX() && punto.getY() == c.punto.getY();
	}

}
