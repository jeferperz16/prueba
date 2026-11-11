package co.edu.redsegura.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.redsegura.core.DatasetFactory;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.RedSeguraException;
import co.edu.redsegura.model.Vertex;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DijkstraSolverTest {

    private MixedGraph graph;
    private DijkstraSolver solver;

    @BeforeEach
    void setUp() {
        graph = DatasetFactory.createSincelejoGraph();
        solver = new DijkstraSolver(graph);
    }

    @Test
    @DisplayName("Resultado de control: S02 a S08 cuesta 24 por S02, S01, S07, S08")
    void controlResult() {
        PathResult r = solver.shortestPath("S02", "S08");
        assertTrue(r.reachable());
        assertEquals(24, r.totalMinutes());
        assertEquals(List.of("S02", "S01", "S07", "S08"), r.vertices());
        assertEquals(List.of("R04", "R01", "R16"), r.edgeIds());
    }

    @Test
    @DisplayName("Bloquear R16: el costo sigue en 24 pero la ruta cambia (había un empate)")
    void blockR16() {
        graph.blockEdge("R16");
        PathResult r = solver.shortestPath("S02", "S08");
        assertEquals(24, r.totalMinutes());
        assertEquals(List.of("S02", "S01", "S09", "S10", "S08"), r.vertices());
    }

    @Test
    @DisplayName("Bloquear R16 y R09 deja el terminal inaccesible (grafo desconectado hacia el destino)")
    void unreachable() {
        graph.blockEdge("R16");
        graph.blockEdge("R09");
        PathResult r = solver.shortestPath("S02", "S08");
        assertFalse(r.reachable());
        assertEquals(-1, r.totalMinutes());
        assertTrue(r.vertices().isEmpty());
    }

    @Test
    @DisplayName("La orientación se respeta: S08 a S07 no usa R16 al revés")
    void directionRespected() {
        PathResult r = solver.shortestPath("S08", "S07");
        assertEquals(15, r.totalMinutes());                       // S08->S10 9, S10--S09 3, S09--S07 3
        assertEquals(List.of("S08", "S10", "S09", "S07"), r.vertices());
    }

    @Test
    @DisplayName("Origen igual a destino cuesta 0")
    void sameVertex() {
        PathResult r = solver.shortestPath("S05", "S05");
        assertEquals(0, r.totalMinutes());
        assertEquals(List.of("S05"), r.vertices());
        assertTrue(r.edgeIds().isEmpty());
    }

    @Test
    @DisplayName("Vértice inexistente (origen o destino) lanza RedSeguraException")
    void unknownVertex() {
        assertThrows(RedSeguraException.class, () -> solver.shortestPath("S99", "S08"));
        assertThrows(RedSeguraException.class, () -> solver.shortestPath("S02", "S99"));
        assertThrows(RedSeguraException.class, () -> solver.solve(null));
    }

    @Test
    @DisplayName("Grafo desconectado: los vértices de otra componente no tienen distancia")
    void disconnectedGraph() {
        MixedGraph g = new MixedGraph();
        for (String id : List.of("A", "B", "C")) g.addVertex(new Vertex(id, "L" + id, "t"));
        g.addEdge(new Edge("E1", "x", "A", "B", 3, false));
        DijkstraSolver.Solution s = new DijkstraSolver(g).solve("A");
        assertEquals(3, s.distances().get("B"));
        assertFalse(s.distances().containsKey("C"));
    }

    @Test
    @DisplayName("Pesos inválidos no pueden llegar al grafo (Edge los rechaza)")
    void invalidWeights() {
        assertThrows(RedSeguraException.class, () -> new Edge("E1", "x", "A", "B", -1, false));
        assertThrows(RedSeguraException.class, () -> new Edge("E1", "x", "A", "B", 0, true));
    }

    @Test
    @DisplayName("Predecesores y distancias del dataset coinciden con el cálculo manual")
    void distancesFromS02() {
        DijkstraSolver.Solution s = solver.solve("S02");
        assertEquals(8, s.distances().get("S01"));
        assertEquals(10, s.distances().get("S06"));
        assertEquals(14, s.distances().get("S05"));
        assertEquals(13, s.distances().get("S09"));
        assertEquals(35, s.distances().get("S11"));
        assertEquals("S09", s.predecessors().get("S12"));
    }
}
