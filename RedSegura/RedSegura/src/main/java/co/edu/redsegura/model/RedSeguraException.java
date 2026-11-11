package co.edu.redsegura.model;

/**
 * Excepción de dominio de RedSegura. Se lanza ante IDs duplicados o inexistentes,
 * pesos inválidos y operaciones que violan el contrato de una estructura.
 * Es no verificada para que la capa de aplicación decida cómo recuperarse sin
 * terminar el programa.
 */
public class RedSeguraException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public RedSeguraException(String message) {
        super(message);
    }
}
