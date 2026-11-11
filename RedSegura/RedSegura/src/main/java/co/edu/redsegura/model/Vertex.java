package co.edu.redsegura.model;

/**
 * Lugar de la red (universidad, centro de salud, nodo de transporte, etc.).
 * Es inmutable: ID, etiqueta y tipo se fijan en la construcción.
 */
public final class Vertex {
    private final String id;
    private final String label;
    private final String type;

    /**
     * @param id    identificador único (p. ej. "S01"), no vacío
     * @param label etiqueta legible (p. ej. "UAJS Sede C"), no vacía
     * @param type  tipo de lugar (p. ej. "Universidad"), no vacío
     * @throws RedSeguraException si algún dato es nulo o vacío
     */
    public Vertex(String id, String label, String type) {
        this.id = requireText(id, "El ID del vértice");
        this.label = requireText(label, "La etiqueta del vértice");
        this.type = requireText(type, "El tipo del vértice");
    }

    private static String requireText(String value, String what) {
        if (value == null || value.isBlank()) {
            throw new RedSeguraException(what + " no puede ser nulo ni vacío.");
        }
        return value.trim();
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public String getType() { return type; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Vertex other && id.equals(other.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return id + " (" + label + ", " + type + ")"; }
}
