
package org.springframework.core.env;

/**
 * Interface indicating a component that contains and exposes an {@link Environment} reference.
 * 应用环境能力，包含和暴露应用运行时环境对象{@link Environment}引用的组件接口。
 *
 * <p>All Spring application contexts are EnvironmentCapable, and the interface is used primarily
 * for performing {@code instanceof} checks in framework methods that accept BeanFactory
 * instances that may or may not actually be ApplicationContext instances in order to interact
 * with the environment if indeed it is available.
 * 所有Spring应用上下文都是EnvironmentCapable，本接口主要用于在接受BeanFactory实例的框架方法中执行类型检查，
 * 这些实例可能是也可能不是ApplicationContext实例，以便在环境可用时与环境进行交互。
 *
 * <p>As mentioned, {@link org.springframework.context.ApplicationContext ApplicationContext}
 * extends EnvironmentCapable, and thus exposes a {@link #getEnvironment()} method; however,
 * {@link org.springframework.context.ConfigurableApplicationContext ConfigurableApplicationContext}
 * redefines {@link org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * getEnvironment()} and narrows the signature to return a {@link ConfigurableEnvironment}.
 * The effect is that an Environment object is 'read-only' until it is being accessed from
 * a ConfigurableApplicationContext, at which point it too may be configured.
 * 如前所述，应用上下文扩展了环境能力，从而暴露了一个{@link #getEnvironment()}方法；
 * 但是，可配置的应用上下文重新定义了getEnvironment()方法，并缩小了方法签名以返回可配置的环境{@link ConfigurableEnvironment}。
 * 结果是，在从可配置的应用上下文访问环境对象之前，它是"只读"的，此时它也可以配置。
 *
 * @author Chris Beams
 * @since 3.1
 * @see Environment
 * @see ConfigurableEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment()
 */
public interface EnvironmentCapable {

	/**
	 * Return the {@link Environment} associated with this component.
	 * 返回与这个组件关联的环境对象{@link Environment}。
	 */
	Environment getEnvironment();

}
