package co.edu.redsegura.core;

import co.edu.redsegura.graph.BreadthFirstSearch;
import co.edu.redsegura.graph.DepthFirstSearch;
import co.edu.redsegura.graph.DijkstraSolver;
import co.edu.redsegura.graph.GraphRepresentations;
import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.graph.PathResult;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.RedSeguraException;
import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.tree.AvlTree;
import co.edu.redsegura.tree.BinarySearchTree;
import co.edu.redsegura.tree.RotationType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Fachada del dominio. {@code RedSeguraApplication} solo habla con esta clase; ella usa
 * {@link AvlTree} (índice de lugares por etiqueta) y {@link MixedGraph} (movilidad), y expone
 * BFS, DFS, Dijkstra y las representaciones. Todo se hace por métodos públicos.
 * <p>Los índices por etiqueta se construyen al crear el núcleo a partir de los vértices del grafo.</p>
 */
public final class RedSeguraCore {

    private final MixedGraph graph;
    private final AvlTree<String, Vertex> avlByLabel = new AvlTree<>();
    private final BinarySearchTree<String, Vertex> bstByLabel = new BinarySearchTree<>();
    private final BreadthFirstSearch bfs;
    private final DepthFirstSearch dfs;
    private final DijkstraSolver dijkstra;
    private final GraphRepresentations representations;

    public RedSeguraCore(MixedGraph graph) {
        this.graph = java.util.Objects.requireNonNull(graph, "El grafo no puede ser nulo");
        for (Vertex v : graph.getVertices()) {
            avlByLabel.insert(v.getLabel(), v);
            bstByLabel.insert(v.getLabel(), v);
        }
        this.bfs = new BreadthFirstSearch(graph);
        this.dfs = new DepthFirstSearch(graph);
        this.dijkstra = new DijkstraSolver(graph);
        this.representations = new GraphRepresentations(graph);
    }

    /** Núcleo con el conjunto de datos suministrado (fábrica). */
    public static RedSeguraCore withDefaultDataset() {
        return new RedSeguraCore(DatasetFactory.createSincelejoGraph());
    }

    // ---------- Lugares ----------

    public List<Vertex> places() { return graph.getVertices(); }

    public List<Edge> routes() { return graph.getEdges(); }

    /** @throws RedSeguraException si el ID no existe */
    public Vertex getPlace(String id) { return graph.getVertex(id); }

    /** Etiqueta del vértice, o el propio ID si no existe (útil para imprimir). */
    public String labelOf(String id) {
        return graph.hasVertex(id) ? graph.getVertex(id).getLabel() : id;
    }

    /** Busca un lugar por etiqueta exacta usando el AVL. */
    public Optional<Vertex> findPlaceByLabel(String label) {
        var r = avlByLabel.search(label);
        return r.found() ? Optional.of(r.value()) : Optional.empty();
    }

    public int placeCount() { return graph.vertexCount(); }

    public int routeCount() { return graph.edgeCount(); }

    // ---------- Q1: ABB vs AVL ----------

    /** Compara la búsqueda con inserción en el orden del dataset (S01..S12). */
    public SearchComparison compareSearch(String label) {
        return new SearchComparison("orden del dataset (S01..S12)", label,
                bstByLabel.search(label), avlByLabel.search(label),
                bstByLabel.height(), avlByLabel.height(), avlByLabel.size());
    }

    /**
     * Compara la búsqueda cuando las etiquetas se insertan en orden alfabético ascendente,
     * el peor caso del ABB (degenera en una lista); el AVL se mantiene balanceado.
     */
    public SearchComparison compareSearchSortedInsertion(String label) {
        List<Vertex> sorted = new ArrayList<>(graph.getVertices());
        sorted.sort(Comparator.comparing(Vertex::getLabel));
        BinarySearchTree<String, Vertex> bst = new BinarySearchTree<>();
        AvlTree<String, Vertex> avl = new AvlTree<>();
        for (Vertex v : sorted) {
            bst.insert(v.getLabel(), v);
            avl.insert(v.getLabel(), v);
        }
        return new SearchComparison("orden alfabético ascendente (peor caso del ABB)", label,
                bst.search(label), avl.search(label), bst.height(), avl.height(), avl.size());
    }

    // ---------- Q2: rotaciones AVL ----------

    /** Ejecuta las cuatro rotaciones con secuencias de tres claves y devuelve la evidencia. */
    public List<RotationDemo> demonstrateRotations() {
        List<RotationDemo> out = new ArrayList<>();
        out.add(runRotation(RotationType.LL, List.of(30, 20, 10)));
        out.add(runRotation(RotationType.RR, List.of(10, 20, 30)));
        out.add(runRotation(RotationType.LR, List.of(30, 10, 20)));
        out.add(runRotation(RotationType.RL, List.of(10, 30, 20)));
        return out;
    }

    private RotationDemo runRotation(RotationType expected, List<Integer> keys) {
        AvlTree<Integer, String> tree = new AvlTree<>();
        for (int k : keys) tree.insert(k, "n" + k);
        return new RotationDemo(expected, keys, new ArrayList<>(tree.getRotationLog()),
                tree.preOrder(), tree.height(), tree.render());
    }

    // ---------- Q3: representaciones ----------

    public GraphRepresentations representations() { return representations; }

    // ---------- Q4: recorridos ----------

    public BreadthFirstSearch.Result bfs(String start) { return bfs.run(start); }

    public DepthFirstSearch.Result dfs(String start) { return dfs.run(start); }

    public List<String> dfsIterative(String start) { return dfs.runIterative(start); }

    public boolean hasCycle() { return dfs.hasCycle(); }

    public List<List<String>> weakComponents() { return dfs.weakComponents(); }

    // ---------- Q5 y Q6: rutas ----------

    public PathResult shortestRoute(String from, String to) { return dijkstra.shortestPath(from, to); }

    public DijkstraSolver.Solution distancesFrom(String from) { return dijkstra.solve(from); }

    /** @throws RedSeguraException si la conexión no existe */
    public void blockRoute(String edgeId) { graph.blockEdge(edgeId); }

    /** @throws RedSeguraException si la conexión no existe */
    public void unblockRoute(String edgeId) { graph.unblockEdge(edgeId); }

    public boolean isRouteBlocked(String edgeId) { return graph.isBlocked(edgeId); }

    /** Conexiones activas que permiten llegar a {@code vertexId} (arcos que entran o aristas que lo tocan). */
    public List<Edge> inboundRoutes(String vertexId) {
        graph.getVertex(vertexId);
        List<Edge> out = new ArrayList<>();
        for (Edge e : graph.getActiveEdges()) {
            boolean arrives = e.isDirected() ? e.getTargetId().equals(vertexId)
                    : (e.getSourceId().equals(vertexId) || e.getTargetId().equals(vertexId));
            if (arrives) out.add(e);
        }
        return out;
    }

    // ---------- Información de árboles ----------

    public int bstHeight() { return bstByLabel.height(); }

    public int avlHeight() { return avlByLabel.height(); }

    public List<String> avlInOrder() { return avlByLabel.inOrder(); }
}
