package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.RedSeguraException;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo mixto (aristas no dirigidas y arcos dirigidos), etiquetado y ponderado.
 * <p>Los datos son privados; otros módulos solo usan los métodos públicos.
 * Las conexiones se pueden bloquear temporalmente ({@link #blockEdge}) sin borrarlas:
 * una conexión bloqueada no aparece en vecinos, representaciones ni algoritmos.</p>
 * <p>Complejidad: addVertex/addEdge O(1); neighbors(v) O(grado(v)); espacio O(V + E).</p>
 */
public final class MixedGraph {

    private final Map<String, Vertex> vertices = new LinkedHashMap<>();
    private final Map<String, Edge> edges = new LinkedHashMap<>();
    /** Conexiones incidentes por vértice (en orden de inserción), incluidas las bloqueadas. */
    private final Map<String, List<Edge>> incident = new LinkedHashMap<>();
    private final Set<String> blocked = new HashSet<>();

    // ---------- Construcción ----------

    /** @throws RedSeguraException si el vértice es nulo o su ID ya existe */
    public void addVertex(Vertex vertex) {
        if (vertex == null) {
            throw new RedSeguraException("El vértice no puede ser nulo.");
        }
        if (vertices.containsKey(vertex.getId())) {
            throw new RedSeguraException("Ya existe un vértice con ID " + vertex.getId() + ".");
        }
        vertices.put(vertex.getId(), vertex);
        incident.put(vertex.getId(), new ArrayList<>());
    }

    /** @throws RedSeguraException si la conexión es nula, su ID está duplicado o algún extremo no existe */
    public void addEdge(Edge edge) {
        if (edge == null) {
            throw new RedSeguraException("La conexión no puede ser nula.");
        }
        if (edges.containsKey(edge.getId())) {
            throw new RedSeguraException("Ya existe una conexión con ID " + edge.getId() + ".");
        }
        requireVertex(edge.getSourceId());
        requireVertex(edge.getTargetId());
        edges.put(edge.getId(), edge);
        incident.get(edge.getSourceId()).add(edge);
        incident.get(edge.getTargetId()).add(edge);
    }

    // ---------- Consulta ----------

    public boolean hasVertex(String id) {
        return id != null && vertices.containsKey(id);
    }

    /** @throws RedSeguraException si el vértice no existe */
    public Vertex getVertex(String id) {
        requireVertex(id);
        return vertices.get(id);
    }

    /** @throws RedSeguraException si la conexión no existe */
    public Edge getEdge(String id) {
        Edge e = id == null ? null : edges.get(id);
        if (e == null) {
            throw new RedSeguraException("La conexión " + id + " no existe en el grafo.");
        }
        return e;
    }

    public boolean hasEdge(String id) {
        return id != null && edges.containsKey(id);
    }

    /** Vértices en orden de inserción (lista no modificable). */
    public List<Vertex> getVertices() {
        return Collections.unmodifiableList(new ArrayList<>(vertices.values()));
    }

    /** Todas las conexiones, bloqueadas o no, en orden de inserción. */
    public List<Edge> getEdges() {
        return Collections.unmodifiableList(new ArrayList<>(edges.values()));
    }

    /** Conexiones no bloqueadas, en orden de inserción. */
    public List<Edge> getActiveEdges() {
        List<Edge> out = new ArrayList<>();
        for (Edge e : edges.values()) {
            if (!blocked.contains(e.getId())) out.add(e);
        }
        return out;
    }

    public int vertexCount() { return vertices.size(); }

    public int edgeCount() { return edges.size(); }

    /**
     * Vecinos alcanzables saliendo de {@code vertexId}: por aristas no dirigidas en cualquier
     * sentido y por arcos dirigidos solo si el vértice es su origen. Omite conexiones bloqueadas.
     * El orden es el de inserción de las conexiones.
     *
     * @throws RedSeguraException si el vértice no existe
     */
    public List<Neighbor> neighbors(String vertexId) {
        requireVertex(vertexId);
        List<Neighbor> out = new ArrayList<>();
        for (Edge e : incident.get(vertexId)) {
            if (blocked.contains(e.getId())) continue;
            if (e.isDirected()) {
                if (e.getSourceId().equals(vertexId)) {
                    out.add(new Neighbor(e.getTargetId(), e));
                }
            } else {
                out.add(new Neighbor(e.otherEnd(vertexId), e));
            }
        }
        return out;
    }

    /**
     * Vecinos ignorando la orientación (grafo subyacente), sin repetidos. Se usa para
     * componentes débilmente conexas. Omite conexiones bloqueadas.
     */
    public List<String> weakNeighbors(String vertexId) {
        requireVertex(vertexId);
        Set<String> seen = new java.util.LinkedHashSet<>();
        for (Edge e : incident.get(vertexId)) {
            if (!blocked.contains(e.getId())) seen.add(e.otherEnd(vertexId));
        }
        return new ArrayList<>(seen);
    }

    // ---------- Bloqueo de conexiones ----------

    /** @throws RedSeguraException si la conexión no existe */
    public void blockEdge(String edgeId) {
        getEdge(edgeId);
        blocked.add(edgeId);
    }

    /** @throws RedSeguraException si la conexión no existe */
    public void unblockEdge(String edgeId) {
        getEdge(edgeId);
        blocked.remove(edgeId);
    }

    public boolean isBlocked(String edgeId) {
        return blocked.contains(edgeId);
    }

    // ---------- Internos ----------

    private void requireVertex(String id) {
        if (id == null || !vertices.containsKey(id)) {
            throw new RedSeguraException("El vértice " + id + " no existe en el grafo.");
        }
    }
}
