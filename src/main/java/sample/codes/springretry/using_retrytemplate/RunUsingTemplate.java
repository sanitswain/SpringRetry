package sample.codes.springretry.using_retrytemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RunUsingTemplate implements ApplicationRunner {

	/**
	 * Condition to run this example
	 */
	@Value("${test.using_template.stateless_retry}")
	private boolean testStatelessRetry;

	@Value("${task.retry.maxattemps}")
	private int maxAttempts;

	@Autowired
	private GlitchedTask task;

	private RetryTemplate rTemplate;

	/*
	 * If the business logic does not succeed before the template decides to
	 * abort, then the client is given the chance to do some alternate
	 * processing through the recovery callback. Recovery task will be run only
	 * once after the last retry fails.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void run(ApplicationArguments args) throws Exception {
		final String mylist = "mylist";
		if (testStatelessRetry) {
			rTemplate = retryTemplate();

			rTemplate.execute(new RetryCallback<Void, Exception>() {

				@Override
				public Void doWithRetry(RetryContext context) throws Exception {

					// Passing value to all retry calls
					List<LocalDateTime> list = (List<LocalDateTime>) context.getAttribute(mylist);
					if (list == null) {
						list = new ArrayList<>();
					}

					list.add(LocalDateTime.now());
					context.setAttribute(mylist, list);

					task.runMyTask();
					return null;
				}

			}, new RecoveryCallback<Void>() {
				@Override
				public Void recover(RetryContext context) throws Exception {
					List<LocalDateTime> list = (List<LocalDateTime>) context.getAttribute(mylist);
					System.out.println("RunUsingTemplate :: recover() :: mylist=" + list);
					return null;
				}
			});
		}
	}

	public RetryTemplate retryTemplate() {
		FixedBackOffPolicy bop = new FixedBackOffPolicy();
		bop.setBackOffPeriod(2000);

		SimpleRetryPolicy srp = new SimpleRetryPolicy();
		srp.setMaxAttempts(this.maxAttempts);

		RetryTemplate rt = new RetryTemplate();
		rt.setBackOffPolicy(bop);
		rt.setRetryPolicy(srp);
		rt.setListeners(new RetryListener[] { new SampleRetryListener("GlitchedTask") });
		return rt;
	}

}
