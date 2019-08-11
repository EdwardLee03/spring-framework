
package org.springframework.core.env;

/**
 * Interface representing the environment in which the current application is running.
 * Models two key aspects of the application environment: <em>profiles</em> and
 * <em>properties</em>. Methods related to property access are exposed via the
 * {@link PropertyResolver} superinterface.
 * 表示当前应用程序正在运行的环境的接口。
 * 应用环境的两个关键模型：<em>配置文件(profiles)</em>和<em>属性集合(properties)</em>。
 * 与属性访问相关的方法通过{@link PropertyResolver}超接口暴露。
 *
 * <p>A <em>profile</em> is a named, logical group of bean definitions to be registered
 * with the container only if the given profile is <em>active</em>. Beans may be assigned
 * to a profile whether defined in XML or via annotations; see the spring-beans 3.1 schema
 * or the {@link org.springframework.context.annotation.Profile @Profile} annotation for
 * syntax details. The role of the {@code Environment} object with relation to profiles is
 * in determining which profiles (if any) are currently {@linkplain #getActiveProfiles
 * active}, and which profiles (if any) should be {@linkplain #getDefaultProfiles active
 * by default}.
 * 只有给定的配置文件处于活动状态，配置文件才是要向容器注册的一个命名逻辑组的Bean定义列表。
 * 与配置文件相关的环境对象的角色是确定哪些配置文件当前是活动的。
 *
 * <p><em>Properties</em> play an important role in almost all applications, and may
 * originate from a variety of sources: properties files, JVM system properties, system
 * environment variables, JNDI, servlet context parameters, ad-hoc Properties objects,
 * Maps, and so on. The role of the environment object with relation to properties is to
 * provide the user with a convenient service interface for configuring property sources
 * and resolving properties from them.
 * 属性集合在所有应用程序中扮演着重要角色，并且可能源自各种资源：属性文件，JVM系统属性，系统环境变量，servlet上下文参数，哈希表等。
 * 与属性集合相关的环境对象的角色是为用户提供方便的服务接口，用于配置属性源和从中解析属性。
 *
 * <p>Beans managed within an {@code ApplicationContext} may register to be {@link
 * org.springframework.context.EnvironmentAware EnvironmentAware} or {@code @Inject} the
 * {@code Environment} in order to query profile state or resolve properties directly.
 * 使用应用环境通知来注入应用环境bean，以便直接查询配置文件状态或解析属性。
 *
 * <p>In most cases, however, application-level beans should not need to interact with the
 * {@code Environment} directly but instead may have to have {@code ${...}} property
 * values replaced by a property placeholder configurer such as
 * {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}, which itself is {@code EnvironmentAware} and
 * as of Spring 3.1 is registered by default when using
 * {@code <context:property-placeholder/>}.
 * 但是，在大多数情况下，应用程序级别的beans不需要直接与应用环境对象交互，
 * 而是可能必须由属性占位符配置器将{@code ${...}}属性值替换，例如属性源占位符配置者PropertySourcesPlaceholderConfigurer，
 * 它本身就是应用环境通知对象EnvironmentAware。
 *
 * <p>Configuration of the environment object must be done through the
 * {@code ConfigurableEnvironment} interface, returned from all
 * {@code AbstractApplicationContext} subclass {@code getEnvironment()} methods. See
 * {@link ConfigurableEnvironment} Javadoc for usage examples demonstrating manipulation
 * of property sources prior to application context {@code refresh()}.
 * 必须通过可配置的应用环境接口ConfigurableEnvironment完成应用环境对象的配置，
 * 该接口从所有应用上下文抽象类AbstractApplicationContext的子类的getEnvironment()方法返回。
 * 有关在应用上下文刷新refresh()之前演示属性源操作的用法示例，请阅读ConfigurableEnvironment文档。
 *
 * @author Chris Beams
 * @since 3.1
 * @see PropertyResolver
 * @see EnvironmentCapable
 * @see ConfigurableEnvironment
 * @see AbstractEnvironment
 * @see StandardEnvironment
 * @see org.springframework.context.EnvironmentAware
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#setEnvironment
 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
 */
public interface Environment extends PropertyResolver {

	/**
	 * Return the set of profiles explicitly made active for this environment. Profiles
	 * are used for creating logical groupings of bean definitions to be registered
	 * conditionally, for example based on deployment environment.  Profiles can be
	 * activated by setting {@linkplain AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 * "spring.profiles.active"} as a system property or by calling
	 * {@link ConfigurableEnvironment#setActiveProfiles(String...)}.
	 * 返回显式为这个应用环境活动的配置文件集。
	 * 配置文件用于创建要有条件地注册的bean定义的逻辑分组，例如基于部署环境。
	 * 可以通过将"spring.profiles.active"设置为系统属性或通过调用{@link ConfigurableEnvironment#setActiveProfiles(String...)}
	 * 来激活配置文件。
	 * <p>If no profiles have explicitly been specified as active, then any
	 * {@linkplain #getDefaultProfiles() default profiles} will automatically be activated.
	 * @see #getDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	String[] getActiveProfiles();

	/**
	 * Return the set of profiles to be active by default when no active profiles have
	 * been set explicitly.
	 * 没有显示设置活动配置文件时，默认的配置文件会被激活。
	 * @see #getActiveProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 */
	String[] getDefaultProfiles();

	/**
	 * Return whether one or more of the given profiles is active or, in the case of no
	 * explicit active profiles, whether one or more of the given profiles is included in
	 * the set of default profiles. If a profile begins with '!' the logic is inverted,
	 * i.e. the method will return true if the given profile is <em>not</em> active.
	 * For example, <pre class="code">env.acceptsProfiles("p1", "!p2")</pre> will
	 * return {@code true} if profile 'p1' is active or 'p2' is not active.
	 * 返回一个或多个给定配置文件是否处于活动状态，
	 * 或者在没有显式活动配置文件的情况下，返回一个或多个给定配置文件是否包含在默认配置文件集中。
	 * @throws IllegalArgumentException if called with zero arguments
	 * or if any profile is {@code null}, empty or whitespace-only
	 * @see #getActiveProfiles
	 * @see #getDefaultProfiles
	 */
	boolean acceptsProfiles(String... profiles);

}
