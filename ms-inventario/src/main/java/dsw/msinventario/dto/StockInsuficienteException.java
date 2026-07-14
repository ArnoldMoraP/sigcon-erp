package dsw.msinventario.dto;

/** Se traduce a HTTP 400 con body { "error": "..." } (ver GlobalExceptionHandler). */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
