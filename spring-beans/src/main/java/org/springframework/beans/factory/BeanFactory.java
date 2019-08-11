
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;

/**
 * The root interface for accessing a Spring bean container.
 * This is the basic client view of a bean container;
 * further interfaces such as {@link ListableBeanFactory} and
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 * Bean工厂，用于访问Spring bean容器的根接口。
 * 这是bean容器的基本客户端视图(bean容器，管控bean对象)；
 * 其他接口，如{@link ListableBeanFactory}和{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}，
 * 可用于特定目的。
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * each uniquely identified by a String name. Depending on the bean definition,
 * the factory will return either an independent instance of a contained object
 * (the Prototype design pattern), or a single shared instance (a superior
 * alternative to the Singleton design pattern, in which the instance is a
 * singleton in the scope of the factory). Which type of instance will be returned
 * depends on the bean factory configuration: the API is the same. Since Spring
 * 2.0, further scopes are available depending on the concrete application
 * context (e.g. "request" and "session" scopes in a web environment).
 * 本接口由持有许多bean定义的对象实现，每个bean定义由字符串名称唯一标识。
 * 根据bean定义，bean工厂将返回一个包含对象的独立实例(原型设计模式)，或单个共享实例(单例设计模式的高级替代，其中实例是工厂范围内的单例)。
 * 将返回哪种类型的实例取决于bean工厂配置：API是相同的。
 * 从Spring 2.0开始，根据具体的应用上下文，可以使用更多的范围。
 *
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 * 这种方法的重点是BeanFactory是应用程序组件的中央注册表，并集中应用程序组件的配置(不再需要单个对象读取属性文件)。
 * (BeanFactory管控组件及其配置)
 * 有关此方法的优点讨论，请参见《Expert One-on-One J2EE Design and Development》的第4章和第11章。
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 * 注意：通常最好依靠依赖注入/Dependency Injection("推送"配置)来通过设置方法或构造函数来配置应用程序对象，
 * 而不是像BeanFactory查找一样使用任何形式的"拉取"配置。
 * Spring的依赖注入功能是使用这个BeanFactory接口及其子接口实现的。
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * source (such as an XML document), and use the {@code org.springframework.beans}
 * package to configure the beans. However, an implementation could simply return
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 * 通常，BeanFactory将加载存储在配置源中的bean定义，并使用beans包来配置beans。
 * 但是，BeanFactory实现可以直接在Java代码中简单地返回它创建的Java对象。
 * 如何存储bean定义没有限制：LDAP，RDBMS，XML，属性文件等。
 * 鼓励BeanFactory实现支持beans之间的引用(依赖注入/Dependency Injection)。
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 * 与{@link ListableBeanFactory}中的方法相反，
 * 如果这是{@link HierarchicalBeanFactory}，则此接口中的所有操作也将检查父亲Bean工厂。
 * 如果在此工厂实例中找不到bean，则会询问直接的父亲Bean工厂。
 * 此工厂实例中的Beans应该在任何父亲Bean工厂中覆盖相同名称的beans。
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:
 * Bean工厂实现应尽可能地支持标准的<b>bean生命周期接口</b>。
 * 完整的初始化方法及其标准顺序是：
 * <ol>
 * <li>BeanNameAware's {@code setBeanName} 1.Bean名称
 * <li>BeanClassLoaderAware's {@code setBeanClassLoader} 2.Bean类加载器
 * <li>BeanFactoryAware's {@code setBeanFactory} 3.Bean工厂
 * <li>EnvironmentAware's {@code setEnvironment} 4.应用环境
 * <li>EmbeddedValueResolverAware's {@code setEmbeddedValueResolver} 5.嵌套的属性值解析器
 * <li>ResourceLoaderAware's {@code setResourceLoader} 6.资源加载器
 * (only applicable when running in an application context/仅适用于在应用上下文中运行时)
 * <li>ApplicationEventPublisherAware's {@code setApplicationEventPublisher} 7.应用事件发布者
 * (only applicable when running in an application context)
 * <li>MessageSourceAware's {@code setMessageSource} 8.消息源
 * (only applicable when running in an application context)
 * <li>ApplicationContextAware's {@code setApplicationContext} 9.应用上下文
 * (only applicable when running in an application context)
 * <li>ServletContextAware's {@code setServletContext} 10.服务端程序上下文
 * (only applicable when running in a web application context)
 * <li>{@code postProcessBeforeInitialization} methods of BeanPostProcessors 11.Bean后置处理器列表的Bean初始化之前处理
 * <li>InitializingBean's {@code afterPropertiesSet} 12.在设置所有Bean属性后处理
 * <li>a custom init-method definition 13.自定义的初始化方法定义
 * <li>{@code postProcessAfterInitialization} methods of BeanPostProcessors 14.Bean后置处理器列表的Bean初始化完成后处理
 * </ol>
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:
 * 在bean工厂关闭时，以下生命周期方法适用：
 * <ol>
 * <li>{@code postProcessBeforeDestruction} methods of DestructionAwareBeanPostProcessors 1.销毁通知的Bean后置处理器列表的Bean销毁之前处理
 * <li>DisposableBean's {@code destroy} 2.在Bean销毁时处理
 * <li>a custom destroy-method definition 3.自定义的销毁方法定义
 * </ol>
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * {@code myJndiObject} is a FactoryBean, getting {@code &myJndiObject}
	 * will return the factory, not the instance returned by the factory.
	 * 工厂Bean前缀，用于取消引用{@link FactoryBean}实例，并将其与FactoryBean创建的beans区分开来。
	 */
	String FACTORY_BEAN_PREFIX = "&";


	// 查找bean实例
	/// bean名称/类型

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean名称的实例，该实例可以是共享的或独立的。(基于名称查找的自动装配)
	 * <p>This method allows a Spring BeanFactory to be used as a replacement for the
	 * Singleton or Prototype design pattern. Callers may retain references to
	 * returned objects in the case of Singleton beans.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * 将bean别名转换回相应的规范bean名称。
	 * 若在当前bean工厂实例找不到bean时，将询问父亲bean工厂。
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition
	 * with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean名称和类型的实例，该实例可以是共享的或独立的。(bean同名但类型不同)
	 * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
	 * required type. This means that ClassCastException can't be thrown on casting
	 * the result correctly, as can happen with {@link #getBean(String)}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to retrieve
	 * @param requiredType type the bean must match. Can be an interface or superclass
	 * of the actual class, or {@code null} for any match. For example, if the value
	 * is {@code Object.class}, this method will succeed whatever the class of the
	 * returned instance.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * 返回唯一匹配给定对象类型的bean实例，如果有。(基于类型查找的自动装配)
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * {@code null} is disallowed.
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if no bean of the given type was found
	 * @throws NoUniqueBeanDefinitionException if more than one bean of the given type was found
	 * @throws BeansException if the bean could not be created
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/// bean名称/类型+方法参数列表

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean名称和类型的实例，该实例可以是共享的或独立的。
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * 允许指定显示的构造函数/工厂方法参数列表，覆盖bean定义中指定的默认参数(如果有)。
	 * @param name the name of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * 返回指定bean名称和类型的实例，该实例可以是共享的或独立的。
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * 允许指定显示的构造函数/工厂方法参数列表，覆盖bean定义中指定的默认参数(如果有)。
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * {@code null} is disallowed.
	 * @param args arguments to use when creating a bean instance using explicit arguments
	 * (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 4.1
	 */
	<T> T getBean(Class<T> requiredType, Object... args) throws BeansException;

	// bean存在性

	/**
	 * Does this bean factory contain a bean definition or externally registered singleton
	 * instance with the given name?
	 * 这个bean工厂是否包含具有给定名称的bean定义或外部注册的单个实例？
	 * <p>If the given name is an alias, it will be translated back to the corresponding
	 * canonical bean name.
	 * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
	 * be found in this factory instance.
	 * <p>If a bean definition or singleton instance matching the given name is found,
	 * this method will return {@code true} whether the named bean definition is concrete
	 * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
	 * return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	boolean containsBean(String name);

	// bean作用域

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean(String)} always
	 * return the same instance?
	 * 这个bean是共享单例吗？也就是说，{@link #getBean(String)}总会返回相同的实例吗？
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * independent instances. It indicates non-singleton instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
	 * check for independent instances.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return
	 * independent instances?
	 * 这个bean是原型吗？也就是说，{@link #getBean(String)}总会返回独立的实例吗？
	 * <p>Note: This method returning {@code false} does not clearly indicate
	 * a singleton object. It indicates non-independent instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
	 * check for a shared singleton instance.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	// bean类型匹配

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * 检查具有给定名称的bean是否与指定的类型匹配。
	 * 更具体地说，检查给定名称的{@link #getBean(String)}调用是否将返回可分配给指定目标类型的对象。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code ResolvableType})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 4.2
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * 检查具有给定名称的bean是否与指定的类型匹配。
	 * 更具体地说，检查给定名称的{@link #getBean(String)}调用是否将返回可分配给指定目标类型的对象。
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @param typeToMatch the type to match against (as a {@code Class})
	 * @return {@code true} if the bean type matches,
	 * {@code false} if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * 确定具有给定名称的bean的类型。
	 * 更具体地说，确定{@link #getBean(String)}将为给定名称返回对象类型。
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or {@code null} if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	// bean名称别名

	/**
	 * Return the aliases for the given bean name, if any.
	 * All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * 返回给定bean名称的别名列表，如果有。
	 * 当在{@link #getBean(String)}调用中使用时，所有这些别名都指向同一个bean。
	 * <p>If the given name is an alias, the corresponding original bean name
	 * and other aliases (if any) will be returned, with the original bean name
	 * being the first element in the array.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
