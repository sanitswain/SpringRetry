package sample.springretry.using_annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SampleInterceptor implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		System.out.println("Sample interceptor.");
		Object val = null;
		try {
			val = mi.proceed();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return val;
	}

}
