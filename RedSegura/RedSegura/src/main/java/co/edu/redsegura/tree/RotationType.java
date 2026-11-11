package co.edu.redsegura.tree;

/**
 * Tipo de rebalanceo aplicado por el AVL.
 * <ul>
 *   <li>LL: desbalance a la izquierda del hijo izquierdo; rotación simple a la derecha.</li>
 *   <li>RR: desbalance a la derecha del hijo derecho; rotación simple a la izquierda.</li>
 *   <li>LR: desbalance a la derecha del hijo izquierdo; rotación doble izquierda-derecha.</li>
 *   <li>RL: desbalance a la izquierda del hijo derecho; rotación doble derecha-izquierda.</li>
 * </ul>
 */
public enum RotationType { LL, RR, LR, RL }
