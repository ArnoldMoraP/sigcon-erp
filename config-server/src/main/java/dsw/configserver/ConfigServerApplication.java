package dsw.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Config Server (Spring Cloud Config) en modo NATIVE: sirve la configuracion
 * centralizada desde una carpeta local (config-repo) en vez de un repo Git.
 * Cada microservicio, al arrancar, le pide aqui su application.yml.
 */
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
