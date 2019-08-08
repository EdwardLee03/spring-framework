
package org.aopalliance.intercept;

/**
 * This interface represents an invocation in the program.
 * 调用实体，表示程序中的调用。
 *
 * <p>An invocation is a joinpoint and can be intercepted by an
 * interceptor.
 * 调用是一个连接点，可以被拦截器拦截。
 *
 * @author Rod Johnson
 */
public interface Invocation extends Joinpoint {

	/**
	 * Get the arguments as an array object.
	 * It is possible to change element values within this
	 * array to change the arguments.
	 * 获取方法执行时的参数值列表。
	 * @return the argument of the invocation
	 */
	Object[] getArguments();

}
