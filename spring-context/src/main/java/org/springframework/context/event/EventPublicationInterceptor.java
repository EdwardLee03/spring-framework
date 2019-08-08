
package org.springframework.context.event;

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

/**
 * {@link MethodInterceptor Interceptor} that publishes an
 * {@code ApplicationEvent} to all {@code ApplicationListeners}
 * registered with an {@code ApplicationEventPublisher} after each
 * <i>successful</i> method invocation.
 * 应用事件发布拦截器，发布一个应用事件到所有的应用事件监听者列表的方法拦截器。
 *
 * <p>Note that this interceptor is only capable of publishing <i>stateless</i>
 * events configured via the
 * {@link #setApplicationEventClass "applicationEventClass"} property.
 *
 * @author Dmitriy Kopylenko
 * @author Juergen Hoeller
 * @author Rick Evans
 * @see #setApplicationEventClass
 * @see org.springframework.context.ApplicationEvent
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.ApplicationEventPublisher
 * @see org.springframework.context.ApplicationContext
 */
public class EventPublicationInterceptor
		implements MethodInterceptor, ApplicationEventPublisherAware, InitializingBean {

    /** 应用事件类型的构造器 */
	private Constructor<?> applicationEventClassConstructor;

    /** 应用事件发布者 */
	private ApplicationEventPublisher applicationEventPublisher;


	/**
	 * Set the application event class to publish.
     * 设置要发布的应用事件类型。
	 * <p>The event class <b>must</b> have a constructor with a single
	 * {@code Object} argument for the event source. The interceptor
	 * will pass in the invoked object.
	 * @throws IllegalArgumentException if the supplied {@code Class} is
	 * {@code null} or if it is not an {@code ApplicationEvent} subclass or
	 * if it does not expose a constructor that takes a single {@code Object} argument
	 */
	public void setApplicationEventClass(Class<?> applicationEventClass) {
		if (ApplicationEvent.class == applicationEventClass ||
			!ApplicationEvent.class.isAssignableFrom(applicationEventClass)) {
		    // 非应用事件子类
			throw new IllegalArgumentException("applicationEventClass needs to extend ApplicationEvent");
		}
		try {
			this.applicationEventClassConstructor =
					applicationEventClass.getConstructor(Object.class);
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalArgumentException("applicationEventClass [" +
					applicationEventClass.getName() + "] does not have the required Object constructor: " + ex);
		}
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        // 必要条件检查
		if (this.applicationEventClassConstructor == null) {
			throw new IllegalArgumentException("applicationEventClass is required");
		}
	}

    /**
     * 在调用方法之后发布应用事件。
     */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
	    // 处理链中的下一个拦截器
		Object retVal = invocation.proceed();

		ApplicationEvent event = (ApplicationEvent)
				this.applicationEventClassConstructor.newInstance(invocation.getThis());
        // 通知所有匹配的注册了这个应用事件的监听者列表
		this.applicationEventPublisher.publishEvent(event);

		return retVal;
	}

}
