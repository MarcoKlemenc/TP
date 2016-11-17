import java.awt.Point;

public class Recorrido {

	private Habitacion habitacion;
	private Point coordenadas;

	public Recorrido(Habitacion habitacion, Point coordenadas) {
		this.habitacion = habitacion;
		this.coordenadas = coordenadas;
	}

	public Habitacion getHabitacion() {
		return habitacion;
	}

	public Point getCoordenadas() {
		return coordenadas;
	}

	public int getFila() {
		return (int) coordenadas.getX();
	}

	public int getColumna() {
		return (int) coordenadas.getY();
	}

	public boolean equals(Recorrido c) {
		return habitacion == c.habitacion && coordenadas.equals(c.getCoordenadas());
	}

	public String toString() {
		return habitacion.getId() + "Ç" + (int) coordenadas.getX() + "Ç" + (int) coordenadas.getY() + "Ç";
	}

}
