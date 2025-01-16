package finance_us.finance_us;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FinanceUsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceUsApplication.class, args);
	}

}
