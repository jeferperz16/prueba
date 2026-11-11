package co.edu.redsegura.tree;

/**
 * Resultado de una búsqueda en un árbol.
 *
 * @param found  si la clave existe
 * @param value  valor asociado (null si no se encontró)
 * @param steps  cantidad de nodos visitados/comparados durante la búsqueda
 */
public record SearchResult<V>(boolean found, V value, int steps) { }
