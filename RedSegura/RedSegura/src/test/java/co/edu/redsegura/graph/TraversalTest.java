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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TraversalTest {

    private MixedGraph disconnected() {
        MixedGraph g = new MixedGraph();
        for (String id : List.of("A", "B", "C", "D")) g.addVertex(new Vertex(id, "L" + id, "t"));
        g.addEdge(new Edge("E1", "x", "A", "B", 1, false));
        g.addEdge(new Edge("E2", "y", "C", "D", 1, true));
        return g;
    }

    @Test
    @DisplayName("BFS desde S02 en el dataset: orden, niveles y árbol")
    void bfsDataset() {
        BreadthFirstSearch.Result r = new BreadthFirstSearch(DatasetFactory.createSincelejoGraph()).run("S02");
        assertEquals(List.of("S02", "S01", "S06", "S07", "S09", "S05", "S08", "S10", "S12", "S03", "S04", "S11"),
                r.order());
        assertEquals(0, r.level().get("S02"));
        assertEquals(1, r.level().get("S06"));
        assertEquals(5, r.level().get("S11"));
        assertEquals("S01", r.parent().get("S07"));
    }

    @Test
    @DisplayName("DFS desde S02 en el dataset: orden recursivo = iterativo y difiere del BFS")
    void dfsDataset() {
        MixedGraph g = DatasetFactory.createSincelejoGraph();
        DepthFirstSearch dfs = new DepthFirstSearch(g);
        DepthFirstSearch.Result r = dfs.run("S02");
        assertEquals(List.of("S02", "S01", "S07", "S09", "S05", "S10", "S08", "S12", "S03", "S04", "S11", "S06"),
                r.order());
        assertEquals(r.order(), dfs.runIterative("S02"));
        assertFalse(r.order().equals(new BreadthFirstSearch(g).run("S02").order()));
    }

    @Test
    @DisplayName("Ambos recorridos visitan cada vértice una sola vez aunque haya ciclos")
    void noRepeats() {
        MixedGraph g = DatasetFactory.createSincelejoGraph();
        List<String> bfs = new BreadthFirstSearch(g).run("S01").order();
        List<String> dfs = new DepthFirstSearch(g).run("S01").order();
        assertEquals(bfs.size(), bfs.stream().distinct().count());
        assertEquals(dfs.size(), dfs.stream().distinct().count());
    }

    @Test
    @DisplayName("Vértice inexistente: BFS y DFS lanzan RedSeguraException")
    void unknownStart() {
        MixedGraph g = DatasetFactory.createSincelejoGraph();
        assertThrows(RedSeguraException.class, () -> new BreadthFirstSearch(g).run("S99"));
        assertThrows(RedSeguraException.class, () -> new DepthFirstSearch(g).run("S99"));
        assertThrows(RedSeguraException.class, () -> new DepthFirstSearch(g).runIterative("S99"));
    }

    @Test
    @DisplayName("Grafo desconectado: los recorridos solo alcanzan su zona y hay dos componentes")
    void disconnectedGraph() {
        MixedGraph g = disconnected();
        assertEquals(List.of("A", "B"), new BreadthFirstSearch(g).run("A").order());
        assertEquals(List.of("C", "D"), new DepthFirstSearch(g).run("C").order());
        assertEquals(List.of("D"), new BreadthFirstSearch(g).run("D").order()); // D no tiene salidas
        assertEquals(List.of(List.of("A", "B"), List.of("C", "D")), new DepthFirstSearch(g).weakComponents());
    }

    @Test
    @DisplayName("Ciclos: el dataset tiene ciclo; una arista simple y una cadena dirigida no")
    void cycles() {
        assertTrue(new DepthFirstSearch(DatasetFactory.createSincelejoGraph()).hasCycle());
        assertFalse(new DepthFirstSearch(disconnected()).hasCycle());
    }

    @Test
    @DisplayName("Dos aristas distintas entre los mismos vértices forman ciclo")
    void parallelUndirectedCycle() {
        MixedGraph g = new MixedGraph();
        g.addVertex(new Vertex("A", "a", "t"));
        g.addVertex(new Vertex("B", "b", "t"));
        g.addEdge(new Edge("E1", "x", "A", "B", 1, false));
        assertFalse(new DepthFirstSearch(g).hasCycle());
        g.addEdge(new Edge("E2", "y", "A", "B", 2, false));
        assertTrue(new DepthFirstSearch(g).hasCycle());
    }

    @Test
    @DisplayName("El dataset es débilmente conexo (un componente)")
    void datasetIsWeaklyConnected() {
        assertEquals(1, new DepthFirstSearch(DatasetFactory.createSincelejoGraph()).weakComponents().size());
    }
}
