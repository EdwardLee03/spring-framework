
package org.springframework.beans.factory;

/**
 * Counterpart of {@link BeanNameAware}. Returns the bean name of an object.
 * 返回对象的bean名称。
 *
 * <p>This interface can be introduced to avoid a brittle dependence on
 * bean name in objects used with Spring IoC and Spring AOP.
 * 可以引入此接口以避免在与Spring IoC和Spring AOP一起使用的对象中对bean名称的脆弱依赖。
 *
 * @author Rod Johnson
 * @since 2.0
 * @see BeanNameAware
 */
public interface NamedBean {

	/**
	 * Return the name of this bean in a Spring bean factory, if known.
	 * 如果已知，则在Spring bean工厂中返回此bean的名称。
	 */
	String getBeanName();

}
