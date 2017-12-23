## What is spring retry?

Spring retry provides the facility to invoke a failed operation repeatedly until it get success or exhausted. There are typical scenarios where a web service or RMI service may fail due to some network glitch or some other issue that is related to that point of time only. In such cases the accepted resolution could be to re-attempt the operation. Spring retry is a smaller module and is not so widely known as like other modules but it serves better those retry scenarios.

This github project demonstrating sample features of spring retry but before continuing further I will encourage you to go through below original github project to get better understanding on what spring retry is really for.

https://github.com/spring-projects/spring-retry

Spring retry can be configured using XML configuration, annotation way or simply calling spring retry's API classes.


## How to run sample code?

This github project contains spring boot's `ApplicationRunner` classes to run individual use-cases so please enable / disable configuration in `application.properties` to invoke specific use-cases. Each of the code contains comment of what the class is doing. There are some of the features that I am depicting below to get the use cases better.


## Retry Policies

Retry policy is responsible for taking decision whether to retry the failed operation or exhaust. On exhaust retry policy uses 
`RetryContext` object to save the exhaust state so that retry will be terminated.

<dl>
  <dt>RetryPolicy methods</dt>
  <dd> boolean canRetry(RetryContext context) </dd>
  <dd> RetryContext open(RetryContext parent) </dd>
  <dd> void close(RetryContext context) </dd>
  <dd> void registerThrowable(RetryContext context, Throwable throwable) </dd>
</dl>


`RetryContext` can also be used as transporter object to carry data to subsequent calls.

```
  retryTemplate.execute(new RetryCallback<Void, Exception>() {
      @Override
      public Void doWithRetry(RetryContext context) throws Exception {
          // Passing value to all retry calls
          List<LocalDateTime> list = (List<LocalDateTime>) context.getAttribute(mylist);
          list.add(LocalDateTime.now());
          context.setAttribute(mylist, list);

          service.runMyTask();
          return null;
      }
   }
```

#### Various retry policies

* `SimpleRetryPolicy`: For re-attempting failed operation fixed number of time, defaults to three times.
* `TimeoutRetryPolicy`: For retrying until specified time is elapsed
* `ExceptionClassifierRetryPolicy`: Individual retry policy for each exception type.

  ```
  RetryTemplate template = new RetryTemplate();

  TimeoutRetryPolicy timeoutPolicy = new TimeoutRetryPolicy();
  timeoutPolicy.setTimeout(5000);

  Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();
  policyMap.put(TimeoutException.class, timeoutPolicy);
  policyMap.put(RuntimeException.class, new SimpleRetryPolicy(4));

  ExceptionClassifierRetryPolicy policy = new ExceptionClassifierRetryPolicy();
  policy.setPolicyMap(policyMap);
  ```

* `CompositeRetryPolicy`: Contains group of retry policies where all of them will take decission of re-attempt.
If any of the retry policy denies to retry then retry will be terminated.
* `CircuitBreakerRetryPolicy`: A special type of retry policy where re-attemp will be suspended for some moment while circuit is open
and then circuit will be reset to enable retry again.

  ```
  @Component
  public class CircuitBreakerTask {
	  private int counter;
	  private boolean previousCircuitStatus;

	  @CircuitBreaker(maxAttempts = 5, openTimeout = 3000l, resetTimeout = 6000l)
	  public void runTask() throws Exception {
		  boolean circuitOpened = getCircuitStatus();
		  if (!circuitOpened && previousCircuitStatus != circuitOpened) {
			  previousCircuitStatus = circuitOpened;
			  System.out.println("{WOW} Circuit is closed, task be called until closed.");
		  }

		  Thread.sleep(500L);

		  if (counter++ < 25) {
			  throw new RuntimeException();
		  }
	  }

	  @Recover
	  private void recover(RuntimeException e) {
		  boolean circuitOpened = getCircuitStatus();
		  if (circuitOpened && previousCircuitStatus != circuitOpened) {
			  previousCircuitStatus = circuitOpened;
			  System.err.println("{Caution}: Circuit is opened, so task is suspended for some moment.");
		  }
	  }

	  private boolean getCircuitStatus() {
		  RetryContext ctx = RetrySynchronizationManager.getContext();
		  return (boolean) ctx.getAttribute(CircuitBreakerRetryPolicy.CIRCUIT_OPEN);
	  }
  }
  
  public static void main(String[] args){
    for (int i = 0; i < 25; i++) {
      circuitTask.runTask();
      if (i % 5 == 0) {
        Thread.sleep(3000);
      }
    }
  }
  ```

###### Output

CircuitBreakerTask :: runTask() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>
CircuitBreakerTask :: runTask() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>
{Caution}: Circuit is opened, so runTask() won't be called for some moment<br/>
CircuitBreakerTask :: recover() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>
{Bravooo}: Circuit is closed, runTask() will be called until closed<br/>
CircuitBreakerTask :: runTask() invoked<br/>
CircuitBreakerTask :: recover() invoked<br/>

Here you will see circuit will be opened if all the attempts fails before `openTimeout` period and there after the control will 
not go to the `runTask()` till `resetTimeout` period is elapsed and circuit is closed again. `@Recover` method will be called on 
each failure no matter circuit is open or closed. The best use case of CircuitBreaker could be the scenario where if a web service's 
state is confirmed as DOWN after few attempts then all the operations related to this web service should be suspended temporarily 
assuming web service might be up after some time and called will be resumed.


## Backoff Policies
Backoff policies defines the strategies to be adopted between two successive retries. These backoff policies could be as simple as 
to wait for given period of time before next retry or perform some other useful operation.

#### Various retry policies
* `FixedBackOffPolicy`: Implementation of `BackOffPolicy` that pauses for a fixed period of time.
* `ExponentialBackOffPolicy`: It increases the back off period for each retry attempt exponentially.
* `UniformRandomBackOffPolicy`: Implementation of `BackOffPolicy` that pauses for a random period of time before continuing.
We can also configure maximum and minimum sleep periods.


