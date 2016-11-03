
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Grafo {

    private final Map<Nodo, ArrayList<Arista>> map;
    private final int numberOfVertices; 

    Grafo(int numberOfVertices) {
        if (numberOfVertices < 0) {
            throw new IllegalArgumentException("A vertex cannot be less than zero");
        }
        this.numberOfVertices = numberOfVertices;
        this.map = new HashMap<Nodo, ArrayList<Arista>>();
    }

    public void addEdge (Nodo node1, Nodo node2, int distance) {
        // necessary to throw null ptr exceptions explicitly since, null can get propagated and saved in edge
        if (node1 == null || node2 == null) {
            throw new NullPointerException("Either of the 2 nodes is null.");
        }
        if (distance < 0) {
            throw new IllegalArgumentException(" The distance cannot be negative. ");
        }

        Arista edge = new Arista(node1, node2, distance);

        addToMap(node1, edge);
        addToMap(node2, edge);
    }

    private void addToMap (Nodo node, Arista edge) {
        if (map.containsKey(node)) {
            List<Arista> l = map.get(node);
            l.add(edge);
        } else  {
            List<Arista> l = new ArrayList<Arista>();
            l.add(edge);
            map.put(node, (ArrayList<Arista>) l);
        }  
    }

    public List<Arista> getAdj(Nodo node) {
        return map.get(node);
    }

    public Map<Nodo, ArrayList<Arista>> getGraph() {
        return map;
    }


    public int getNumVertices() {
        return numberOfVertices;
    }
}
