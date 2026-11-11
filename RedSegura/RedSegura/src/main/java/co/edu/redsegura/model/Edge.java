package co.edu.redsegura.model;

/**
 * Conexión etiquetada y ponderada entre dos vértices. El peso son minutos
 * estimados (dato didáctico de simulación). Si es dirigida, solo se puede
 * recorrer de origen a destino; si no, en ambos sentidos.
 * Es inmutable.
 */
public final class Edge {
    private final String id;
    private final String label;
    private final String sourceId;
    private final String targetId;
    private final int weight;
    private final boolean directed;

    /**
     * @param weight minutos, estrictamente positivo (Dijkstra exige pesos no negativos
     *               y un peso 0 se confundiría con "sin conexión" en la matriz de adyacencia)
     * @throws RedSeguraException si hay campos vacíos, peso &lt;= 0 o un lazo (origen = destino)
     */
    public Edge(String id, String label, String sourceId, String targetId, int weight, boolean directed) {
        this.id = requireText(id, "El ID de la conexión");
        this.label = requireText(label, "La etiqueta de la conexión");
        this.sourceId = requireText(sourceId, "El origen de la conexión");
        this.targetId = requireText(targetId, "El destino de la conexión");
        if (weight <= 0) {
            throw new RedSeguraException("Peso inválido en " + this.id + ": " + weight
                    + ". Debe ser un número de minutos mayor que 0.");
        }
        if (this.sourceId.equals(this.targetId)) {
            throw new RedSeguraException("La conexión " + this.id + " no puede unir un vértice consigo mismo.");
        }
        this.weight = weight;
        this.directed = directed;
    }

    private static String requireText(String value, String what) {
        if (value == null || value.isBlank()) {
            throw new RedSeguraException(what + " no puede ser nulo ni vacío.");
        }
        return value.trim();
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getSourceId() { return sourceId; }
    public String getTargetId() { return targetId; }
    public int getWeight() { return weight; }
    public boolean isDirected() { return directed; }

    /** Devuelve el extremo opuesto a {@code vertexId}. */
    public String otherEnd(String vertexId) {
        if (vertexId.equals(sourceId)) return targetId;
        if (vertexId.equals(targetId)) return sourceId;
        throw new RedSeguraException("El vértice " + vertexId + " no pertenece a la conexión " + id + ".");
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Edge other && id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        return id + " [" + sourceId + (directed ? " -> " : " -- ") + targetId + ", " + weight + " min, " + label + "]";
    }
}
