
package org.aopalliance.intercept;

import org.aopalliance.aop.Advice;

/**
 * This interface represents a generic interceptor.
 * 通用拦截器。
 *
 * <p>A generic interceptor can intercept runtime events that occur
 * within a base program. Those events are materialized by (reified
 * in) joinpoints. Runtime joinpoints can be invocations, field
 * access, exceptions...
 * 通用拦截器可以拦截运行时事件，这些事件由连接点实现。
 * 运行时连接点可以是方法调用，字段访问，异常等。
 *
 * <p>This interface is not used directly. Use the sub-interfaces
 * to intercept specific events. For instance, the following class
 * implements some specific interceptors in order to implement a
 * debugger:
 * 使用子接口拦截特定的事件。
 *
 * <pre class=code>
 * class DebuggingInterceptor implements MethodInterceptor, 
 *     ConstructorInterceptor, FieldInterceptor {
 *
 *   Object invoke(MethodInvocation i) throws Throwable {
 *     debug(i.getMethod(), i.getThis(), i.getArgs());
 *     return i.proceed();
 *   }
 *
 *   Object construct(ConstructorInvocation i) throws Throwable {
 *     debug(i.getConstructor(), i.getThis(), i.getArgs());
 *     return i.proceed();
 *   }
 * 
 *   Object get(FieldAccess fa) throws Throwable {
 *     debug(fa.getField(), fa.getThis(), null);
 *     return fa.proceed();
 *   }
 *
 *   Object set(FieldAccess fa) throws Throwable {
 *     debug(fa.getField(), fa.getThis(), fa.getValueToSet());
 *     return fa.proceed();
 *   }
 *
 *   void debug(AccessibleObject ao, Object this, Object value) {
 *     ...
 *   }
 * }
 * </pre>
 *
 * @author Rod Johnson
 * @see Joinpoint
 */
public interface Interceptor extends Advice {
    // 标记接口
}
