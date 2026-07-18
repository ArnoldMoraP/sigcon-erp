package dsw.msventas.client;

import dsw.msventas.dto.DescontarStockRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioClient {

    private final RestTemplate restTemplate;

    private static final String URL = "http://ms-inventario/inventario/descontar-stock";
    // FASE 4: endpoint de compensacion (deshacer descuento de stock).
    private static final String URL_REPONER = "http://ms-inventario/inventario/reponer-stock";

    /**
     * Devuelve el body de respuesta de ms-inventario si el descuento fue exitoso.
     * Lanza RuntimeException con mensaje legible si hay stock insuficiente,
     * producto no encontrado, o ms-inventario no está disponible.
     */
    public Map<String, Object> descontarStock(String producto, Integer cantidad) {
        try {
            HttpEntity<DescontarStockRequest> request =
                    new HttpEntity<>(new DescontarStockRequest(producto, cantidad));

            ResponseEntity<Map> response = restTemplate.exchange(
                    URL, HttpMethod.PATCH, request, Map.class);

            return response.getBody();

        } catch (HttpClientErrorException.BadRequest e) {
            // ms-inventario respondió 400 (ej: stock insuficiente, producto no encontrado)
            log.warn("ms-inventario rechazó el descuento de stock: {}", e.getResponseBodyAsString());
            throw new RuntimeException(e.getResponseBodyAsString());

        } catch (ResourceAccessException e) {
            log.error("ms-inventario no disponible: {}", e.getMessage());
            throw new RuntimeException("ms-inventario no está disponible en este momento");
        }
    }

    /**
     * FASE 4 (compensacion Saga). Repone el stock que se habia descontado,
     * cuando el pedido no llego a guardarse. No propaga la excepcion hacia
     * arriba: si la compensacion falla, se LOGUEA como inconsistencia critica
     * (para revisar/reintentar manualmente) pero no tapa el error original del
     * pedido. Devuelve true si repuso, false si no pudo.
     */
    public boolean reponerStock(String producto, Integer cantidad) {
        try {
            HttpEntity<DescontarStockRequest> request =
                    new HttpEntity<>(new DescontarStockRequest(producto, cantidad));

            restTemplate.exchange(URL_REPONER, HttpMethod.PATCH, request, Map.class);

            log.info("Compensacion OK: se repusieron {} de '{}'", cantidad, producto);
            return true;

        } catch (Exception e) {
            // Ojo: si esto falla, el stock quedo descontado sin pedido -> inconsistencia.
            log.error("INCONSISTENCIA: fallo la compensacion de stock para '{}' (cantidad {}). "
                    + "Revisar/reponer manualmente. Causa: {}", producto, cantidad, e.getMessage());
            return false;
        }
    }
}