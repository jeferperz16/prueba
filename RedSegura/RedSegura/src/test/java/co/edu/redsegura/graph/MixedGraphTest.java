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

class MixedGraphTest {

    private MixedGraph small() {
        MixedGraph g = new MixedGraph();
        g.addVertex(new Vertex("A", "Alfa", "t"));
        g.addVertex(new Vertex("B", "Beta", "t"));
        g.addVertex(new Vertex("C", "Gamma", "t"));
        g.addEdge(new Edge("E1", "dirigida", "A", "B", 5, true));
        g.addEdge(new Edge("E2", "no dirigida", "B", "C", 2, false));
        return g;
    }

    @Test
    @DisplayName("El dataset tiene 12 lugares y 18 conexiones")
    void datasetSize() {
        MixedGraph g = DatasetFactory.createSincelejoGraph();
        assertEquals(12, g.vertexCount());
        assertEquals(18, g.edgeCount());
    }

    @Test
    @DisplayName("Un ID de vértice duplicado se rechaza")
    void duplicateVertex() {
        MixedGraph g = small();
        assertThrows(RedSeguraException.class, () -> g.addVertex(new Vertex("A", "Otro", "t")));
        assertThrows(RedSeguraException.class, () -> g.addVertex(null));
    }

    @Test
    @DisplayName("Conexión con ID duplicado o con vértice inexistente se rechaza")
    void invalidEdges() {
        MixedGraph g = small();
        assertThrows(RedSeguraException.class, () -> g.addEdge(new Edge("E1", "dup", "A", "C", 1, false)));
        assertThrows(RedSeguraException.class, () -> g.addEdge(new Edge("E9", "x", "A", "Z", 1, false)));
        assertEquals(2, g.edgeCount());
    }

    @Test
    @DisplayName("Los arcos dirigidos solo se recorren hacia adelante; las aristas, en ambos sentidos")
    void neighborsRespectDirection() {
        MixedGraph g = small();
        assertEquals(1, g.neighbors("A").size());
        assertEquals("B", g.neighbors("A").get(0).vertexId());
        assertEquals(1, g.neighbors("B").size());   // B -> C (E2); E1 llega a B pero no sale
        assertEquals(1, g.neighbors("C").size());   // C -- B
        assertEquals(List.of("A", "C"), g.weakNeighbors("B"));
    }

    @Test
    @DisplayName("Consultar un vértice inexistente lanza RedSeguraException")
    void unknownVertex() {
        MixedGraph g = small();
        assertThrows(RedSeguraException.class, () -> g.neighbors("Z"));
        assertThrows(RedSeguraException.class, () -> g.getVertex("Z"));
        assertThrows(RedSeguraException.class, () -> g.neighbors(null));
        assertFalse(g.hasVertex("Z"));
    }

    @Test
    @DisplayName("Bloquear una conexión la oculta; desbloquearla la restaura")
    void blocking() {
        MixedGraph g = small();
        g.blockEdge("E2");
        assertTrue(g.isBlocked("E2"));
        assertEquals(0, g.neighbors("C").size());
        assertEquals(1, g.getActiveEdges().size());
        assertEquals(2, g.getEdges().size());
        g.unblockEdge("E2");
        assertEquals(1, g.neighbors("C").size());
        assertThrows(RedSeguraException.class, () -> g.blockEdge("E99"));
    }
}
