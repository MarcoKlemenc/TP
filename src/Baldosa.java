import java.awt.Point;

public class Baldosa extends Superficie {

	private int fila, columna;
	private boolean pasar = true;

	public Baldosa(int x, int y, int largo, int alto, int fila, int columna) {
		super(x, y, largo, alto);
		this.fila = fila;
		this.columna = columna;
	}

	public Point getCoordenadas() {
		return new Point(getFila(), getColumna());
	}

	public boolean isPasar() {
		return pasar;
	}

	public void cambiarPasar() {
		this.pasar ^= true;
	}

	public int getFila() {
		return fila;
	}

	public void setFila(int fila) {
		this.fila = fila;
	}

	public int getColumna() {
		return columna;
	}

	public void setColumna(int columna) {
		this.columna = columna;
	}

}
