package co.edu.redsegura.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModelTest {

    @Test
    @DisplayName("Un vértice válido conserva ID, etiqueta y tipo")
    void validVertex() {
        Vertex v = new Vertex("S01", "UAJS Sede C", "Universidad");
        assertEquals("S01", v.getId());
        assertEquals("UAJS Sede C", v.getLabel());
        assertEquals("Universidad", v.getType());
    }

    @Test
    @DisplayName("Un vértice con datos vacíos o nulos se rechaza")
    void invalidVertex() {
        assertThrows(RedSeguraException.class, () -> new Vertex("", "x", "y"));
        assertThrows(RedSeguraException.class, () -> new Vertex("S01", null, "y"));
        assertThrows(RedSeguraException.class, () -> new Vertex("S01", "x", "  "));
    }

    @Test
    @DisplayName("Una conexión válida conserva ID, etiqueta, peso y orientación")
    void validEdge() {
        Edge e = new Edge("R01", "Corredor universitario", "S01", "S07", 4, true);
        assertEquals("R01", e.getId());
        assertEquals("Corredor universitario", e.getLabel());
        assertEquals(4, e.getWeight());
        assertTrue(e.isDirected());
        assertEquals("S07", e.otherEnd("S01"));
    }

    @Test
    @DisplayName("Pesos inválidos (cero o negativos) se rechazan")
    void invalidWeights() {
        assertThrows(RedSeguraException.class, () -> new Edge("R1", "x", "A", "B", 0, false));
        assertThrows(RedSeguraException.class, () -> new Edge("R1", "x", "A", "B", -5, true));
    }

    @Test
    @DisplayName("Un lazo y un ID vacío se rechazan")
    void invalidEdges() {
        assertThrows(RedSeguraException.class, () -> new Edge("R1", "x", "A", "A", 3, false));
        assertThrows(RedSeguraException.class, () -> new Edge(" ", "x", "A", "B", 3, false));
    }

    @Test
    @DisplayName("otherEnd con un vértice ajeno falla")
    void otherEndForeign() {
        Edge e = new Edge("R1", "x", "A", "B", 3, false);
        assertThrows(RedSeguraException.class, () -> e.otherEnd("Z"));
        assertFalse(e.isDirected());
    }
}
