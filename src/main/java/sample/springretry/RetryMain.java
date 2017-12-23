package sample.springretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

@Configuration
@EnableRetry
@SpringBootApplication
public class RetryMain {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RetryMain.class);
	}

	/*@Bean
	public RetryListener retryListener() {
		return new SampleRetryListener("AnnotatedGlitchedTask");
	}*/

}
