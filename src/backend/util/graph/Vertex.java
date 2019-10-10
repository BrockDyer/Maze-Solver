package backend.util.graph;

import java.util.HashSet;
import java.util.Set;

/**
 * A vertex in a graph.
 *
 * @author Brock Dyer.
 */
public class Vertex<T> {

    /**
     * The neighbors of this vertex.
     */
    private Set<Vertex<T>> neighbors;

    /**
     * The value of this vertex.
     */
    private T value;

    /**
     * Construct a new vertex.
     *
     * @param value the value of this vertex.
     */
    public Vertex(T value) {
        this.value = value;

        this.neighbors = new HashSet<>();
    }

    /**
     * Get the value of this vertex.
     *
     * @return the value of this vertex.
     */
    public T getValue() {
        return value;
    }

    /**
     * Add a neighbor to this vertex.
     *
     * @param vertex the new vertex to add.
     */
    public void addNeighbor(Vertex<T> vertex) {

        if(this.neighbors.contains(vertex)){
            return;
        }
        this.neighbors.add(vertex);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Vertex) {
            Vertex other = (Vertex) obj;
            return this.value.equals(other.value);
        }

        return false;
    }

    /**
     * Print out the neighbors of this vertex.
     */
    public void printNeighbors() {
        for (Vertex<T> v : neighbors) {
            System.out.println(v.toString());
        }
    }

    /**
     * Get the neighbors of this vertex.
     *
     * @return the set of this vertex's neighbors.
     */
    public Set<Vertex<T>> getNeighbors(){
        return neighbors;
    }

    @Override
    public String toString() {
        return "Vertex( " + value.toString() + " )";
    }
}
