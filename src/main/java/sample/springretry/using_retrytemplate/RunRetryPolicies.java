package sample.springretry.using_retrytemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.NoBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.policy.TimeoutRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class RunRetryPolicies implements ApplicationRunner {

	@Value("${test.using_template.timeout_retry}")
	private boolean testTimeoutRetry;

	@Value("${test.using_template.exception_classifier_retry}")
	private boolean testExceptionClassifierRetry;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		try {
			if (testTimeoutRetry) {
				RetryTemplate template = timeoutTemplate();
				template.execute(context -> {
					runTimeoutTask();
					return null;
				});
			}

			if (testExceptionClassifierRetry) {
				RetryTemplate template = exceptionClassifierTemplate();
				template.execute(context -> {
					runTimeoutTask();
					return null;
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runTimeoutTask() throws Exception {
		System.out.println("Task invoked.");
		Thread.sleep(1000);
		if (Math.random() < 0.8) {
			throw new RuntimeException();
		}
		System.out.println("Task completed.");
	}

	private RetryTemplate timeoutTemplate() {
		RetryTemplate template = new RetryTemplate();
		TimeoutRetryPolicy rp = new TimeoutRetryPolicy();
		rp.setTimeout(5000);

		template.setBackOffPolicy(new NoBackOffPolicy());
		return template;
	}

	private RetryTemplate exceptionClassifierTemplate() {
		RetryTemplate template = new RetryTemplate();

		TimeoutRetryPolicy timeoutPolicy = new TimeoutRetryPolicy();
		timeoutPolicy.setTimeout(5000);

		Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();
		policyMap.put(TimeoutException.class, timeoutPolicy);
		policyMap.put(RuntimeException.class, new SimpleRetryPolicy(4));

		ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
		policy.setPolicyMap(policyMap);

		template.setBackOffPolicy(new NoBackOffPolicy());
		return template;
	}

}
