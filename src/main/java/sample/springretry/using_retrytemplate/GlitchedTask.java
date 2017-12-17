package sample.springretry.using_retrytemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * This task will fail first time and will get success next time. This is just
 * an example of a service that is exposed to some network glitch which fails to
 * get response once in a while. In such situation it's caller's duty to retry
 * the service if needed.
 */
@Component
public class GlitchedTask {

	@Value("${task.retry.maxattemps}")
	private int maxCount;

	private int cnt = 1;

	public void runMyTask() {
		System.out.println("Start of GlitchedTask.");

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (cnt <= maxCount) {
			cnt++;
			//System.err.println("GlitchedTask raised RuntimeException");
			throw new RuntimeException("Throwing exception for retry test.");
		} else {
			System.out.println("GlitchedTask successfully completed.");
		}
	}
}
