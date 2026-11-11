package co.edu.redsegura.graph;

import java.util.List;

/**
 * Resultado de una consulta de ruta mínima.
 *
 * @param source       vértice origen
 * @param target       vértice destino
 * @param reachable    si existe camino de origen a destino
 * @param totalMinutes costo total en minutos (-1 si no es alcanzable)
 * @param vertices     vértices del camino de origen a destino (vacío si no es alcanzable)
 * @param edgeIds      IDs de las conexiones usadas, en orden
 */
public record PathResult(String source, String target, boolean reachable, int totalMinutes,
                         List<String> vertices, List<String> edgeIds) { }
