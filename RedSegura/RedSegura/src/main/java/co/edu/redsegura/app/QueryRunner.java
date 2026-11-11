package co.edu.redsegura.app;

import co.edu.redsegura.core.RedSeguraCore;
import co.edu.redsegura.core.RotationDemo;
import co.edu.redsegura.core.SearchComparison;
import co.edu.redsegura.graph.BreadthFirstSearch;
import co.edu.redsegura.graph.DepthFirstSearch;
import co.edu.redsegura.graph.DijkstraSolver;
import co.edu.redsegura.graph.PathResult;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.RedSeguraException;
import co.edu.redsegura.model.Vertex;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementa las consultas obligatorias Q1 a Q7 (y un análisis extra) sobre {@link RedSeguraCore}.
 * Cada consulta recibe sus parámetros para poder usarse tanto en la demo como en el menú interactivo.
 * Los errores de dominio ({@link RedSeguraException}) se capturan donde la consulta lo requiere
 * para que el programa nunca termine por un dato inexistente.
 */
public final class QueryRunner {

    private final RedSeguraCore core;
    private final PrintStream out;

    public QueryRunner(RedSeguraCore core, PrintStream out) {
        this.core = core;
        this.out = out;
    }

    /** Ejecuta la demostración completa con los parámetros del enunciado. */
    public void runAll() {
        out.println("RedSegura - demostración (tiempos didácticos de simulación; no son datos oficiales");
        out.println("de tránsito ni de respuesta a emergencias)");
        q1("Hospital Universitario");
        q2();
        q3();
        q4("S02");
        q5("S02", "S08");
        q6("R16", "S02", "S08");
        q7("S99");
        extras();
    }

    // ------------------------------------------------------------------ Q1
    public void q1(String label) {
        header("Q1 - Buscar \"" + label + "\" en el ABB y en el AVL");
        printComparison(core.compareSearch(label));
        printComparison(core.compareSearchSortedInsertion(label));
        out.println();
        out.println("Lectura: la forma del ABB depende del orden de llegada de las claves (su altura va de");
        out.println("~log n hasta n; con entrada ordenada degenera en una lista y buscar cuesta O(n)).");
        out.println("El AVL se reequilibra con rotaciones y conserva altura O(log n) sin importar el orden.");
    }

    private void printComparison(SearchComparison c) {
        out.println();
        out.println("Inserción en " + c.scenario() + " - " + c.size() + " claves");
        out.printf("  ABB: %-14s pasos = %2d   altura = %d%n", found(c.bst().found()), c.bst().steps(), c.bstHeight());
        out.printf("  AVL: %-14s pasos = %2d   altura = %d%n", found(c.avl().found()), c.avl().steps(), c.avlHeight());
        int diff = c.bst().steps() - c.avl().steps();
        if (diff > 0) {
            out.println("  -> El AVL necesitó " + diff + " paso(s) menos.");
        } else if (diff < 0) {
            out.println("  -> En este caso el ABB necesitó " + (-diff) + " paso(s) menos.");
        } else {
            out.println("  -> Ambos necesitaron la misma cantidad de pasos.");
        }
    }

    // ------------------------------------------------------------------ Q2
    public void q2() {
        header("Q2 - Las cuatro rotaciones del AVL");
        for (RotationDemo d : core.demonstrateRotations()) {
            out.println();
            out.println("Caso " + d.expected() + ": se insertan " + d.insertionOrder());
            out.println("  Rotaciones aplicadas: " + d.applied());
            out.println("  Preorden resultante : " + d.preOrderAfter() + "   (altura = " + d.heightAfter() + ")");
            out.println("  Árbol (raíz a la izquierda, derecho arriba):");
            for (String line : d.drawing().split("\n")) out.println("    " + line);
        }
        out.println();
        out.println("LL y RR son rotaciones simples; LR y RL son dobles (rotación del hijo y luego de la raíz).");
        out.println("En los cuatro casos queda la clave del medio como raíz, con altura 2.");
    }

    // ------------------------------------------------------------------ Q3
    public void q3() {
        header("Q3 - Representaciones del grafo");
        out.println("Lista de adyacencia (solo salidas válidas según orientación):");
        out.print(core.representations().formatAdjacencyList());
        out.println();
        out.println("Matriz de adyacencia ponderada:");
        out.print(core.representations().formatAdjacencyMatrix());
        out.println();
        out.println("Matriz de incidencia:");
        out.print(core.representations().formatIncidenceMatrix());
    }

    // ------------------------------------------------------------------ Q4
    public void q4(String start) {
        header("Q4 - BFS y DFS desde " + start + " (" + core.labelOf(start) + ")");
        BreadthFirstSearch.Result b = core.bfs(start);
        DepthFirstSearch.Result d = core.dfs(start);
        List<String> dIter = core.dfsIterative(start);

        out.println("BFS (cola, por niveles):");
        out.println("  Orden : " + b.order());
        out.println("  Niveles:");
        int maxLevel = b.level().values().stream().mapToInt(Integer::intValue).max().orElse(0);
        for (int lv = 0; lv <= maxLevel; lv++) {
            List<String> at = new ArrayList<>();
            for (String id : b.order()) if (b.level().get(id) == lv) at.add(id);
            out.println("    nivel " + lv + ": " + at);
        }
        out.println("  Árbol BFS (hijo <- padre): " + b.parent());

        out.println("DFS recursivo:");
        out.println("  Orden : " + d.order());
        out.println("  Árbol DFS (hijo <- padre): " + d.parent());
        out.println("DFS iterativo (pila): " + dIter + (dIter.equals(d.order()) ? "  (igual al recursivo)" : "  (difiere del recursivo)"));

        int diverge = -1;
        for (int i = 0; i < Math.min(b.order().size(), d.order().size()); i++) {
            if (!b.order().get(i).equals(d.order().get(i))) { diverge = i; break; }
        }
        out.println();
        if (diverge >= 0) {
            out.println("Los órdenes empiezan a diferir en la posición " + (diverge + 1) + ": BFS visita "
                    + b.order().get(diverge) + " y DFS visita " + d.order().get(diverge) + ".");
        } else {
            out.println("En este caso los dos órdenes coinciden.");
        }
        out.println("Por qué pueden diferir: BFS usa una cola FIFO y agota todos los vecinos de un nivel antes de");
        out.println("bajar; DFS se hunde por el primer vecino hasta quedarse sin salidas y entonces retrocede.");
        out.println("Además, el orden entre hermanos depende del orden en que se registraron las conexiones, y");
        out.println("los arcos dirigidos limitan qué vértices se alcanzan desde el origen.");
    }

    // ------------------------------------------------------------------ Q5
    public void q5(String from, String to) {
        header("Q5 - Dijkstra de " + from + " a " + to);
        PathResult r = core.shortestRoute(from, to);
        printPath(r);
        if (from.equals("S02") && to.equals("S08")) {
            boolean ok = r.reachable() && r.totalMinutes() == 24
                    && r.vertices().equals(List.of("S02", "S01", "S07", "S08"));
            out.println("Resultado de control (24 min por S02, S01, S07, S08): " + (ok ? "CUMPLE" : "NO CUMPLE"));
            out.println("Nota: S08 también se alcanza con 24 min por S02, S01, S09, S10, S08 (empate). Dijkstra");
            out.println("conserva la primera ruta encontrada porque solo relaja con distancia estrictamente menor.");
        }
        DijkstraSolver.Solution sol = core.distancesFrom(from);
        out.println();
        out.println("Distancias mínimas desde " + from + " y predecesores:");
        out.printf("  %-4s %-24s %10s  %s%n", "ID", "Lugar", "Minutos", "Predecesor (conexión)");
        for (Vertex v : core.places()) {
            Integer dist = sol.distances().get(v.getId());
            String pred = sol.predecessors().get(v.getId());
            String predText = pred == null ? "-" : pred + " (" + sol.viaEdges().get(v.getId()) + ")";
            out.printf("  %-4s %-24s %10s  %s%n", v.getId(), v.getLabel(),
                    dist == null ? "inalcanzable" : dist.toString(), predText);
        }
    }

    // ------------------------------------------------------------------ Q6
    public void q6(String edgeId, String from, String to) {
        header("Q6 - Bloquear " + edgeId + " y recalcular " + from + " -> " + to);
        PathResult before = core.shortestRoute(from, to);
        boolean wasBlocked = core.isRouteBlocked(edgeId);
        List<Edge> extraBlocked = new ArrayList<>();
        core.blockRoute(edgeId); // lanza RedSeguraException si la conexión no existe
        try {
            out.println("Antes del bloqueo:");
            printPath(before);
            PathResult after = core.shortestRoute(from, to);
            out.println("Después de bloquear " + edgeId + ":");
            printPath(after);

            out.println("Explicación:");
            if (!before.edgeIds().contains(edgeId)) {
                out.println("  La ruta original no usaba " + edgeId + ", por eso el resultado no cambia.");
            } else if (!after.reachable()) {
                out.println("  " + edgeId + " era la única vía posible: el destino queda inaccesible.");
            } else if (after.totalMinutes() == before.totalMinutes()) {
                out.println("  La ruta original usaba " + edgeId + ". Al bloquearla Dijkstra tomó otra ruta con el");
                out.println("  mismo costo (" + after.totalMinutes() + " min): había un empate y antes se había conservado la");
                out.println("  primera encontrada. Cambia el camino, no el tiempo.");
            } else {
                out.println("  La ruta original usaba " + edgeId + ". Sin ella el mejor camino alterno cuesta "
                        + after.totalMinutes() + " min en lugar de " + before.totalMinutes() + " (+"
                        + (after.totalMinutes() - before.totalMinutes()) + " min).");
            }

            List<Edge> inbound = core.inboundRoutes(to);
            if (!inbound.isEmpty()) {
                out.println();
                out.print("Escenario adicional: se bloquean también las demás entradas a " + to + ":");
                for (Edge e : inbound) {
                    out.print(" " + e.getId());
                    core.blockRoute(e.getId());
                    extraBlocked.add(e);
                }
                out.println();
                PathResult none = core.shortestRoute(from, to);
                printPath(none);
                out.println("  Grafo desconectado hacia el destino: Dijkstra informa que no hay ruta, sin fallar.");
            }
        } finally {
            for (Edge e : extraBlocked) core.unblockRoute(e.getId());
            if (!wasBlocked) core.unblockRoute(edgeId);
            out.println();
            out.println("Estado restaurado: las conexiones vuelven a estar disponibles.");
        }
    }

    // ------------------------------------------------------------------ Q7
    public void q7(String id) {
        header("Q7 - Consultar un vértice inexistente (" + id + ")");
        attempt("Consultar lugar " + id, () -> out.println("  Encontrado: " + core.getPlace(id)));
        attempt("BFS desde " + id, () -> out.println("  Orden: " + core.bfs(id).order()));
        attempt("Dijkstra de S02 a " + id, () -> printPath(core.shortestRoute("S02", id)));
        out.println("El programa continúa con normalidad.");
    }

    private void attempt(String what, Runnable action) {
        out.println(what + ":");
        try {
            action.run();
        } catch (RedSeguraException e) {
            out.println("  Error controlado: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------ Extras
    public void extras() {
        header("Extra - Estructura del grafo (DFS)");
        out.println("Lugares: " + core.placeCount() + "   Conexiones: " + core.routeCount());
        out.println("¿Tiene ciclos? " + (core.hasCycle() ? "sí" : "no"));
        List<List<String>> comps = core.weakComponents();
        out.println("Componentes débilmente conexas: " + comps.size());
        for (int i = 0; i < comps.size(); i++) out.println("  #" + (i + 1) + ": " + comps.get(i));
    }

    // ------------------------------------------------------------------ Utilidades
    private void printPath(PathResult r) {
        if (!r.reachable()) {
            out.println("  No existe ruta de " + r.source() + " a " + r.target() + ".");
            return;
        }
        Map<String, Edge> byId = new java.util.HashMap<>();
        for (Edge e : core.routes()) byId.put(e.getId(), e);
        StringBuilder sb = new StringBuilder("  ").append(r.vertices().get(0));
        for (int i = 0; i < r.edgeIds().size(); i++) {
            Edge e = byId.get(r.edgeIds().get(i));
            sb.append(" -[").append(e.getId()).append(", ").append(e.getWeight()).append("]-> ")
              .append(r.vertices().get(i + 1));
        }
        out.println(sb);
        StringBuilder names = new StringBuilder("  ");
        for (int i = 0; i < r.vertices().size(); i++) {
            if (i > 0) names.append(" > ");
            names.append(core.labelOf(r.vertices().get(i)));
        }
        out.println(names);
        out.println("  Total: " + r.totalMinutes() + " minutos");
    }

    private void header(String title) {
        out.println();
        out.println("=".repeat(78));
        out.println(title);
        out.println("=".repeat(78));
    }

    private static String found(boolean f) {
        return f ? "encontrado" : "no encontrado";
    }
}
