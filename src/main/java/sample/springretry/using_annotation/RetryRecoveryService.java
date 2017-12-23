package sample.springretry.using_annotation;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * This shows the example of containing a retry-able method (retryMethod) that
 * will be called configurable number of time in-case fails otherwise recovery
 * method will be called after exhausted means all attempts failed.
 */
@Service
public class RetryRecoveryService {

	private int cnt = 1;

	/**
	 * Retry-able method that will be repeatedly attempted until get success or
	 * exhausted.
	 */
	@Retryable(value = { RuntimeException.class })
	public void retryMethod(int x) throws Exception {
		System.out.println("SampleService invoked.");

		Thread.sleep(500);

		if (cnt++ < 4) {
			System.err.println("SampleService raised RuntimeException");
			throw new RuntimeException("SampleService throwing exception");
		}
	}

	/**
	 * After all attempts are failed, control will come to recovery method. The
	 * first argument will be the exception object and rest will be original
	 * method argument. This recovery method can be used to call fall-back
	 * strategy such as switching to some other service or just logging.
	 */
	@Recover
	public void recover(RuntimeException ex, int x) {
		System.out.printf("SampleService :: recover() : ex=%s\n", ex.getClass().getSimpleName());
	}

}
