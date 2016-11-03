
final class Arista {
	
    private final Nodo node1, node2;
    private final int distance;

    public Arista (Nodo node1, Nodo node2, int distance) {
        this.node1 = node1;
        this.node2 = node2;
        this.distance = distance;
    }

    public Nodo getAdjacentNode (Nodo node) {
        //return node.getValue() != node1.getValue() ? node1 : node2;
    	return node;
    }

    public int getDistance() {
        return distance;
    }
}
