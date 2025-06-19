package adivina_la_cancion.prototipo.adivina_la_cancion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.SpringVersion;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableScheduling
public class AdivinaLaCancionApplication {

	public static void main(String[] args) {
		System.out.println(SpringVersion.getVersion());
		SpringApplication.run(AdivinaLaCancionApplication.class, args);
	}

	/**
	 * Define un bean de tipo RestTemplate para realizar peticiones HTTP.
	 * Esto permite inyectar y reutilizar RestTemplate en toda la aplicación.
	 * 
	 * @return una nueva instancia de RestTemplate gestionada por Spring.
	 */
	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
