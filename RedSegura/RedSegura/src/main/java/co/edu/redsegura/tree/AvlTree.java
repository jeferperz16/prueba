package co.edu.redsegura.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Árbol AVL implementado desde cero. Mantiene |factor de balance| &lt;= 1 en todos los nodos
 * mediante rotaciones LL, RR, LR y RL tras insertar y eliminar.
 * <p>Factor de balance = altura(izquierdo) - altura(derecho). Altura: vacío = 0, hoja = 1.</p>
 * <p>Política de duplicados: reemplaza el valor. Claves nulas rechazadas.</p>
 * <p>Complejidad: insert/search/delete O(log n) en el peor caso; recorridos O(n); espacio O(n).</p>
 * <p>Cada rebalanceo se registra en {@link #getRotationLog()} (una entrada por caso, incluidos
 * LR y RL, que internamente son dos rotaciones simples).</p>
 */
public final class AvlTree<K extends Comparable<K>, V> {

    private static final class Node<K, V> {
        K key;
        V value;
        int height = 1;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;
    private final List<RotationType> rotationLog = new ArrayList<>();

    // ---------- Operaciones públicas ----------

    public void insert(K key, V value) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        root = insert(root, key, value);
    }

    public SearchResult<V> search(K key) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        int steps = 0;
        Node<K, V> current = root;
        while (current != null) {
            steps++;
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                return new SearchResult<>(true, current.value, steps);
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return new SearchResult<>(false, null, steps);
    }

    public boolean contains(K key) {
        return search(key).found();
    }

    /** @return true si la clave existía y fue eliminada */
    public boolean delete(K key) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        int before = size;
        root = delete(root, key);
        return size < before;
    }

    public List<K> inOrder() {
        List<K> out = new ArrayList<>();
        inOrder(root, out);
        return out;
    }

    public List<K> preOrder() {
        List<K> out = new ArrayList<>();
        preOrder(root, out);
        return out;
    }

    public List<K> postOrder() {
        List<K> out = new ArrayList<>();
        postOrder(root, out);
        return out;
    }

    public int height() { return height(root); }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    /**
     * Factor de balance del nodo con la clave dada.
     * @throws IllegalArgumentException si la clave no existe
     */
    public int balanceFactor(K key) {
        Node<K, V> n = root;
        while (n != null) {
            int cmp = key.compareTo(n.key);
            if (cmp == 0) return balance(n);
            n = cmp < 0 ? n.left : n.right;
        }
        throw new IllegalArgumentException("La clave no existe en el árbol: " + key);
    }

    /** Verifica el invariante AVL (alturas correctas y |balance| &lt;= 1 en todos los nodos). */
    public boolean isBalanced() {
        return checkBalanced(root);
    }

    /** Rotaciones registradas desde la última limpieza, en orden cronológico. */
    public List<RotationType> getRotationLog() {
        return Collections.unmodifiableList(rotationLog);
    }

    public void clearRotationLog() {
        rotationLog.clear();
    }

    /** Dibujo lateral: la raíz está a la izquierda y el hijo derecho se muestra arriba. */
    public String render() {
        StringBuilder sb = new StringBuilder();
        render(root, 0, sb);
        return sb.toString();
    }

    // ---------- Implementación interna ----------

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.value = value;
            return node;
        }
        return rebalance(node);
    }

    private Node<K, V> delete(Node<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            size--;
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node<K, V> successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.key = successor.key;
            node.value = successor.value;
            node.right = removeMin(node.right);
        }
        return rebalance(node);
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null) return node.right;
        node.left = removeMin(node.left);
        return rebalance(node);
    }

    /** Actualiza la altura del nodo y aplica la rotación que corresponda. */
    private Node<K, V> rebalance(Node<K, V> node) {
        updateHeight(node);
        int bf = balance(node);
        if (bf > 1) {
            if (balance(node.left) >= 0) {
                rotationLog.add(RotationType.LL);
                return rotateRight(node);
            }
            rotationLog.add(RotationType.LR);
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (bf < -1) {
            if (balance(node.right) <= 0) {
                rotationLog.add(RotationType.RR);
                return rotateLeft(node);
            }
            rotationLog.add(RotationType.RL);
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        y.left = x.right;
        x.right = y;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        x.right = y.left;
        y.left = x;
        updateHeight(x);
        updateHeight(y);
        return y;
    }

    private int height(Node<K, V> n) {
        return n == null ? 0 : n.height;
    }

    private void updateHeight(Node<K, V> n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int balance(Node<K, V> n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private boolean checkBalanced(Node<K, V> n) {
        if (n == null) return true;
        int expected = 1 + Math.max(height(n.left), height(n.right));
        if (n.height != expected || Math.abs(balance(n)) > 1) return false;
        return checkBalanced(n.left) && checkBalanced(n.right);
    }

    private void inOrder(Node<K, V> n, List<K> out) {
        if (n == null) return;
        inOrder(n.left, out);
        out.add(n.key);
        inOrder(n.right, out);
    }

    private void preOrder(Node<K, V> n, List<K> out) {
        if (n == null) return;
        out.add(n.key);
        preOrder(n.left, out);
        preOrder(n.right, out);
    }

    private void postOrder(Node<K, V> n, List<K> out) {
        if (n == null) return;
        postOrder(n.left, out);
        postOrder(n.right, out);
        out.add(n.key);
    }

    private void render(Node<K, V> n, int depth, StringBuilder sb) {
        if (n == null) return;
        render(n.right, depth + 1, sb);
        sb.append("    ".repeat(depth)).append(n.key).append('\n');
        render(n.left, depth + 1, sb);
    }
}
