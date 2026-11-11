package co.edu.redsegura.core;

import co.edu.redsegura.tree.RotationType;

import java.util.List;

/**
 * Evidencia de una rotación AVL: claves insertadas, rotaciones aplicadas y estado final del árbol.
 */
public record RotationDemo(RotationType expected, List<Integer> insertionOrder,
                           List<RotationType> applied, List<Integer> preOrderAfter,
                           int heightAfter, String drawing) { }
