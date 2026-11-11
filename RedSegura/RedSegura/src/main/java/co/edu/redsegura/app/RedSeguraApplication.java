package co.edu.redsegura.app;

import co.edu.redsegura.core.RedSeguraCore;
import co.edu.redsegura.model.RedSeguraException;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Aplicación de consola RedSegura.
 * <pre>
 *   (sin argumentos)      menú interactivo
 *   --demo                ejecuta Q1..Q7 con los parámetros del enunciado
 *   --report [archivo]    genera el reporte (demo + análisis Big O); por defecto reporte_redsegura.txt
 * </pre>
 * Solo usa la fachada {@link RedSeguraCore}; no toca árboles ni grafos directamente.
 */
public final class RedSeguraApplication {

    private RedSeguraApplication() { }

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        RedSeguraCore core = RedSeguraCore.withDefaultDataset();

        if (args.length > 0 && args[0].equals("--demo")) {
            new QueryRunner(core, out).runAll();
        } else if (args.length > 0 && args[0].equals("--report")) {
            Path path = Path.of(args.length > 1 ? args[1] : "reporte_redsegura.txt");
            writeReport(core, path);
            out.println("Reporte generado en: " + path.toAbsolutePath());
        } else {
            interactive(core, out);
        }
    }

    // ------------------------------------------------------------------ Menú
    private static void interactive(RedSeguraCore core, PrintStream out) {
        QueryRunner q = new QueryRunner(core, out);
        Scanner in = new Scanner(System.in, StandardCharsets.UTF_8);
        out.println("RedSegura - movilidad en Sincelejo (datos didácticos de simulación, no oficiales)");
        while (true) {
            printMenu(out);
            String option = read(in, out, "Opción", null);
            if (option == null) break; // fin de entrada
            try {
                switch (option.trim()) {
                    case "1" -> q.q1(read(in, out, "Lugar a buscar", "Hospital Universitario"));
                    case "2" -> q.q2();
                    case "3" -> q.q3();
                    case "4" -> q.q4(readId(in, out, "Vértice de inicio", "S02"));
                    case "5" -> q.q5(readId(in, out, "Origen", "S02"), readId(in, out, "Destino", "S08"));
                    case "6" -> q.q6(readId(in, out, "Conexión a bloquear", "R16"),
                            readId(in, out, "Origen", "S02"), readId(in, out, "Destino", "S08"));
                    case "7" -> q.q7(readId(in, out, "ID de vértice a consultar", "S99"));
                    case "8" -> q.extras();
                    case "9" -> {
                        Path path = Path.of("reporte_redsegura.txt");
                        writeReport(core, path);
                        out.println("Reporte generado en: " + path.toAbsolutePath());
                    }
                    case "10" -> q.runAll();
                    case "0" -> {
                        out.println("Hasta luego.");
                        return;
                    }
                    default -> out.println("Opción no válida. Elija un número del menú.");
                }
            } catch (RedSeguraException e) {
                out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                out.println("No se pudo escribir el reporte: " + e.getMessage());
            }
        }
    }

    private static void printMenu(PrintStream out) {
        out.println();
        out.println("--------------------- MENÚ ---------------------");
        out.println(" 1. Q1 Buscar lugar en ABB y AVL (comparar pasos)");
        out.println(" 2. Q2 Rotaciones AVL (LL, RR, LR, RL)");
        out.println(" 3. Q3 Lista, matriz de adyacencia y de incidencia");
        out.println(" 4. Q4 BFS y DFS");
        out.println(" 5. Q5 Ruta mínima (Dijkstra)");
        out.println(" 6. Q6 Bloquear una conexión y recalcular");
        out.println(" 7. Q7 Consultar un vértice (probar inexistente)");
        out.println(" 8. Ciclos y componentes (DFS)");
        out.println(" 9. Generar reporte (reporte_redsegura.txt)");
        out.println("10. Demostración completa");
        out.println(" 0. Salir");
    }

    /** Lee una línea; si está vacía devuelve el valor por defecto. Devuelve null si no hay más entrada. */
    private static String read(Scanner in, PrintStream out, String prompt, String def) {
        out.print(prompt + (def != null ? " [" + def + "]" : "") + ": ");
        out.flush();
        if (!in.hasNextLine()) return null;
        String line = in.nextLine().trim();
        return line.isEmpty() && def != null ? def : line;
    }

    private static String readId(Scanner in, PrintStream out, String prompt, String def) {
        String v = read(in, out, prompt, def);
        return v == null ? def : v.toUpperCase();
    }

    // ------------------------------------------------------------------ Reporte
    static void writeReport(RedSeguraCore core, Path path) throws IOException {
        try (PrintStream ps = new PrintStream(Files.newOutputStream(path), true, StandardCharsets.UTF_8)) {
            ps.println("REPORTE REDSEGURA");
            ps.println("Proyecto: movilidad entre universidades, salud, transporte y puntos de encuentro de Sincelejo");
            ps.println("Aviso: los tiempos son datos didácticos de simulación; no son información oficial de");
            ps.println("tránsito ni de respuesta a emergencias.");
            ps.println();
            new QueryRunner(core, ps).runAll();
            ps.println();
            ps.println(ComplexityAnalysis.text());
        }
    }
}
