package cl.duocuc.darmijo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@SpringBootApplication
public class Exp1Application {

	public static void main(String[] args) {
		SpringApplication.run(Exp1Application.class, args);
	}
	
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("")
					.allowedOrigins("http://localhost:4200", "localhost:4200")
					.allowedMethods("GET", "POST", "PUT", "DELETE");
			}
		};
	}
	
}
