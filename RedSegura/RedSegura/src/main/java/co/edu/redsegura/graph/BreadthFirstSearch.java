package co.edu.redsegura.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Búsqueda por anchura con cola (FIFO) y conjunto de visitados. Respeta la orientación de los arcos.
 * Complejidad: tiempo O(V + E); espacio O(V).
 */
public final class BreadthFirstSearch {

    /**
     * @param order  vértices en el orden en que se visitan
     * @param parent árbol BFS: vértice -> padre (la raíz no aparece como clave)
     * @param level  distancia en número de arcos desde el origen
     */
    public record Result(List<String> order, Map<String, String> parent, Map<String, Integer> level) { }

    private final MixedGraph graph;

    public BreadthFirstSearch(MixedGraph graph) {
        this.graph = java.util.Objects.requireNonNull(graph, "El grafo no puede ser nulo");
    }

    /** @throws co.edu.redsegura.model.RedSeguraException si el origen no existe */
    public Result run(String start) {
        graph.getVertex(start); // valida existencia
        List<String> order = new ArrayList<>();
        Map<String, String> parent = new LinkedHashMap<>();
        Map<String, Integer> level = new LinkedHashMap<>();
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        visited.add(start);
        level.put(start, 0);
        queue.add(start);
        while (!queue.isEmpty()) {
            String u = queue.remove();
            order.add(u);
            for (Neighbor n : graph.neighbors(u)) {
                if (visited.add(n.vertexId())) {
                    parent.put(n.vertexId(), u);
                    level.put(n.vertexId(), level.get(u) + 1);
                    queue.add(n.vertexId());
                }
            }
        }
        return new Result(order, parent, level);
    }
}
