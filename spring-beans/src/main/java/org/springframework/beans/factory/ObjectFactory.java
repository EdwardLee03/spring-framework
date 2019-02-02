
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * Defines a factory which can return an Object instance
 * (possibly shared or independent) when invoked.
 * 定义一个可以在调用时返回Object实例（可能是共享或独立）的工厂。
 *
 * <p>This interface is typically used to encapsulate a generic factory which
 * returns a new instance (prototype) of some target object on each invocation.
 * 此接口通常用于封装通用工厂，该工厂在每次调用时返回某个目标对象的新实例（原型）。
 *
 * <p>This interface is similar to {@link FactoryBean}, but implementations
 * of the latter are normally meant to be defined as SPI instances in a
 * {@link BeanFactory}, while implementations of this class are normally meant
 * to be fed as an API to other beans (through injection). As such, the
 * {@code getObject()} method has different exception handling behavior.
 *
 * @author Colin Sampaleanu
 * @since 1.0.2
 * @see FactoryBean
 */
public interface ObjectFactory<T> {

	/**
	 * Return an instance (possibly shared or independent)
	 * of the object managed by this factory.
	 * 返回此工厂管理的对象的实例（可能是共享的或独立的）。
	 * @return an instance of the bean (should never be {@code null})
	 * @throws BeansException in case of creation errors
	 */
	T getObject() throws BeansException;

}
