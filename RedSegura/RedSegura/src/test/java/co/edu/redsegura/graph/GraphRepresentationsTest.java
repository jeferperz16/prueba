package co.edu.redsegura.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.redsegura.core.DatasetFactory;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GraphRepresentationsTest {

    private MixedGraph graph;
    private GraphRepresentations rep;

    @BeforeEach
    void setUp() {
        graph = DatasetFactory.createSincelejoGraph();
        rep = new GraphRepresentations(graph);
    }

    @Test
    @DisplayName("Matriz de adyacencia: dimensión, arcos dirigidos y aristas simétricas")
    void adjacencyMatrix() {
        int[][] m = rep.adjacencyMatrix();
        assertEquals(12, m.length);
        assertEquals(4, m[0][6]);   // S01 -> S07 (R01)
        assertEquals(6, m[6][0]);   // S07 -> S01 (R02), peso distinto
        assertEquals(5, m[0][8]);   // S01 -- S09 (R03)
        assertEquals(5, m[8][0]);
        assertEquals(10, m[1][5]);  // S02 -> S06 dirigida
        assertEquals(0, m[5][1]);   // sin retorno
        assertEquals(8, m[9][7]);   // S10 -> S08 (R09)
        assertEquals(9, m[7][9]);   // S08 -> S10 (R10)
    }

    @Test
    @DisplayName("Matriz de incidencia: +1 origen, -1 destino dirigido, +1/+1 no dirigida")
    void incidenceMatrix() {
        int[][] m = rep.incidenceMatrix();
        assertEquals(12, m.length);
        assertEquals(18, m[0].length);
        assertEquals(1, m[0][0]);   // R01 origen S01
        assertEquals(-1, m[6][0]);  // R01 destino S07
        assertEquals(1, m[0][2]);   // R03 no dirigida: S01
        assertEquals(1, m[8][2]);   // R03 no dirigida: S09
        for (int j = 0; j < 18; j++) {
            int nonZero = 0;
            for (int i = 0; i < 12; i++) if (m[i][j] != 0) nonZero++;
            assertEquals(2, nonZero);   // cada conexión incide en exactamente dos vértices
        }
    }

    @Test
    @DisplayName("Lista de adyacencia respeta la orientación")
    void adjacencyList() {
        var list = rep.adjacencyList();
        assertEquals(12, list.size());
        List<String> s08 = list.get("S08").stream().map(Neighbor::vertexId).toList();
        assertEquals(List.of("S10"), s08);
        assertEquals(3, list.get("S01").size());
    }

    @Test
    @DisplayName("Las representaciones omiten conexiones bloqueadas")
    void blockedEdgesAreOmitted() {
        graph.blockEdge("R16");
        assertEquals(0, rep.adjacencyMatrix()[6][7]);
        assertEquals(17, rep.incidenceMatrix()[0].length);
        assertTrue(rep.formatAdjacencyMatrix().contains("S08"));
    }

    @Test
    @DisplayName("Conexiones paralelas conservan el menor peso en la matriz de adyacencia")
    void parallelEdges() {
        MixedGraph g = new MixedGraph();
        g.addVertex(new Vertex("A", "a", "t"));
        g.addVertex(new Vertex("B", "b", "t"));
        g.addEdge(new Edge("E1", "lenta", "A", "B", 9, true));
        g.addEdge(new Edge("E2", "rápida", "A", "B", 4, true));
        assertEquals(4, new GraphRepresentations(g).adjacencyMatrix()[0][1]);
    }
}
