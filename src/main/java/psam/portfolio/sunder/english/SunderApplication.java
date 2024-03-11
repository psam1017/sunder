package psam.portfolio.sunder.english;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SunderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunderApplication.class, args);
	}

}
