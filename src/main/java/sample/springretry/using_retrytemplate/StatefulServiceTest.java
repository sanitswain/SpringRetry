package sample.springretry.using_retrytemplate;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.retry.RetryListener;
import org.springframework.retry.RetryState;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MapRetryContextCache;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

/**
 * This is not completed....
 *
 */
@Component
public class StatefulServiceTest implements ApplicationRunner {

	@Autowired
	private GlitchedTask task;

	@Value("${test.using_template.stateful_retry}")
	private boolean canRun;

	@Value("${task.retry.maxattemps}")
	private int maxAttempts;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (canRun) {
			try {
				RetryTemplate template = statefulRetryTemplate();
				RetryState state = retryState();

				template.execute(context -> {
					task.runMyTask();
					return null;
				}, state);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public RetryTemplate statefulRetryTemplate() {
		FixedBackOffPolicy bop = new FixedBackOffPolicy();
		bop.setBackOffPeriod(2000);

		SimpleRetryPolicy srp = new SimpleRetryPolicy();
		srp.setMaxAttempts(this.maxAttempts);

		RetryTemplate rt = new RetryTemplate();
		rt.setBackOffPolicy(bop);
		rt.setRetryPolicy(srp);
		rt.setListeners(new RetryListener[] { new SampleRetryListener("GlitchedTask") });
		rt.setRetryContextCache(new MapRetryContextCache());
		return rt;
	}

	public RetryState retryState() {
		DefaultRetryState state = new DefaultRetryState(UUID.randomUUID(), false);
		return state;
	}

}
