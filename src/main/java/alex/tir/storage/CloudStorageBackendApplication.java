package alex.tir.storage;

import alex.tir.storage.config.FilesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties(FilesProperties.class)
@EnableTransactionManagement(order = 1000)
@EnableJpaRepositories(enableDefaultTransactions = false)
public class CloudStorageBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudStorageBackendApplication.class, args);
	}

}
