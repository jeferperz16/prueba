package co.edu.redsegura.core;

import co.edu.redsegura.model.Vertex;
import co.edu.redsegura.tree.SearchResult;

/**
 * Comparación de una misma búsqueda por etiqueta en un ABB y en un AVL.
 *
 * @param scenario  descripción del orden de inserción usado
 * @param label     etiqueta buscada
 * @param bst       resultado en el ABB (con pasos)
 * @param avl       resultado en el AVL (con pasos)
 * @param bstHeight altura del ABB
 * @param avlHeight altura del AVL
 * @param size      cantidad de claves en cada árbol
 */
public record SearchComparison(String scenario, String label, SearchResult<Vertex> bst,
                               SearchResult<Vertex> avl, int bstHeight, int avlHeight, int size) { }
