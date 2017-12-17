package sample.codes.springretry.using_annotation;

import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RetryRecoveryService {

	private int cnt = 1;

	@Retryable(RuntimeException.class)
	public void retryMethod() throws Exception {
		System.out.println("SampleService invoked.");

		Thread.sleep(500);

		if (cnt++ < 4) {
			System.err.println("SampleService raised RuntimeException");
			throw new RuntimeException("SampleService throwing exception");
		}
	}

	@Recover
	public void recover(RuntimeException ex) {
		System.out.printf("SampleService :: recover() : ex=%s\n", ex.getClass().getSimpleName());
	}

}
