
public class Nodo {

	private final int v;
	private int distance = Integer.MAX_VALUE;

	public Nodo(int v) {
		this.v = v;
	}

	public int getValue() {
		return v;
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
		return node.getValue() == v;
	}

	@Override
	public int hashCode() {
		return v;
	}
}
