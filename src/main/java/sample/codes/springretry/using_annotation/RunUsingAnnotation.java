package sample.codes.springretry.using_annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RunUsingAnnotation implements ApplicationRunner {

	/**
	 * Condition to run this example
	 */
	@Value("${test.annotation.basic_retry_exp}")
	private boolean testBasicExp;

	@Value("${test.annotation.retry_recovery}")
	private boolean testRetryRecovery;

	@Value("${test.annotation.circuitbreaker}")
	private boolean testCircuitBreaker;

	@Autowired
	private AnnotatedGlitchedTask basicTask;

	@Autowired
	private RetryRecoveryService retryRecoveryTask;

	@Autowired
	private CircuitBreakerTask circuitTask;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			if (testBasicExp) {
				basicTask.runMyTask();
			}

			if (testRetryRecovery) {
				retryRecoveryTask.retryMethod();
			}

			if (testCircuitBreaker) {
				for (int i = 0; i < 25; i++) {
					circuitTask.runTask();
					if (i % 5 == 0) {
						Thread.sleep(3000);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
