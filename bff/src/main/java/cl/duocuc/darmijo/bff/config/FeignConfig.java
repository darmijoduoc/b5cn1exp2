package cl.duocuc.darmijo.bff.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    Logger.Level feignLoggerLevel() {
        // Esto mostrará cabeceras, body y metadatos de las llamadas BFF -> Backend
        return Logger.Level.FULL;
    }
}
