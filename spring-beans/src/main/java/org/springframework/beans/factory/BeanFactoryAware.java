
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Interface to be implemented by beans that wish to be aware of their
 * owning {@link BeanFactory}.
 * 希望了解他们拥有的{@link BeanFactory}的beans实现的回调。
 *
 * <p>For example, beans can look up collaborating beans via the factory
 * (Dependency Lookup). Note that most beans will choose to receive references
 * to collaborating beans via corresponding bean properties or constructor
 * arguments (Dependency Injection).
 * 例如，beans可以通过Bean工厂查找协作的beans（依赖查找/Dependency Lookup）。
 * 注意：大多数beans将选择通过相应的bean属性或构造函数参数（依赖注入）接收协作的beans的引用。
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 * 有关所有bean生命周期方法的列表，请参阅{@link BeanFactory BeanFactory文档}。
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @since 11.03.2003
 * @see BeanNameAware
 * @see BeanClassLoaderAware
 * @see InitializingBean
 * @see org.springframework.context.ApplicationContextAware
 */
public interface BeanFactoryAware extends Aware {

	/**
	 * Callback that supplies the owning factory to a bean instance.
     * 将拥有Bean工厂提供给bean实例的回调。
	 * <p>Invoked after the population of normal bean properties
	 * but before an initialization callback such as
	 * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     * 在普通bean属性填充之后，但在初始化回调方法或自定义init方法之前被调用。
	 * @param beanFactory owning BeanFactory (never {@code null}).
	 * The bean can immediately call methods on the factory.
	 * @throws BeansException in case of initialization errors
	 * @see BeanInitializationException
	 */
	void setBeanFactory(BeanFactory beanFactory) throws BeansException;

}
