package co.edu.redsegura.graph;

import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Construye las tres representaciones clásicas a partir de un {@link MixedGraph}
 * usando solo su API pública. Refleja el estado actual (las conexiones bloqueadas no aparecen).
 *
 * <ul>
 *   <li>Lista de adyacencia: espacio O(V + E).</li>
 *   <li>Matriz de adyacencia ponderada V x V: espacio O(V²). 0 = sin conexión
 *       (válido porque los pesos son &gt; 0). Con conexiones paralelas se guarda el menor peso.</li>
 *   <li>Matriz de incidencia V x E: espacio O(V·E). Arco dirigido: +1 en el origen y -1 en el
 *       destino. Arista no dirigida: +1 en ambos extremos.</li>
 * </ul>
 */
public final class GraphRepresentations {

    private final MixedGraph graph;

    public GraphRepresentations(MixedGraph graph) {
        this.graph = java.util.Objects.requireNonNull(graph, "El grafo no puede ser nulo");
    }

    /** IDs de vértices en el orden de filas/columnas de las matrices. */
    public List<String> vertexIds() {
        List<String> ids = new ArrayList<>();
        for (Vertex v : graph.getVertices()) ids.add(v.getId());
        return ids;
    }

    /** IDs de conexiones activas en el orden de columnas de la matriz de incidencia. */
    public List<String> edgeIds() {
        List<String> ids = new ArrayList<>();
        for (Edge e : graph.getActiveEdges()) ids.add(e.getId());
        return ids;
    }

    public Map<String, List<Neighbor>> adjacencyList() {
        Map<String, List<Neighbor>> map = new LinkedHashMap<>();
        for (Vertex v : graph.getVertices()) {
            map.put(v.getId(), graph.neighbors(v.getId()));
        }
        return map;
    }

    public int[][] adjacencyMatrix() {
        List<String> ids = vertexIds();
        Map<String, Integer> index = indexOf(ids);
        int n = ids.size();
        int[][] m = new int[n][n];
        for (Edge e : graph.getActiveEdges()) {
            int s = index.get(e.getSourceId());
            int t = index.get(e.getTargetId());
            put(m, s, t, e.getWeight());
            if (!e.isDirected()) put(m, t, s, e.getWeight());
        }
        return m;
    }

    public int[][] incidenceMatrix() {
        List<String> ids = vertexIds();
        Map<String, Integer> index = indexOf(ids);
        List<Edge> active = graph.getActiveEdges();
        int[][] m = new int[ids.size()][active.size()];
        for (int j = 0; j < active.size(); j++) {
            Edge e = active.get(j);
            int s = index.get(e.getSourceId());
            int t = index.get(e.getTargetId());
            m[s][j] = 1;
            m[t][j] = e.isDirected() ? -1 : 1;
        }
        return m;
    }

    // ---------- Formato de texto para consola ----------

    public String formatAdjacencyList() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Neighbor>> entry : adjacencyList().entrySet()) {
            String id = entry.getKey();
            sb.append(String.format("%s %-24s -> ", id, graph.getVertex(id).getLabel()));
            List<Neighbor> list = entry.getValue();
            if (list.isEmpty()) {
                sb.append("(sin salidas)");
            } else {
                for (int i = 0; i < list.size(); i++) {
                    Neighbor n = list.get(i);
                    if (i > 0) sb.append(" | ");
                    sb.append(n.vertexId()).append('(').append(n.weight()).append(" min, ")
                      .append(n.edge().getId()).append(')');
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String formatAdjacencyMatrix() {
        List<String> ids = vertexIds();
        int[][] m = adjacencyMatrix();
        StringBuilder sb = new StringBuilder("     ");
        for (String id : ids) sb.append(String.format("%5s", id));
        sb.append('\n');
        for (int i = 0; i < ids.size(); i++) {
            sb.append(String.format("%-5s", ids.get(i)));
            for (int j = 0; j < ids.size(); j++) {
                sb.append(String.format("%5s", m[i][j] == 0 ? "." : String.valueOf(m[i][j])));
            }
            sb.append('\n');
        }
        sb.append("(. = sin conexión; valores en minutos; fila = origen, columna = destino)\n");
        return sb.toString();
    }

    public String formatIncidenceMatrix() {
        List<String> ids = vertexIds();
        List<String> eIds = edgeIds();
        int[][] m = incidenceMatrix();
        StringBuilder sb = new StringBuilder("     ");
        for (String id : eIds) sb.append(String.format("%5s", id));
        sb.append('\n');
        for (int i = 0; i < ids.size(); i++) {
            sb.append(String.format("%-5s", ids.get(i)));
            for (int j = 0; j < eIds.size(); j++) {
                int v = m[i][j];
                sb.append(String.format("%5s", v > 0 ? "+" + v : String.valueOf(v)));
            }
            sb.append('\n');
        }
        sb.append("(+1 origen o extremo de arista no dirigida; -1 destino de arco dirigido; 0 sin incidencia)\n");
        return sb.toString();
    }

    // ---------- Internos ----------

    private static void put(int[][] m, int i, int j, int w) {
        if (m[i][j] == 0 || w < m[i][j]) m[i][j] = w;
    }

    private static Map<String, Integer> indexOf(List<String> ids) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) index.put(ids.get(i), i);
        return index;
    }
}
