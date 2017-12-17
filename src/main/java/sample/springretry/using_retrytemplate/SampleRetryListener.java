package sample.springretry.using_retrytemplate;

import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

/**
 * @author Sanit
 * 
 *         Listener to track retry operation.
 *
 */
public class SampleRetryListener extends RetryListenerSupport {

	private String listeningTask;

	public SampleRetryListener(String listeningTask) {
		this.listeningTask = listeningTask;
	}

	@Override
	public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		System.out.println("*****\t*****\t*****\t*****\t*****\t*****\t*****\n");
	}

	@Override
	public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
			Throwable throwable) {
		System.err.printf("%s service raised %s exception on attempt: %d.\n", listeningTask,
				throwable.getClass().getName(), context.getRetryCount());
	}

	@Override
	public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
		System.out.println("\n*****\t*****\t*****\t*****\t*****\t*****\t*****");
		System.out.printf("%s task will be retried configured number of times if fails.\n", listeningTask);
		return true;
	}

}
