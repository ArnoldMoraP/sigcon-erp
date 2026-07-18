package dsw.msventas.config;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        // El RestTemplate por defecto usa HttpURLConnection, que NO soporta PATCH
        // ("Invalid HTTP method: PATCH"). Apache HttpClient5 si lo soporta.
        return new RestTemplate(
                new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault())
        );
    }
}