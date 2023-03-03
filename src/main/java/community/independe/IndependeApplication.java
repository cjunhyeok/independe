package community.independe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class IndependeApplication {

	public static void main(String[] args) {
		SpringApplication.run(IndependeApplication.class, args);
	}

}
