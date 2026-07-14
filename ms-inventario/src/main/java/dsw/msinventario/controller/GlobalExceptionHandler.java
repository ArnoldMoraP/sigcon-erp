package dsw.msinventario.controller;

import dsw.msinventario.dto.ProductoNoEncontradoException;
import dsw.msinventario.dto.StockInsuficienteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduce las excepciones a la forma { "error": "..." } con HTTP 400,
 * que es exactamente lo que InventarioClient (ms-ventas) espera leer
 * cuando captura HttpClientErrorException.BadRequest.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({StockInsuficienteException.class, ProductoNoEncontradoException.class})
    public ResponseEntity<Map<String, String>> negocio(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> ilegal(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> generico(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage() == null ? "Error inesperado" : e.getMessage()));
    }
}
