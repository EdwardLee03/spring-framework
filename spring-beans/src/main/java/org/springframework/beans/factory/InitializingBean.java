
package org.springframework.beans.factory;

/**
 * Interface to be implemented by beans that need to react once all their
 * properties have been set by a BeanFactory: for example, to perform custom
 * initialization, or merely to check that all mandatory properties have been set.
 * 一旦BeanFactory设置了所有属性，就需要做出反应的beans实现的回调。
 * 例如，执行自定义初始化，或仅检查是否已设置所有必需属性。
 *
 * <p>An alternative to implementing InitializingBean is specifying a custom
 * init-method, for example in an XML bean definition.
 * For a list of all bean lifecycle methods, see the BeanFactory javadocs.
 * 实现InitializingBean的替代方法是指定自定义init方法。
 * 有关所有bean生命周期方法的列表，请参阅{@link BeanFactory BeanFactory文档}。
 *
 * @author Rod Johnson
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see BeanFactory
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.context.ApplicationContextAware
 */
public interface InitializingBean {

	/**
	 * Invoked by a BeanFactory after it has set all bean properties supplied
	 * (and satisfied BeanFactoryAware and ApplicationContextAware).
	 * 在BeanFactory设置了所有提供的bean属性（并且满足BeanFactoryAware和ApplicationContextAware）之后，
	 * 由BeanFactory调用。
	 * <p>This method allows the bean instance to perform initialization only
	 * possible when all bean properties have been set and to throw an
	 * exception in the event of misconfiguration.
	 * @throws Exception in the event of misconfiguration (such
	 * as failure to set an essential property) or if initialization fails.
	 */
	void afterPropertiesSet() throws Exception;

}
