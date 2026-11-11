package co.edu.redsegura.graph;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Búsqueda en profundidad (recursiva e iterativa), detección de ciclos y componentes débilmente
 * conexas. Todas usan conjunto de visitados (o colores). Complejidad: tiempo O(V + E); espacio O(V)
 * (la versión recursiva usa además pila de llamadas O(V) en el peor caso).
 */
public final class DepthFirstSearch {

    /**
     * @param order  vértices en orden de descubrimiento
     * @param parent árbol DFS: vértice -> padre
     */
    public record Result(List<String> order, Map<String, String> parent) { }

    private final MixedGraph graph;

    public DepthFirstSearch(MixedGraph graph) {
        this.graph = java.util.Objects.requireNonNull(graph, "El grafo no puede ser nulo");
    }

    /** DFS recursivo desde {@code start}. @throws co.edu.redsegura.model.RedSeguraException si no existe */
    public Result run(String start) {
        graph.getVertex(start);
        List<String> order = new ArrayList<>();
        Map<String, String> parent = new LinkedHashMap<>();
        visit(start, new HashSet<>(), order, parent);
        return new Result(order, parent);
    }

    private void visit(String u, Set<String> visited, List<String> order, Map<String, String> parent) {
        visited.add(u);
        order.add(u);
        for (Neighbor n : graph.neighbors(u)) {
            if (!visited.contains(n.vertexId())) {
                parent.put(n.vertexId(), u);
                visit(n.vertexId(), visited, order, parent);
            }
        }
    }

    /**
     * DFS iterativo con pila explícita. Empuja los vecinos en orden inverso y marca al sacar,
     * por lo que produce el mismo orden de visita que la versión recursiva.
     */
    public List<String> runIterative(String start) {
        graph.getVertex(start);
        List<String> order = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            String u = stack.pop();
            if (!visited.add(u)) continue;
            order.add(u);
            List<Neighbor> ns = graph.neighbors(u);
            for (int i = ns.size() - 1; i >= 0; i--) {
                if (!visited.contains(ns.get(i).vertexId())) stack.push(ns.get(i).vertexId());
            }
        }
        return order;
    }

    /**
     * ¿El grafo tiene algún ciclo? Colores blanco/gris/negro. En aristas no dirigidas se ignora
     * únicamente la conexión por la que se llegó (para no contar el viaje de ida y vuelta por la
     * misma arista); un par de conexiones distintas entre dos vértices sí cuenta como ciclo.
     */
    public boolean hasCycle() {
        Map<String, Integer> state = new HashMap<>(); // 0/ausente = blanco, 1 = gris, 2 = negro
        for (var v : graph.getVertices()) {
            if (state.getOrDefault(v.getId(), 0) == 0 && cycleFrom(v.getId(), null, state)) {
                return true;
            }
        }
        return false;
    }

    private boolean cycleFrom(String u, String viaEdgeId, Map<String, Integer> state) {
        state.put(u, 1);
        for (Neighbor n : graph.neighbors(u)) {
            if (n.edge().getId().equals(viaEdgeId)) continue;
            int s = state.getOrDefault(n.vertexId(), 0);
            if (s == 1) return true;
            if (s == 0 && cycleFrom(n.vertexId(), n.edge().getId(), state)) return true;
        }
        state.put(u, 2);
        return false;
    }

    /** Componentes débilmente conexas (ignorando la orientación), en orden de inserción de vértices. */
    public List<List<String>> weakComponents() {
        Set<String> visited = new HashSet<>();
        List<List<String>> components = new ArrayList<>();
        for (var v : graph.getVertices()) {
            if (!visited.contains(v.getId())) {
                List<String> comp = new ArrayList<>();
                collectWeak(v.getId(), visited, comp);
                components.add(Collections.unmodifiableList(comp));
            }
        }
        return components;
    }

    private void collectWeak(String u, Set<String> visited, List<String> comp) {
        visited.add(u);
        comp.add(u);
        for (String w : graph.weakNeighbors(u)) {
            if (!visited.contains(w)) collectWeak(w, visited, comp);
        }
    }
}
