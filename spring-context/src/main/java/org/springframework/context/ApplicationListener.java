
package org.springframework.context;

import java.util.EventListener;

/**
 * Interface to be implemented by application event listeners.
 * Based on the standard {@code java.util.EventListener} interface
 * for the Observer design pattern.
 * 应用事件监听器，由应用事件监听器列表实现的接口。
 * 基于观察者设计模式的标准事件监听器EventListener接口。
 *
 * <p>As of Spring 3.0, an ApplicationListener can generically declare the event type
 * that it is interested in. When registered with a Spring ApplicationContext, events
 * will be filtered accordingly, with the listener getting invoked for matching event
 * objects only.
 * 从Spring 3.0开始，应用事件监听器一般可以声明它感兴趣的事件类型。
 * 当使用Spring应用上下文注册时，将相应地过滤事件，只调用匹配的事件对象的监听器。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @param <E> the specific ApplicationEvent subclass to listen to
 * @see org.springframework.context.event.ApplicationEventMulticaster
 */
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {

	/**
	 * Handle an application event.
	 * 处理应用事件。
	 * @param event the event to respond to
	 */
	void onApplicationEvent(E event);

}
