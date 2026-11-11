package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Rutas mínimas desde un origen con Dijkstra (PriorityQueue con eliminación perezosa de entradas
 * obsoletas). Respeta la orientación de los arcos y omite conexiones bloqueadas.
 * <p>Requiere pesos no negativos; {@link Edge} garantiza pesos &gt; 0.</p>
 * <p>Desempates: la relajación solo actualiza cuando la nueva distancia es estrictamente menor,
 * así que ante rutas de igual costo se conserva la que se descubrió primero (la del vértice
 * con menor distancia extraído antes de la cola).</p>
 * <p>Complejidad: tiempo O((V + E) log V); espacio O(V + E).</p>
 */
public final class DijkstraSolver {

    /**
     * @param distances    mínimos minutos desde el origen; los vértices inalcanzables no aparecen
     * @param predecessors vértice -> predecesor en el camino mínimo
     * @param viaEdges     vértice -> ID de la conexión con la que se llegó a él
     */
    public record Solution(String source, Map<String, Integer> distances,
                           Map<String, String> predecessors, Map<String, String> viaEdges) { }

    private record Entry(String vertexId, int distance) { }

    private final MixedGraph graph;

    public DijkstraSolver(MixedGraph graph) {
        this.graph = java.util.Objects.requireNonNull(graph, "El grafo no puede ser nulo");
    }

    /** @throws co.edu.redsegura.model.RedSeguraException si el origen no existe */
    public Solution solve(String source) {
        graph.getVertex(source);
        Map<String, Integer> dist = new LinkedHashMap<>();
        Map<String, String> pred = new HashMap<>();
        Map<String, String> via = new HashMap<>();
        Set<String> done = new HashSet<>();
        PriorityQueue<Entry> pq = new PriorityQueue<>(
                (a, b) -> a.distance() != b.distance()
                        ? Integer.compare(a.distance(), b.distance())
                        : a.vertexId().compareTo(b.vertexId()));

        dist.put(source, 0);
        pq.add(new Entry(source, 0));
        while (!pq.isEmpty()) {
            Entry current = pq.poll();
            String u = current.vertexId();
            if (!done.add(u)) continue; // entrada obsoleta
            for (Neighbor n : graph.neighbors(u)) {
                String v = n.vertexId();
                if (done.contains(v)) continue;
                int candidate = current.distance() + n.weight();
                Integer known = dist.get(v);
                if (known == null || candidate < known) { // relajación
                    dist.put(v, candidate);
                    pred.put(v, u);
                    via.put(v, n.edge().getId());
                    pq.add(new Entry(v, candidate));
                }
            }
        }
        return new Solution(source, dist, pred, via);
    }

    /**
     * Ruta mínima de {@code source} a {@code target}, reconstruida con el mapa de predecesores.
     * @throws co.edu.redsegura.model.RedSeguraException si algún extremo no existe
     */
    public PathResult shortestPath(String source, String target) {
        graph.getVertex(target);
        Solution sol = solve(source);
        Integer total = sol.distances().get(target);
        if (total == null) {
            return new PathResult(source, target, false, -1, List.of(), List.of());
        }
        List<String> vertices = new ArrayList<>();
        List<String> edgeIds = new ArrayList<>();
        for (String at = target; at != null; at = sol.predecessors().get(at)) {
            vertices.add(at);
            String e = sol.viaEdges().get(at);
            if (e != null) edgeIds.add(e);
        }
        Collections.reverse(vertices);
        Collections.reverse(edgeIds);
        return new PathResult(source, target, true, total, vertices, edgeIds);
    }
}
