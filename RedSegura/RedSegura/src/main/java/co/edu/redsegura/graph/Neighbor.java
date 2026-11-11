package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;

/**
 * Vecino alcanzable desde un vértice: el ID del vértice destino y la conexión usada.
 */
public record Neighbor(String vertexId, Edge edge) {
    public int weight() {
        return edge.getWeight();
    }
}
