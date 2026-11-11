package co.edu.redsegura.tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AvlTreeTest {

    private AvlTree<Integer, String> build(int... keys) {
        AvlTree<Integer, String> t = new AvlTree<>();
        for (int k : keys) t.insert(k, "v" + k);
        return t;
    }

    @Test
    @DisplayName("Rotación LL (30, 20, 10)")
    void rotationLL() {
        AvlTree<Integer, String> t = build(30, 20, 10);
        assertEquals(List.of(RotationType.LL), t.getRotationLog());
        assertEquals(List.of(20, 10, 30), t.preOrder());
        assertEquals(2, t.height());
    }

    @Test
    @DisplayName("Rotación RR (10, 20, 30)")
    void rotationRR() {
        AvlTree<Integer, String> t = build(10, 20, 30);
        assertEquals(List.of(RotationType.RR), t.getRotationLog());
        assertEquals(List.of(20, 10, 30), t.preOrder());
    }

    @Test
    @DisplayName("Rotación LR (30, 10, 20)")
    void rotationLR() {
        AvlTree<Integer, String> t = build(30, 10, 20);
        assertEquals(List.of(RotationType.LR), t.getRotationLog());
        assertEquals(List.of(20, 10, 30), t.preOrder());
    }

    @Test
    @DisplayName("Rotación RL (10, 30, 20)")
    void rotationRL() {
        AvlTree<Integer, String> t = build(10, 30, 20);
        assertEquals(List.of(RotationType.RL), t.getRotationLog());
        assertEquals(List.of(20, 10, 30), t.preOrder());
    }

    @Test
    @DisplayName("Factores de balance tras rotar son 0")
    void balanceFactors() {
        AvlTree<Integer, String> t = build(30, 20, 10);
        assertEquals(0, t.balanceFactor(20));
        assertEquals(0, t.balanceFactor(10));
        assertThrows(IllegalArgumentException.class, () -> t.balanceFactor(99));
    }

    @Test
    @DisplayName("Inserción ordenada 1..100 mantiene altura logarítmica y árbol balanceado")
    void sequentialInsert() {
        AvlTree<Integer, String> t = new AvlTree<>();
        for (int i = 1; i <= 100; i++) t.insert(i, "v");
        assertTrue(t.isBalanced());
        assertTrue(t.height() <= 8);
        assertEquals(100, t.size());
    }

    @Test
    @DisplayName("La eliminación dispara rotaciones y conserva el invariante")
    void deleteRebalances() {
        AvlTree<Integer, String> t = build(20, 10, 30, 5);
        t.clearRotationLog();
        assertTrue(t.delete(30));
        assertEquals(List.of(RotationType.LL), t.getRotationLog());
        assertTrue(t.isBalanced());
        assertEquals(List.of(5, 10, 20), t.inOrder());
    }

    @Test
    @DisplayName("Eliminar con dos hijos, inexistente y vaciar el árbol")
    void deleteCases() {
        AvlTree<Integer, String> t = build(50, 30, 70, 20, 40, 60, 80);
        assertTrue(t.delete(50));
        assertTrue(t.isBalanced());
        assertEquals(List.of(20, 30, 40, 60, 70, 80), t.inOrder());
        assertFalse(t.delete(1234));
        for (int k : List.of(20, 30, 40, 60, 70, 80)) assertTrue(t.delete(k));
        assertTrue(t.isEmpty());
        assertEquals(0, t.height());
    }

    @Test
    @DisplayName("Prueba aleatoria: inserciones y eliminaciones mantienen el invariante y el orden")
    void randomized() {
        Random rnd = new Random(42);
        AvlTree<Integer, String> t = new AvlTree<>();
        List<Integer> keys = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            int k = rnd.nextInt(10_000);
            if (!keys.contains(k)) keys.add(k);
            t.insert(k, "v");
            assertTrue(t.isBalanced());
        }
        Collections.shuffle(keys, rnd);
        for (int i = 0; i < keys.size() / 2; i++) {
            assertTrue(t.delete(keys.get(i)));
            assertTrue(t.isBalanced());
        }
        List<Integer> remaining = new ArrayList<>(keys.subList(keys.size() / 2, keys.size()));
        Collections.sort(remaining);
        assertEquals(remaining, t.inOrder());
        assertEquals(remaining.size(), t.size());
    }

    @Test
    @DisplayName("Búsqueda cuenta pasos; recorridos y claves nulas")
    void searchAndNull() {
        AvlTree<Integer, String> t = build(10, 20, 30, 40, 50, 25);
        assertTrue(t.search(25).found());
        assertTrue(t.search(25).steps() <= t.height());
        assertFalse(t.search(99).found());
        assertEquals(List.of(10, 20, 25, 30, 40, 50), t.inOrder());
        assertEquals(t.size(), t.postOrder().size());
        assertThrows(NullPointerException.class, () -> t.insert(null, "x"));
    }
}
