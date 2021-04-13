package space.gavinklfong.forex.config;

import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.opencsv.exceptions.CsvValidationException;

import space.gavinklfong.forex.services.ForexPriceService;


@Configuration
public class AppConfig {

	@Bean
	public ForexPriceService initializeForexRateSpreadRepo() throws CsvValidationException, IOException {
		Resource resource = new ClassPathResource("/rate-price.csv");
		return new ForexPriceService(resource.getInputStream());
	}


}
