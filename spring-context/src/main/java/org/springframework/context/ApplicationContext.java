
package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;

/**
 * Central interface to provide configuration for an application.
 * This is read-only while the application is running, but may be
 * reloaded if the implementation supports this.
 * 应用上下文，为应用程序提供配置的核心接口。
 * 当应用程序在运行时，它是只读对象，但它可以被重新加载。
 *
 * <p>An ApplicationContext provides:
 * <p>应用上下文提供如下功能：
 * <ul>
 * <li>Bean factory methods for accessing application components.
 * 继承列表的Bean工厂{@link ListableBeanFactory}，用于访问应用程序组件的Bean工厂方法。
 * Inherited from {@link org.springframework.beans.factory.ListableBeanFactory}.
 * <li>The ability to load file resources in a generic fashion.
 * 继承资源加载器，以通用方式加载文件资源的能力。
 * Inherited from the {@link org.springframework.core.io.ResourceLoader} interface.
 * <li>The ability to publish events to registered listeners.
 * 继承应用事件发布者{@link ApplicationEventPublisher}，发布事件到注册的侦听器的能力。
 * Inherited from the {@link ApplicationEventPublisher} interface.
 * <li>The ability to resolve messages, supporting internationalization.
 * 继承信息源{@link MessageSource}，解析信息、支持国际化的能力。
 * Inherited from the {@link MessageSource} interface.
 * <li>Inheritance from a parent context. Definitions in a descendant context
 * will always take priority. This means, for example, that a single parent
 * context can be used by an entire web application, while each servlet has
 * its own child context that is independent of that of any other servlet.
 * 从父亲应用上下文继承，后/子代应用上下文中的Bean定义总是优先的。
 * </ul>
 *
 * <p>In addition to standard {@link org.springframework.beans.factory.BeanFactory}
 * lifecycle capabilities, ApplicationContext implementations detect and invoke
 * {@link ApplicationContextAware} beans as well as {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} and {@link MessageSourceAware} beans.
 * Bean工厂的组件生命周期管理能力，应用上下文实现检测和调用应用上下文通知{@link ApplicationContextAware}的组件列表，
 * 资源加载器通知{@link ResourceLoaderAware}，应用事件发布者通知{@link ApplicationEventPublisherAware}的组件列表。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * Return the unique id of this application context.
	 * 返回这个应用上下文的唯一标识。
	 * @return the unique id of the context, or {@code null} if none
	 */
	String getId();

	/**
	 * Return a name for the deployed application that this context belongs to.
	 * 返回这个应用上下文所属的部署应用的名称。
	 * @return a name for the deployed application, or the empty String by default
	 */
	String getApplicationName();

	/**
	 * Return a friendly name for this context.
	 * 返回这个应用上下文的友好展示名称。
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * Return the timestamp when this context was first loaded.
	 * 返回这个应用上下文首次被加载的时间戳。
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * and this is the root of the context hierarchy.
	 * 返回父亲应用上下文，null表示没有父亲应用上下文，同时它是上下文结构树的根。
	 * (父子应用上下文继承)
	 * @return the parent context, or {@code null} if there is no parent
	 */
	ApplicationContext getParent();

	/**
	 * Expose AutowireCapableBeanFactory functionality for this context.
	 * 为这个应用上下文暴露具有自动装配能力的Bean工厂的功能。
	 * <p>This is not typically used by application code, except for the purpose of
	 * initializing bean instances that live outside of the application context,
	 * applying the Spring bean lifecycle (fully or partly) to them.
     *
	 * <p>Alternatively, the internal BeanFactory exposed by the
	 * {@link ConfigurableApplicationContext} interface offers access to the
	 * {@link AutowireCapableBeanFactory} interface too. The present method mainly
	 * serves as a convenient, specific facility on the ApplicationContext interface.
	 * <p>
	 * 通过{@link ConfigurableApplicationContext}接口暴露的内部Bean工厂也提供了
	 * 访问{@link AutowireCapableBeanFactory}接口的能力。
	 * 当前方法主要作为{@link ApplicationContext}接口上的一个方便的特殊工具。
	 *
	 * <p><b>NOTE: As of 4.2, this method will consistently throw IllegalStateException
	 * after the application context has been closed.</b> In current Spring Framework
	 * versions, only refreshable application contexts behave that way; as of 4.2,
	 * all application context implementations will be required to comply.
	 * @return the AutowireCapableBeanFactory for this context
	 * @throws IllegalStateException if the context does not support the
	 * {@link AutowireCapableBeanFactory} interface, or does not hold an
	 * autowire-capable bean factory yet (e.g. if {@code refresh()} has
	 * never been called), or if the context has been closed already
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
