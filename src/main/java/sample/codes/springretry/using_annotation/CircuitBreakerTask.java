package sample.codes.springretry.using_annotation;

import java.time.LocalTime;

import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.policy.CircuitBreakerRetryPolicy;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

/**
 * CircuitBreaker is usually used to pause a service call for some time after few retries in-case of failure.
 * In such scenarios, circuit-breaker service will start processing requests after 
 * prescribed time (resetTimeout). Here are the rules.
 * 
 * 1. Whether circuit is open/closed, @Recover method will always be called except method gets success.
 * 
 * 2. If maxAttempts is completed before openTimeout is elapsed then CircuitBreaker will open the circuit
 * 	  so that non of the calls there after will come to runTask() till circuit closes.
 * 
 * 3. resetTimeout will reset the circuit (maxAttemptCounter=0, openTimeout=current time) means 
 * 	  it will close the circuit so that runTask will again start processing.
 *    
 * 4. In this example we will see, after 5 attempts call will directly go to recover method 
 *    until circuit closes at resetTimeout.
 */
@Component
public class CircuitBreakerTask {

	private int counter;
	private boolean previousCircuitStatus;

	@CircuitBreaker(maxAttempts = 5, openTimeout = 3000l, resetTimeout = 6000l)
	public void runTask() throws Exception {
		boolean circuitOpened = getCircuitStatus();
		if (!circuitOpened && previousCircuitStatus != circuitOpened) {
			previousCircuitStatus = circuitOpened;
			System.out.printf(
					"===> (counter=%d, Time=%s) [Bravooo]: Circuit is closed, runTask() will be called until closed.\n",
					counter, LocalTime.now());
		}

		System.out.printf("(counter=%d, Time=%s) :: CircuitBreakerTask :: runTask() invoked.\n", counter,
				LocalTime.now());
		Thread.sleep(500L);

		if (counter++ < 25) {
			throw new RuntimeException();
		}
		System.out.printf("(counter=%d, Time=%s) :: CircuitBreakerTask :: runTask() completed.\n", counter,
				LocalTime.now());
	}

	@Recover
	private void recover(RuntimeException e) {
		System.out.printf("(counter=%d, Time=%s) :: CircuitBreakerTask :: recover() invoked.\n", counter,
				LocalTime.now());

		boolean circuitOpened = getCircuitStatus();
		if (circuitOpened && previousCircuitStatus != circuitOpened) {
			previousCircuitStatus = circuitOpened;
			System.err.printf(
					"===> (counter=%d, Time=%s) [Caution]: Circuit is opened, so runTask() won't be called for some moment.\n",
					counter, LocalTime.now());
		}
	}

	private boolean getCircuitStatus() {
		RetryContext ctx = RetrySynchronizationManager.getContext();
		return (boolean) ctx.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN);
	}

}
