
package org.aopalliance.intercept;

import java.lang.reflect.Method;

/**
 * Description of an invocation to a method, given to an interceptor
 * upon method-call.
 * 方法调用实体，对方法调用的描述，在调用方法时给予拦截器。
 *
 * <p>A method invocation is a joinpoint and can be intercepted by a
 * method interceptor.
 * 方法调用是一个连接点，可以被方法拦截器拦截。
 *
 * @author Rod Johnson
 * @see MethodInterceptor
 */
public interface MethodInvocation extends Invocation {

	/**
	 * Get the method being called.
	 * 获取被调用的方法对象。
	 * <p>This method is a frienly implementation of the
	 * {@link Joinpoint#getStaticPart()} method (same result).
	 * @return the method being called
	 */
	Method getMethod();

}
