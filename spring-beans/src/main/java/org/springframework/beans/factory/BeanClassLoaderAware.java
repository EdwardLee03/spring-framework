/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

/**
 * Callback that allows a bean to be aware of the bean
 * {@link ClassLoader class loader}; that is, the class loader used by the
 * present bean factory to load bean classes.
 * 允许bean知道bean类加载器的回调；
 * 当前bean工厂用来加载bean类的类加载器。
 *
 * <p>This is mainly intended to be implemented by framework classes which
 * have to pick up application classes by name despite themselves potentially
 * being loaded from a shared class loader.
 * 这主要是由框架类实现的，框架类必须按名称获取应用程序类，
 * 尽管它们可能是从共享类加载器加载的。
 *
 * <p>For a list of all bean lifecycle methods, see the
 * {@link BeanFactory BeanFactory javadocs}.
 * 有关所有bean生命周期方法的列表，请参阅{@link BeanFactory BeanFactory文档}。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 2.0
 * @see BeanNameAware
 * @see BeanFactoryAware
 * @see InitializingBean
 */
public interface BeanClassLoaderAware extends Aware {

	/**
	 * Callback that supplies the bean {@link ClassLoader class loader} to
	 * a bean instance.
	 * 将bean类加载器提供给bean实例的回调。
	 * <p>Invoked <i>after</i> the population of normal bean properties but
	 * <i>before</i> an initialization callback such as
	 * {@link InitializingBean InitializingBean's}
	 * {@link InitializingBean#afterPropertiesSet()}
	 * method or a custom init-method.
	 * 在普通bean属性填充之后，但在初始化回调方法或自定义init方法之前被调用。
	 * @param classLoader the owning class loader; may be {@code null} in
	 * which case a default {@code ClassLoader} must be used, for example
	 * the {@code ClassLoader} obtained via
	 * {@link org.springframework.util.ClassUtils#getDefaultClassLoader()}
	 */
	void setBeanClassLoader(ClassLoader classLoader);

}
