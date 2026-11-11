package co.edu.redsegura.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Árbol binario de búsqueda (sin balanceo) implementado desde cero.
 * <p>Política de duplicados: insertar una clave existente reemplaza su valor y no
 * cambia el tamaño. Las claves nulas se rechazan.</p>
 * <p>Convención de altura: árbol vacío = 0; un solo nodo = 1.</p>
 * <p>Complejidad: insert/search/delete O(log n) promedio y O(n) en el peor caso
 * (inserción ordenada); recorridos O(n); espacio O(n).</p>
 */
public final class BinarySearchTree<K extends Comparable<K>, V> {

    private static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node<K, V> root;
    private int size;

    /** Inserta (o reemplaza el valor de) una clave. Iterativo para no desbordar la pila en el peor caso. */
    public void insert(K key, V value) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        if (root == null) {
            root = new Node<>(key, value);
            size++;
            return;
        }
        Node<K, V> current = root;
        while (true) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) {
                current.value = value;
                return;
            }
            if (cmp < 0) {
                if (current.left == null) {
                    current.left = new Node<>(key, value);
                    size++;
                    return;
                }
                current = current.left;
            } else {
                if (current.right == null) {
                    current.right = new Node<>(key, value);
                    size++;
                    return;
                }
                current = current.right;
            }
        }
    }

    /** Busca una clave contando los nodos visitados. */
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

    /**
     * Elimina una clave. Con dos hijos se reemplaza por el sucesor in-order (mínimo del subárbol derecho).
     * @return true si la clave existía
     */
    public boolean delete(K key) {
        Objects.requireNonNull(key, "La clave no puede ser nula");
        int before = size;
        root = delete(root, key);
        return size < before;
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
        return node;
    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node.left == null) return node.right;
        node.left = removeMin(node.left);
        return node;
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

    /** Altura en niveles: vacío = 0, un nodo = 1. */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> n) {
        return n == null ? 0 : 1 + Math.max(height(n.left), height(n.right));
    }

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    /** Dibujo lateral: la raíz está a la izquierda y el hijo derecho se muestra arriba. */
    public String render() {
        StringBuilder sb = new StringBuilder();
        render(root, 0, sb);
        return sb.toString();
    }

    private void render(Node<K, V> n, int depth, StringBuilder sb) {
        if (n == null) return;
        render(n.right, depth + 1, sb);
        sb.append("    ".repeat(depth)).append(n.key).append('\n');
        render(n.left, depth + 1, sb);
    }
}
