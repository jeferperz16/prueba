package co.edu.redsegura.app;

/**
 * Texto del análisis de complejidad (Big O) que se incluye en el reporte.
 * n = número de claves; V = vértices; E = conexiones; h = altura del árbol; d = grado de un vértice.
 */
public final class ComplexityAnalysis {

    private ComplexityAnalysis() { }

    public static String text() {
        return """
                ANÁLISIS DE COMPLEJIDAD (n = claves, V = vértices, E = conexiones, h = altura)
                --------------------------------------------------------------------------------
                Componente / operación                    Tiempo                 Espacio extra
                ABB insert / search / delete              O(h): O(log n) prom.,   O(1) iterativo
                                                          O(n) peor caso          (delete: O(h) pila)
                ABB recorridos (in/pre/post)              O(n)                    O(h) pila + O(n) lista
                AVL insert / search / delete              O(log n) peor caso      O(log n) pila
                AVL rotaciones LL/RR/LR/RL                O(1) cada una           O(1)
                AVL recorridos                            O(n)                    O(log n) pila + O(n)
                Grafo addVertex / addEdge                 O(1) amortizado         O(1)
                Grafo neighbors(v)                        O(d(v))                 O(d(v))
                Lista de adyacencia                       O(V + E)                O(V + E)
                Matriz de adyacencia                      O(V² + E)               O(V²)
                Matriz de incidencia                      O(V·E)                  O(V·E)
                BFS (cola + visitados)                    O(V + E)                O(V)
                DFS recursivo / iterativo                 O(V + E)                O(V) (pila)
                Detección de ciclos                       O(V + E)                O(V)
                Componentes débiles                       O(V + E)                O(V)
                Dijkstra con PriorityQueue                O((V + E) log V)        O(V + E)
                Reconstrucción del camino                 O(V)                    O(V)

                Notas:
                - El ABB se degrada a O(n) con inserciones ordenadas; el AVL garantiza altura
                  <= 1.44 log2(n + 2), por eso mantiene O(log n) siempre (ver Q1).
                - Con V = 12 y E = 18 las diferencias son pequeñas; el análisis aplica al crecer el mapa.
                - Dijkstra usa eliminación perezosa de entradas obsoletas, por lo que la cola puede
                  tener hasta O(E) entradas; log E = O(log V) porque E <= V².
                """;
    }
}
