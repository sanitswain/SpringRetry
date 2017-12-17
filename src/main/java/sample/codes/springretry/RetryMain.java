package sample.codes.springretry;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;
import org.springframework.retry.annotation.EnableRetry;

import sample.codes.springretry.using_annotation.SampleInterceptor;
import sample.codes.springretry.using_retrytemplate.SampleRetryListener;

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

	@Bean
	public MethodInterceptor sampleInterceptor() {
		return new SampleInterceptor();
	}

}
