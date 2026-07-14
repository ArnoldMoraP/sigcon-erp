package dsw.msrrhh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MsRrhhApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsRrhhApplication.class, args);
    }
}
