package sample.codes.springretry.using_annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

/**
 * @author Sanit
 *
 *         This task will fail first time and will get success next time. This
 *         is just an example of a service that is exposed to some network
 *         glitch which fails to get response once in a while. In such situation
 *         it's caller's duty to retry the service if needed.
 */
@Component
public class AnnotatedGlitchedTask {

	@Value("${task.retry.maxattemps}")
	private int maxCount;

	private int cnt = 1;

	@Retryable(maxAttemptsExpression = "#{${task.retry.maxattemps}}", include = { RuntimeException.class,
			Exception.class }, backoff = @Backoff(value = 0))
	public void runMyTask() {
		System.out.println("Start of AnnotatedGlitchedTask.");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (cnt < maxCount) {
			cnt++;
			System.err.println("AnnotatedGlitchedTask raised RuntimeException");
			throw new RuntimeException("Throwing exception for retry test.");
		} else {
			System.out.println("AnnotatedGlitchedTask successfully completed.");
		}
	}
}
