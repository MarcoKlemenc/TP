
public class Nodo {
	
    private final int fila;
    private final int columna;
    private int distance = Integer.MAX_VALUE;

    public Nodo (int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        Nodo node = (Nodo) o;
        return node.getFila() == fila && node.getColumna() == columna;
    }

	public int getFila() {
		return fila;
	}

	public int getColumna() {
		return columna;
	}
    
}
