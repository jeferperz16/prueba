package co.edu.redsegura.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BinarySearchTreeTest {

    private BinarySearchTree<Integer, String> tree;

    /** Árbol: 50 -> (30 -> 20, 40), (70 -> 60, 80) */
    @BeforeEach
    void setUp() {
        tree = new BinarySearchTree<>();
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80}) tree.insert(k, "v" + k);
    }

    @Test
    @DisplayName("Recorridos in/pre/post sobre un árbol conocido")
    void traversals() {
        assertEquals(List.of(20, 30, 40, 50, 60, 70, 80), tree.inOrder());
        assertEquals(List.of(50, 30, 20, 40, 70, 60, 80), tree.preOrder());
        assertEquals(List.of(20, 40, 30, 60, 80, 70, 50), tree.postOrder());
    }

    @Test
    @DisplayName("Altura y tamaño")
    void heightAndSize() {
        assertEquals(3, tree.height());
        assertEquals(7, tree.size());
        assertEquals(0, new BinarySearchTree<Integer, String>().height());
    }

    @Test
    @DisplayName("Búsqueda: encontrada con pasos y no encontrada")
    void search() {
        SearchResult<String> r = tree.search(60);
        assertTrue(r.found());
        assertEquals("v60", r.value());
        assertEquals(3, r.steps());
        SearchResult<String> miss = tree.search(65);
        assertFalse(miss.found());
        assertNull(miss.value());
    }

    @Test
    @DisplayName("Eliminar hoja, nodo con un hijo y nodo con dos hijos")
    void delete() {
        assertTrue(tree.delete(20));
        assertEquals(List.of(30, 40, 50, 60, 70, 80), tree.inOrder());
        assertTrue(tree.delete(30));
        assertEquals(List.of(40, 50, 60, 70, 80), tree.inOrder());
        assertTrue(tree.delete(50));
        assertEquals(List.of(40, 60, 70, 80), tree.inOrder());
        assertEquals(60, tree.preOrder().get(0));
        assertEquals(4, tree.size());
    }

    @Test
    @DisplayName("Eliminar una clave inexistente devuelve false y no cambia el árbol")
    void deleteMissing() {
        assertFalse(tree.delete(999));
        assertEquals(7, tree.size());
    }

    @Test
    @DisplayName("Duplicados reemplazan el valor; claves nulas se rechazan")
    void duplicatesAndNull() {
        tree.insert(50, "nuevo");
        assertEquals(7, tree.size());
        assertEquals("nuevo", tree.search(50).value());
        assertThrows(NullPointerException.class, () -> tree.insert(null, "x"));
    }

    @Test
    @DisplayName("Árbol vacío: recorridos vacíos y búsqueda sin resultados")
    void emptyTree() {
        BinarySearchTree<Integer, String> empty = new BinarySearchTree<>();
        assertTrue(empty.isEmpty());
        assertEquals(List.of(), empty.inOrder());
        assertFalse(empty.search(1).found());
        assertFalse(empty.delete(1));
    }

    @Test
    @DisplayName("Inserción ordenada degenera en lista (altura = n)")
    void degenerate() {
        BinarySearchTree<Integer, String> t = new BinarySearchTree<>();
        for (int i = 1; i <= 10; i++) t.insert(i, "v");
        assertEquals(10, t.height());
        assertEquals(10, t.search(10).steps());
    }
}
