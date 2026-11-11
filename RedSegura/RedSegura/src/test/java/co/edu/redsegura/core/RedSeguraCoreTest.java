package co.edu.redsegura.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import co.edu.redsegura.model.RedSeguraException;
import co.edu.redsegura.tree.RotationType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RedSeguraCoreTest {

    private RedSeguraCore core;

    @BeforeEach
    void setUp() {
        core = RedSeguraCore.withDefaultDataset();
    }

    @Test
    @DisplayName("Encuentra lugares por etiqueta con el AVL y devuelve vacío si no existe")
    void findByLabel() {
        assertEquals("S05", core.findPlaceByLabel("Hospital Universitario").orElseThrow().getId());
        assertTrue(core.findPlaceByLabel("Lugar que no existe").isEmpty());
    }

    @Test
    @DisplayName("Q1: el AVL nunca necesita más pasos que el ABB en inserción ordenada y su altura es menor")
    void compareSearch() {
        SearchComparison sorted = core.compareSearchSortedInsertion("Hospital Universitario");
        assertTrue(sorted.bst().found());
        assertTrue(sorted.avl().found());
        assertEquals(12, sorted.bstHeight());
        assertTrue(sorted.avlHeight() <= 4);
        assertTrue(sorted.avl().steps() <= sorted.bst().steps());
        SearchComparison dataset = core.compareSearch("Hospital Universitario");
        assertTrue(dataset.avl().steps() <= dataset.bst().steps());
    }

    @Test
    @DisplayName("Q2: las cuatro rotaciones ocurren con las secuencias de tres claves")
    void rotations() {
        List<RotationDemo> demos = core.demonstrateRotations();
        assertEquals(4, demos.size());
        for (RotationDemo d : demos) {
            assertEquals(List.of(d.expected()), d.applied());
            assertEquals(List.of(20, 10, 30), d.preOrderAfter());
        }
        assertEquals(List.of(RotationType.LL, RotationType.RR, RotationType.LR, RotationType.RL),
                demos.stream().map(RotationDemo::expected).toList());
    }

    @Test
    @DisplayName("Q5: ruta mínima S02 a S08 = 24 minutos")
    void shortestRoute() {
        var r = core.shortestRoute("S02", "S08");
        assertEquals(24, r.totalMinutes());
        assertEquals(List.of("S02", "S01", "S07", "S08"), r.vertices());
    }

    @Test
    @DisplayName("Q6: bloquear y desbloquear una ruta cambia y restaura el resultado")
    void blockAndRestore() {
        core.blockRoute("R16");
        assertTrue(core.isRouteBlocked("R16"));
        assertEquals(List.of("S02", "S01", "S09", "S10", "S08"), core.shortestRoute("S02", "S08").vertices());
        core.unblockRoute("R16");
        assertEquals(List.of("S02", "S01", "S07", "S08"), core.shortestRoute("S02", "S08").vertices());
        assertThrows(RedSeguraException.class, () -> core.blockRoute("R99"));
    }

    @Test
    @DisplayName("Q7: un vértice inexistente produce RedSeguraException, no un fallo del programa")
    void unknownVertex() {
        assertThrows(RedSeguraException.class, () -> core.getPlace("S99"));
        assertThrows(RedSeguraException.class, () -> core.bfs("S99"));
        assertThrows(RedSeguraException.class, () -> core.shortestRoute("S02", "S99"));
        assertEquals("S99", core.labelOf("S99"));
        assertEquals(12, core.placeCount());   // el núcleo sigue funcionando
    }

    @Test
    @DisplayName("Rutas de entrada a un destino y análisis del grafo")
    void inboundAndStructure() {
        assertEquals(List.of("R09", "R16"), core.inboundRoutes("S08").stream().map(e -> e.getId()).toList());
        assertTrue(core.hasCycle());
        assertEquals(1, core.weakComponents().size());
        assertFalse(core.representations().formatIncidenceMatrix().isEmpty());
    }
}
