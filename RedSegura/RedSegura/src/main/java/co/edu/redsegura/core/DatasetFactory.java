package co.edu.redsegura.core;

import co.edu.redsegura.graph.MixedGraph;
import co.edu.redsegura.model.Edge;
import co.edu.redsegura.model.Vertex;

/**
 * Fábrica del conjunto de datos suministrado: 12 lugares de Sincelejo y 18 conexiones.
 * Los minutos son datos didácticos de simulación, no información oficial de tránsito
 * ni de respuesta a emergencias.
 */
public final class DatasetFactory {

    private DatasetFactory() { }

    public static MixedGraph createSincelejoGraph() {
        MixedGraph g = new MixedGraph();

        g.addVertex(new Vertex("S01", "UAJS Sede C", "Universidad"));
        g.addVertex(new Vertex("S02", "UAJS Sede E", "Universidad"));
        g.addVertex(new Vertex("S03", "Universidad de Sucre", "Universidad"));
        g.addVertex(new Vertex("S04", "CECAR", "Universidad"));
        g.addVertex(new Vertex("S05", "Hospital Universitario", "Salud"));
        g.addVertex(new Vertex("S06", "Clínica Las Américas", "Salud"));
        g.addVertex(new Vertex("S07", "Estación Central SETP", "Transporte"));
        g.addVertex(new Vertex("S08", "Terminal de Transporte", "Transporte"));
        g.addVertex(new Vertex("S09", "Parque Santander", "Punto de encuentro"));
        g.addVertex(new Vertex("S10", "Mercado de Sincelejo", "Comercio"));
        g.addVertex(new Vertex("S11", "PEP Argelia", "Paradero"));
        g.addVertex(new Vertex("S12", "Glorieta Sincelejito", "Movilidad"));

        //                 id     etiqueta                      origen destino min dirigida
        g.addEdge(new Edge("R01", "Corredor universitario",     "S01", "S07",  4, true));
        g.addEdge(new Edge("R02", "Retorno por vía alterna",    "S07", "S01",  6, true));
        g.addEdge(new Edge("R03", "Corredor peatonal",          "S01", "S09",  5, false));
        g.addEdge(new Edge("R04", "Conexión entre sedes",       "S02", "S01",  8, false));
        g.addEdge(new Edge("R05", "Ruta de atención",           "S02", "S06", 10, true));
        g.addEdge(new Edge("R06", "Corredor clínico",           "S06", "S05",  4, true));
        g.addEdge(new Edge("R07", "Vía hospitalaria",           "S05", "S09",  6, false));
        g.addEdge(new Edge("R08", "Centro histórico",           "S09", "S10",  3, false));
        g.addEdge(new Edge("R09", "Salida al terminal",         "S10", "S08",  8, true));
        g.addEdge(new Edge("R10", "Ingreso desde terminal",     "S08", "S10",  9, true));
        g.addEdge(new Edge("R11", "Corredor Puerta Roja",       "S03", "S12",  4, false));
        g.addEdge(new Edge("R12", "Troncal al centro",          "S12", "S09",  7, false));
        g.addEdge(new Edge("R13", "Conexión universitaria",     "S04", "S11",  6, false));
        g.addEdge(new Edge("R14", "Ruta SETP al centro",        "S11", "S07",  8, true));
        g.addEdge(new Edge("R15", "Transferencia central",      "S07", "S09",  3, false));
        g.addEdge(new Edge("R16", "Ruta SETP al terminal",      "S07", "S08", 12, true));
        g.addEdge(new Edge("R17", "Ruta hacia universidad",     "S09", "S03",  7, true));
        g.addEdge(new Edge("R18", "Ruta hacia CECAR",           "S12", "S04",  9, true));
        return g;
    }
}
