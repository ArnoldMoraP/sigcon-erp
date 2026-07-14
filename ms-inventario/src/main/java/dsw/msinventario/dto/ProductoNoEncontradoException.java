package dsw.msinventario.dto;

/** Se traduce a HTTP 400 con body { "error": "..." } (ver GlobalExceptionHandler). */
public class ProductoNoEncontradoException extends RuntimeException {
    public ProductoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
