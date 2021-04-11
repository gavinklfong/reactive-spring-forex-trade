package space.gavinklfong.forex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication
public class ForexApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForexApplication.class, args);
	}

//    @Bean
//    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
//
//        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
//        initializer.setConnectionFactory(connectionFactory);
//        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
//
//        return initializer;
//    }
	
}
