package space.gavinklfong.forex.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import io.r2dbc.h2.H2ConnectionConfiguration;
import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
class R2DBCConfig extends AbstractR2dbcConfiguration {
    @Bean
    public H2ConnectionFactory connectionFactory() {
        return new H2ConnectionFactory(
            H2ConnectionConfiguration.builder()
              .url("mem:testdb;DB_CLOSE_DELAY=-1;")
              .username("sa")
              .build()
        );
    }
    
    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
    	
    	ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
    	initializer.setConnectionFactory(connectionFactory);
    	
    	CompositeDatabasePopulator populator = new CompositeDatabasePopulator();
    	populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
    	populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource("data.sql")));
    	
    	initializer.setDatabasePopulator(populator);
    	
    	return initializer;
    }
}
