
package org.springframework.context.support;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringValueResolver;

/**
 * Abstract implementation of the {@link org.springframework.context.ApplicationContext}
 * interface. Doesn't mandate the type of storage used for configuration; simply
 * implements common context functionality. Uses the Template Method design pattern,
 * requiring concrete subclasses to implement abstract methods.
 * 应用上下文接口的骨架实现。
 * 不要求用于配置的存储类型，只需实现常见的应用上下文功能。
 * 使用模板方法设计模式，需要具体的子类来实现抽象方法。
 *
 * <p>In contrast to a plain BeanFactory, an ApplicationContext is supposed
 * to detect special beans defined in its internal bean factory:
 * Therefore, this class automatically registers
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors}
 * and {@link org.springframework.context.ApplicationListener ApplicationListeners}
 * which are defined as beans in the context.
 * 与普通bean工厂相比，应用上下文应该检测在其内部bean工厂中定义的特殊beans。
 * 因此，这个类自动注册bean工厂后置处理器列表BeanFactoryPostProcessors，
 * bean后置处理器列表BeanPostProcessors和应用事件监听器列表ApplicationListeners，
 * 它们在应用上下文中定义为beans。
 *
 * <p>A {@link org.springframework.context.MessageSource} may also be supplied
 * as a bean in the context, with the name "messageSource"; otherwise, message
 * resolution is delegated to the parent context. Furthermore, a multicaster
 * for application events can be supplied as "applicationEventMulticaster" bean
 * of type {@link org.springframework.context.event.ApplicationEventMulticaster}
 * in the context; otherwise, a default multicaster of type
 * {@link org.springframework.context.event.SimpleApplicationEventMulticaster} will be used.
 * 此外，应用事件的多播器可以在应用上下文作为应用事件多播器{@link ApplicationEventMulticaster}类型的
 * "applicationEventMulticaster" bean提供。
 *
 * <p>Implements resource loading through extending
 * {@link org.springframework.core.io.DefaultResourceLoader}.
 * Consequently treats non-URL resource paths as class path resources
 * (supporting full class path resource names that include the package path,
 * e.g. "mypackage/myresource.dat"), unless the {@link #getResourceByPath}
 * method is overwritten in a subclass.
 * 通过扩展默认的资源加载器{@link DefaultResourceLoader}实现资源加载。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Stephane Nicoll
 * @since January 21, 2001
 * @see #refreshBeanFactory
 * @see #getBeanFactory
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.MessageSource
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader
		implements ConfigurableApplicationContext, DisposableBean {

	/**
	 * Name of the MessageSource bean in the factory.
	 * If none is supplied, message resolution is delegated to the parent.
	 * 信息源bean的名称
	 * @see MessageSource
	 */
	public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

	/**
	 * Name of the LifecycleProcessor bean in the factory.
	 * If none is supplied, a DefaultLifecycleProcessor is used.
	 * 组件生命周期处理器bean的名称
	 * @see org.springframework.context.LifecycleProcessor
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

	/**
	 * Name of the ApplicationEventMulticaster bean in the factory.
	 * If none is supplied, a default SimpleApplicationEventMulticaster is used.
	 * 应用事件多播器bean的名称
	 * @see org.springframework.context.event.ApplicationEventMulticaster
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";


	static {
		// Eagerly load the ContextClosedEvent class to avoid weird classloader issues
		// on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
		ContextClosedEvent.class.getName();
	}


	/** Logger used by this class. Available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	// 域(field)

	/**
	 * Unique id for this context, if any
	 * 这个应用上下文的唯一ID。
	 */
	private String id = ObjectUtils.identityToString(this);

	/** Display name (显示名称) */
	private String displayName = ObjectUtils.identityToString(this);

	/**
	 * Parent context
	 * 父亲应用上下文
	 */
	private ApplicationContext parent;

	/**
	 * Environment used by this context
	 * 这个应用上下文使用的应用环境
	 */
	private ConfigurableEnvironment environment;

	/**
	 * BeanFactoryPostProcessors to apply on refresh
	 * 用于刷新的bean工厂后置处理器列表
	 */
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors =
			new ArrayList<BeanFactoryPostProcessor>();

	/**
	 * System time in milliseconds when this context started
	 * 这个应用上下文启动时的系统时间
	 */
	private long startupDate;

	/**
	 * Flag that indicates whether this context is currently active
	 * 这个应用上下文当前活动的标识开关
	 */
	private final AtomicBoolean active = new AtomicBoolean();

	/**
	 * Flag that indicates whether this context has been closed already
	 * 这个应用上下文已关闭的标识开关
	 */
	private final AtomicBoolean closed = new AtomicBoolean();

	/**
	 * Synchronization monitor for the "refresh" and "destroy"
	 * 用于"刷新"和"销毁"的同步监视器
	 */
	private final Object startupShutdownMonitor = new Object();

	/**
	 * Reference to the JVM shutdown hook, if registered
	 * 引用JVM关闭钩子，如果已注册
	 */
	private Thread shutdownHook;

	/** ResourcePatternResolver used by this context
	 * 资源模式解析器
	 */
	private ResourcePatternResolver resourcePatternResolver;

	/**
	 * LifecycleProcessor for managing the lifecycle of beans within this context
	 * 组件生命周期处理器，用于在这个应用上下文中管理beans的生命周期
	 */
	private LifecycleProcessor lifecycleProcessor;

	/** MessageSource we delegate our implementation of this interface to */
	private MessageSource messageSource;

	/**
	 * Helper class used in event publishing
	 * 应用事件发布中使用的助手类，应用事件多播器
	 */
	private ApplicationEventMulticaster applicationEventMulticaster;

	/**
	 * Statically specified listeners
	 * 静态指定的应用事件监听器列表
	 */
	private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<ApplicationListener<?>>();

	/**
	 * ApplicationEvents published early
	 * 早期发布的应用事件列表
	 */
	private Set<ApplicationEvent> earlyApplicationEvents;


	/**
	 * Create a new AbstractApplicationContext with no parent.
	 * 创建一个没有父亲应用上下文的应用上下文实例。
	 */
	public AbstractApplicationContext() {
		this.resourcePatternResolver = getResourcePatternResolver();
	}

	/**
	 * Create a new AbstractApplicationContext with the given parent context.
	 * 使用给定的父亲应用上下文创建新的应用上下文实例。
	 * @param parent the parent context
	 */
	public AbstractApplicationContext(ApplicationContext parent) {
		this();
		// 设置父亲应用上下文
		setParent(parent);
	}


	//---------------------------------------------------------------------
	// Implementation of ApplicationContext interface (应用上下文接口的实现)
	//---------------------------------------------------------------------

	/**
	 * Set the unique id of this application context.
	 * 设置这个应用上下文的唯一ID。
	 * <p>Default is the object id of the context instance, or the name
	 * of the context bean if the context is itself defined as a bean.
	 * 默认值是应用上下文实例的对象ID，如果应用上下文本身被定义为bean，则为应用上下文bean的名称。
	 * @param id the unique id of the context
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getApplicationName() {
		// 应用名称为空
		return "";
	}

	/**
	 * Set a friendly name for this context.
	 * Typically done during initialization of concrete context implementations.
	 * 为这个应用上下文设置友好名称。
	 * 通常在具体应用上下文实现的初始化期间完成。
	 * <p>Default is the object id of the context instance.
	 * 默认值是应用上下文实例的对象ID。
	 */
	public void setDisplayName(String displayName) {
		Assert.hasLength(displayName, "Display name must not be empty");
		this.displayName = displayName;
	}

	/**
	 * Return a friendly name for this context.
	 * 返回这个应用上下文的友好名称。
	 * @return a display name for this context (never {@code null})
	 */
	@Override
	public String getDisplayName() {
		return this.displayName;
	}

	// 父亲应用上下文

	/**
	 * Return the parent context, or {@code null} if there is no parent
	 * (that is, this context is the root of the context hierarchy).
	 * 返回父亲应用上下文，如果没有父亲应用上下文，则返回null。
	 * (这个应用上下文是应用上下文层次结构的根)
	 */
	@Override
	public ApplicationContext getParent() {
		return this.parent;
	}

	// 应用环境

	/**
	 * {@inheritDoc}
	 * 以可配置的形式返回这个应用上下文的应用环境。
	 * <p>If {@code null}, a new environment will be initialized via
	 * {@link #createEnvironment()}.
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}

	/**
	 * {@inheritDoc}
	 * 为这个应用上下文设置可配置的应用环境。
	 * <p>Default value is determined by {@link #createEnvironment()}. Replacing the
	 * default with this method is one option but configuration through {@link
	 * #getEnvironment()} should also be considered. In either case, such modifications
	 * should be performed <em>before</em> {@link #refresh()}.
	 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * Return this context's internal bean factory as AutowireCapableBeanFactory,
	 * if already available.
	 * 将这个应用上下文的内部bean工厂作为具有自动装配能力的bean工厂返回，如果已经可用。
	 * @see #getBeanFactory()
	 */
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		// 可配置的列表的bean工厂
		return getBeanFactory();
	}

	/**
	 * Return the timestamp (ms) when this context was first loaded.
	 * 返回首次加载这个应用上下文的时间戳。
	 */
	@Override
	public long getStartupDate() {
		return this.startupDate;
	}

	// 应用事件监听器

	/**
	 * Publish the given event to all listeners.
	 * 将给定应用事件发布给所有应用事件监听器。
	 * <p>Note: Listeners get initialized after the MessageSource, to be able
	 * to access it within listener implementations. Thus, MessageSource
	 * implementations cannot publish events.
	 * @param event the event to publish (may be application-specific or a
	 * standard framework event)
	 */
	@Override
	public void publishEvent(ApplicationEvent event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * <p>Note: Listeners get initialized after the MessageSource, to be able
	 * to access it within listener implementations. Thus, MessageSource
	 * implementations cannot publish events.
	 * @param event the event to publish (may be an {@link ApplicationEvent}
	 * or a payload object to be turned into a {@link PayloadApplicationEvent})
	 */
	@Override
	public void publishEvent(Object event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * 将给定应用事件发布给所有应用事件监听器。
	 * @param event the event to publish (may be an {@link ApplicationEvent}
	 * or a payload object to be turned into a {@link PayloadApplicationEvent})
	 * @param eventType the resolved event type, if known
	 * @since 4.2
	 */
	protected void publishEvent(Object event, ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");
		if (logger.isTraceEnabled()) {
			// 在哪个应用上下文中发布哪个应用事件的日志
			logger.trace("Publishing event in " + getDisplayName() + ": " + event);
		}

		// Decorate event as an ApplicationEvent if necessary
		// 1.将事件装饰为应用事件
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}
		else {
			applicationEvent = new PayloadApplicationEvent<Object>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent)applicationEvent).getResolvableType();
			}
		}

		// Multicast right now if possible - or lazily once the multicaster is initialized
		// 2.如果可能的话现在就进行组播 - 或在初始化多播器后延迟地进行组播
		if (this.earlyApplicationEvents != null) {
			this.earlyApplicationEvents.add(applicationEvent);
		}
		else {
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}

		// Publish event via parent context as well...
		// 3.通过父亲应用上下文发布应用事件
		if (this.parent != null) {
			if (this.parent instanceof AbstractApplicationContext) {
				// 发布应用上下文事件
				((AbstractApplicationContext) this.parent).publishEvent(event, eventType);
			}
			else {
				// 发布其他应用事件
				this.parent.publishEvent(event);
			}
		}
	}

	/**
	 * Return the internal ApplicationEventMulticaster used by the context.
	 * 返回这个应用上下文使用的内部应用事件多播器。
	 * @return the internal ApplicationEventMulticaster (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (this.applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - " +
					"call 'refresh' before multicasting events via the context: " + this);
		}
		return this.applicationEventMulticaster;
	}

	// 组件生命周期处理器

	/**
	 * Return the internal LifecycleProcessor used by the context.
	 * 返回这个应用上下文使用的内部组件生命周期处理器。
	 * @return the internal LifecycleProcessor (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
		if (this.lifecycleProcessor == null) {
			// 组件生命周期处理器尚未初始化
			throw new IllegalStateException("LifecycleProcessor not initialized - " +
					"call 'refresh' before invoking lifecycle methods via the context: " + this);
		}
		return this.lifecycleProcessor;
	}

	/**
	 * Return the ResourcePatternResolver to use for resolving location patterns
	 * into Resource instances. Default is a
	 * {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},
	 * supporting Ant-style location patterns.
	 * <p>Can be overridden in subclasses, for extended resolution strategies,
	 * for example in a web environment.
	 * <p><b>Do not call this when needing to resolve a location pattern.</b>
	 * Call the context's {@code getResources} method instead, which
	 * will delegate to the ResourcePatternResolver.
	 * @return the ResourcePatternResolver for this context
	 * @see #getResources
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	protected ResourcePatternResolver getResourcePatternResolver() {
		// 路径匹配的资源模式解析器
		return new PathMatchingResourcePatternResolver(this);
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurableApplicationContext interface (可配置的应用上下文接口的实现)
	//---------------------------------------------------------------------

	// 父亲应用上下文

	/**
	 * {@inheritDoc}
	 * 设置这个应用上下文的父亲。
	 * <p>The parent {@linkplain ApplicationContext#getEnvironment() environment} is
	 * {@linkplain ConfigurableEnvironment#merge(ConfigurableEnvironment) merged} with
	 * this (child) application context environment if the parent is non-{@code null} and
	 * its environment is an instance of {@link ConfigurableEnvironment}.
	 * @see ConfigurableEnvironment#merge(ConfigurableEnvironment)
	 */
	@Override
	public void setParent(ApplicationContext parent) {
		this.parent = parent;
		if (parent != null) {
			Environment parentEnvironment = parent.getEnvironment();
			if (parentEnvironment instanceof ConfigurableEnvironment) {
				// 合并父亲应用上下文使用的可配置的应用环境
				getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
			}
		}
	}

	// Bean工厂后置处理器

	/**
	 * {@inheritDoc}
	 * 添加一个新的bean工厂后置处理器，将在刷新时应用于这个应用上下文的内部bean工厂。
	 * 在应用上下文配置期间调用。
	 * @param postProcessor the factory processor to register
	 */
	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
		this.beanFactoryPostProcessors.add(postProcessor);
	}


	/**
	 * Return the list of BeanFactoryPostProcessors that will get applied
	 * to the internal BeanFactory.
	 * 返回将应用于内部bean工厂的bean工厂后置处理器列表。
	 */
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return this.beanFactoryPostProcessors;
	}

	// 应用事件监听器

	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
		if (this.applicationEventMulticaster != null) {
			// 动态扩展地应用事件监听器
			this.applicationEventMulticaster.addApplicationListener(listener);
		}
		else {
			// 静态指定的应用事件监听器
			this.applicationListeners.add(listener);
		}
	}

	/**
	 * Return the list of statically specified ApplicationListeners.
	 */
	public Collection<ApplicationListener<?>> getApplicationListeners() {
		return this.applicationListeners;
	}

	/**
	 * Create and return a new {@link StandardEnvironment}.
	 * 创建并返回一个新的标准的应用环境实例。
	 * <p>Subclasses may override this method in order to supply
	 * a custom {@link ConfigurableEnvironment} implementation.
	 */
	protected ConfigurableEnvironment createEnvironment() {
		// 标准的应用环境
		return new StandardEnvironment();
	}

	/**
	 * {@inheritDoc}
	 * 加载或刷新配置的持久化表示，可能是XML文件，属性文件，数据源或关系数据库模式。
	 * @throws BeansException if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		// 用于"刷新"和"销毁"的同步监视器
		synchronized (this.startupShutdownMonitor) {
			// 一、属性源、环境属性

			// Prepare this context for refreshing.
			// 1.为这个应用上下文刷新做准备
			// 设置其启动时间和活动标识，以及执行占位符属性源的初始化
			prepareRefresh();

			// 二、Bean工厂

			// Tell the subclass to refresh the internal bean factory.
			// 2.告诉应用上下文子类刷新内部的bean工厂
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
			// 3.为在这个应用上下文中使用的bean工厂做准备
			// 包括bean类加载器，应用上下文通知的后置处理器，可解析的依赖关系的类型(bean工厂，资源加载器，应用事件发布者，应用上下文)，
			// 早期的检测内部beans作为应用事件监听器列表的后置处理器，加载时编织者，默认的环境beans。
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
				// 4.允许在应用上下文子类中对bean工厂进行后置处理
				// 在标准初始化之后修改应用上下文的内部bean工厂
				postProcessBeanFactory(beanFactory);

				// 三、Bean工厂后置处理器

				// Invoke factory processors registered as beans in the context.
				// 5.调用注册为beans的bean工厂后置处理器
				invokeBeanFactoryPostProcessors(beanFactory);

				// 四、Bean后置处理器

				// Register bean processors that intercept bean creation.
				// 6.注册拦截bean创建的bean后置处理器
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
				// 7.初始化信息源
				initMessageSource();

				// Initialize event multicaster for this context.
				// 8.初始化应用事件多播器
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
				// 9.在特定的应用上下文子类中初始化其他特殊的beans
				onRefresh();

				// Check for listener beans and register them.
				// 10.检查监听器beans并注册它们
				registerListeners();

				// 二、Bean工厂

				// Instantiate all remaining (non-lazy-init) singletons.
				// 11.实例化所有剩余(非延迟初始化)的单例beans对象
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
				// 12.最后一步：发布相应的应用上下文事件
				finishRefresh();
			}

			catch (BeansException ex) {
				// 应用上下文刷新失败
				if (logger.isWarnEnabled()) {
					// 在应用上下文初始化期间遇到异常，现取消刷新尝试
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				// 1.销毁已经创建的单例beans对象，以避免出现悬空资源
				destroyBeans();

				// Reset 'active' flag.
				// 2.重置'活动'状态标识为非活动状态
				cancelRefresh(ex);

				// Propagate exception to caller.
				// 3.向调用者传播异常
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				// 重置spring核心中的常见内省缓存，因为我们可能不再需要单例beans的元数据了...
				resetCommonCaches();
			}
		}
	}

	// 1.为这个应用上下文刷新做准备

	/**
	 * Prepare this context for refreshing, setting its startup date and
	 * active flag as well as performing any initialization of property sources.
	 * 为这个应用上下文刷新做准备，设置其启动日期和活动标识，以及执行占位符属性源的初始化。
	 */
	protected void prepareRefresh() {
		// 1.这个应用上下文启动时的系统时间
		this.startupDate = System.currentTimeMillis();
		// 这个应用上下文已关闭的标识
		this.closed.set(false);
		// 2.这个应用上下文当前处于活动状态的标识
		this.active.set(true);

		if (logger.isInfoEnabled()) {
			// "开始刷新这个应用上下文状态"的日志
			logger.info("Refreshing " + this);
		}

		// Initialize any placeholder property sources in the context environment
		// 3.在应用上下文环境中初始化任何占位符属性源
		initPropertySources();

		// Validate that all properties marked as required are resolvable
		// see ConfigurablePropertyResolver#setRequiredProperties
		// 4.验证标记为必需的所有属性是否可解析
		getEnvironment().validateRequiredProperties();

		// Allow for the collection of early ApplicationEvents,
		// to be published once the multicaster is available...
		// 5.允许收集早期的应用事件列表，在多播器可用时发布...
		this.earlyApplicationEvents = new LinkedHashSet<ApplicationEvent>();
	}

	/**
	 * Replace any stub property sources with actual instances.
	 * 使用实际实例替换任何存根属性源。
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#initServletPropertySources
	 */
	protected void initPropertySources() {
		// For subclasses: do nothing by default.
		// 对于子类：默认情况下不执行任何操作
	}

	// 2.告诉应用上下文子类刷新内部的bean工厂

	/**
	 * Tell the subclass to refresh the internal bean factory.
	 * 告诉应用上下文子类刷新内部的bean工厂。
	 * @return the fresh BeanFactory instance
	 * @see #refreshBeanFactory()
	 * @see #getBeanFactory()
	 */
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		// 1.刷新内部的bean工厂
		refreshBeanFactory();
		// 2.获取可配置的列表的bean工厂
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (logger.isDebugEnabled()) {
			// "Bean工厂状态"的日志
			logger.debug("Bean factory for " + getDisplayName() + ": " + beanFactory);
		}
		return beanFactory;
	}

	// 3.为在这个应用上下文中使用的bean工厂做准备

	/**
	 * Configure the factory's standard context characteristics,
	 * such as the context's ClassLoader and post-processors.
	 * 配置bean工厂的标准应用上下文特征，
	 * 包括bean类加载器，应用上下文通知的后置处理器，可解析的依赖关系的类型(bean工厂，资源加载器，应用事件发布者，应用上下文)，
	 * 早期的检测内部beans作为应用事件监听器列表的后置处理器，加载时编织者，默认的环境beans。
	 * @param beanFactory the BeanFactory to configure 待配置的bean工厂
	 */
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// Tell the internal bean factory to use the context's class loader etc.
		// 1.告诉内部的bean工厂使用应用上下文的类加载器等
		/// 1.1.用于加载bean类的类加载器
		beanFactory.setBeanClassLoader(getClassLoader());
		/// 1.2.为bean定义值中的表达式指定解析策略
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
		/// 1.3.添加应用于所有bean创建过程的属性编辑器注册表
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

		// Configure the bean factory with context callbacks.
		// 2.使用应用上下文回调通知方法配置bean工厂
		/// 2.1.添加应用上下文通知的后置处理器
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		/// 忽略给定的自动装配的依赖关系接口
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

		// BeanFactory interface not registered as resolvable type in a plain factory.
		// MessageSource registered (and found for autowiring) as a bean.
		// 3.Bean工厂接口未在普通bean工厂中注册为可解析的类型
		// 信息源注册为bean，并发现用于自动装配
		/// 注册可解析的依赖关系的类型(bean工厂，资源加载器，应用事件发布者，应用上下文)
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		beanFactory.registerResolvableDependency(ApplicationContext.class, this);

		// Register early post-processor for detecting inner beans as ApplicationListeners.
		// 4.注册早期的后置处理器，用于检测内部beans作为应用事件监听器列表
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

		// Detect a LoadTimeWeaver and prepare for weaving, if found.
		// 5.检测到加载时编织者并准备编织，如果找到
		if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			/// 5.1.添加加载时编织者通知的后置处理器
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			// Set a temporary ClassLoader for type matching.
			/// 5.2.为类型匹配设置临时的bean类加载器
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}

		// Register default environment beans.
		// 6.注册默认的环境beans
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
			// 注册可配置的应用环境单例
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			// 注册系统属性单例
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			// 注册系统环境单例
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}

	// 4.允许在应用上下文子类中对bean工厂进行后置处理

	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans
	 * will have been instantiated yet. This allows for registering special
	 * BeanPostProcessors etc in certain ApplicationContext implementations.
	 * 在标准初始化之后修改应用上下文的内部bean工厂。
	 * 所有bean定义将会被加载，但尚未实例化任何bean。
	 * 这允许在某些应用上下文实现中注册特殊的bean后置处理器列表。
	 * @param beanFactory the bean factory used by the application context
	 */
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// 待子类覆盖
	}

	// 5.调用注册为beans的bean工厂后置处理器

	/**
	 * Instantiate and invoke all registered BeanFactoryPostProcessor beans,
	 * respecting explicit order if given.
	 * 实例化并调用所有已注册的bean工厂后置处理器beans，遵守显式顺序。
	 * <p>Must be called before singleton instantiation.
	 * 必须在单例实例化之前被调用。
	 */
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		// 1.调用bean工厂后置处理器列表
		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
		// (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		// 2.如果在此期间找到，则检测加载时编织者并准备编织
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			/// 2.1.添加加载时编织者通知的后置处理器
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			/// 2.2.为类型匹配设置临时的bean类加载器
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}

	// 6.注册拦截bean创建的bean后置处理器

	/**
	 * Instantiate and invoke all registered BeanPostProcessor beans,
	 * respecting explicit order if given.
	 * 实例化并调用所有已注册的bean后置处理器beans，遵守显式顺序。
	 * <p>Must be called before any instantiation of application beans.
	 * 必须在应用程序beans的任何实例化之前调用。
	 */
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		// 后置处理器注册委派，注册bean后置处理器列表
		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
	}

	// 7.初始化信息源

	/**
	 * Initialize the MessageSource.
	 * Use parent's if none defined in this context.
	 */
	protected void initMessageSource() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
			this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
			// Make MessageSource aware of parent MessageSource.
			if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
				HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
				if (hms.getParentMessageSource() == null) {
					// Only set parent context as parent MessageSource if no parent MessageSource
					// registered already.
					hms.setParentMessageSource(getInternalParentMessageSource());
				}
			}
			if (logger.isDebugEnabled()) {
				// "使用的信息源实例状态"的日志
				logger.debug("Using MessageSource [" + this.messageSource + "]");
			}
		}
		else {
			// Use empty MessageSource to be able to accept getMessage calls.
			DelegatingMessageSource dms = new DelegatingMessageSource();
			dms.setParentMessageSource(getInternalParentMessageSource());
			this.messageSource = dms;
			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate MessageSource with name '" + MESSAGE_SOURCE_BEAN_NAME +
						"': using default [" + this.messageSource + "]");
			}
		}
	}

	// 8.初始化应用事件多播器

	/**
	 * Initialize the ApplicationEventMulticaster.
	 * Uses SimpleApplicationEventMulticaster if none defined in the context.
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			this.applicationEventMulticaster =
					beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isDebugEnabled()) {
				// "使用的应用事件多播器实例状态"的日志
				logger.debug("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
			}
		}
		else {
			this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate ApplicationEventMulticaster with name '" +
						APPLICATION_EVENT_MULTICASTER_BEAN_NAME +
						"': using default [" + this.applicationEventMulticaster + "]");
			}
		}
	}

	/**
	 * Initialize the LifecycleProcessor.
	 * Uses DefaultLifecycleProcessor if none defined in the context.
	 * 初始化组件生命周期处理器。
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
			this.lifecycleProcessor =
					beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
			if (logger.isDebugEnabled()) {
				// "使用的组件生命周期处理器实例状态"的日志
				logger.debug("Using LifecycleProcessor [" + this.lifecycleProcessor + "]");
			}
		}
		else {
			// 组件生命周期处理器的默认实现
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			this.lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, this.lifecycleProcessor);
			if (logger.isDebugEnabled()) {
				logger.debug("Unable to locate LifecycleProcessor with name '" +
						LIFECYCLE_PROCESSOR_BEAN_NAME +
						"': using default [" + this.lifecycleProcessor + "]");
			}
		}
	}

	// 9.在特定的应用上下文子类中初始化其他特殊的beans

	/**
	 * Template method which can be overridden to add context-specific refresh work.
	 * Called on initialization of special beans, before instantiation of singletons.
	 * 可以被重写的模板方法，以添加特定应用上下文的刷新工作。
	 * 在实例化单例之前调用特殊bean的初始化。
	 * <p>This implementation is empty.
	 * @throws BeansException in case of errors
	 * @see #refresh()
	 */
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
		// 对于子类，默认情况下不执行任何操作
	}

	// 10.检查监听器beans并注册它们

	/**
	 * Add beans that implement ApplicationListener as listeners.
	 * Doesn't affect other listeners, which can be added without being beans.
	 * 添加实现应用事件监听器作为监听器的beans。
	 * 不影响其他监听器，可以添加不是beans的监听器。
	 */
	protected void registerListeners() {
		// Register statically specified listeners first.
		// 1.注册静态指定的应用事件监听器列表
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}

		// Do not initialize FactoryBeans here: We need to leave all regular beans
		// uninitialized to let post-processors apply to them!
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}

		// Publish early application events now that we finally have a multicaster...
		// 使用多播器发布早期的应用事件
		Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
		this.earlyApplicationEvents = null;
		if (earlyEventsToProcess != null) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}

	// 11.实例化所有剩余(非延迟初始化)的单例beans对象

	/**
	 * Finish the initialization of this context's bean factory,
	 * initializing all remaining singleton beans.
	 * 完成这个应用上下文的bean工厂的初始化，初始化所有剩余的单例beans。
	 */
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		// Initialize conversion service for this context.
		// 1.初始化这个应用上下文的转换服务
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
				beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(
					beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}

		// Register a default embedded value resolver if no bean post-processor
		// (such as a PropertyPlaceholderConfigurer bean) registered any before:
		// at this point, primarily for resolution in annotation attribute values.
		// 2.如果没有bean后置处理器，则注册默认的嵌入的值解析器
		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(new StringValueResolver() {
				@Override
				public String resolveStringValue(String strVal) {
					return getEnvironment().resolvePlaceholders(strVal);
				}
			});
		}

		// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
		// 尽早初始化加载时编织者beans，以允许尽早注册其变换器
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(
				LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}

		// Stop using the temporary ClassLoader for type matching.
		// 停止使用临时的类加载器进行类型匹配
		beanFactory.setTempClassLoader(null);

		// Allow for caching all bean definition metadata, not expecting further changes.
		// 允许缓存所有bean定义的元数据，而不期望进一步的更改
		beanFactory.freezeConfiguration();

		// Instantiate all remaining (non-lazy-init) singletons.
		// 实例化所有剩余的单例(非延迟初始化)
		beanFactory.preInstantiateSingletons();
	}

	// 12.最后一步：发布相应的应用上下文事件

	/**
	 * Finish the refresh of this context, invoking the LifecycleProcessor's
	 * onRefresh() method and publishing the
	 * {@link org.springframework.context.event.ContextRefreshedEvent}.
	 * 完成这个应用上下文的刷新，调用组件生命周期管理器的onRefresh()方法，并发布应用上下文刷新完成事件。
	 */
	protected void finishRefresh() {
		// Initialize lifecycle processor for this context.
		// 1.为这个应用上下文初始化组件生命周期处理器
		initLifecycleProcessor();

		// Propagate refresh to lifecycle processor first.
		// 2.传播刷新到组件生命周期处理器
		getLifecycleProcessor().onRefresh();

		// Publish the final event.
		// 3.发布最终的应用上下文的刷新完成事件
		publishEvent(new ContextRefreshedEvent(this));

		// Participate in LiveBeansView MBean, if active.
		// 如果应用上下文处于活动状态，存活的beans视图(构建当前beans及其依赖项的快照)
		LiveBeansView.registerApplicationContext(this);
	}

	// 2.重置'活动'状态标识为非活动状态

	/**
	 * Cancel this context's refresh attempt, resetting the {@code active} flag
	 * after an exception got thrown.
	 * 取消这个应用上下文的刷新尝试，在抛出异常后重置活动标识为非活动状态。
	 * @param ex the exception that led to the cancellation
	 */
	protected void cancelRefresh(BeansException ex) {
		this.active.set(false);
	}

	/**
	 * Reset Spring's common core caches, in particular the {@link ReflectionUtils},
	 * {@link ResolvableType} and {@link CachedIntrospectionResults} caches.
	 * 重置spring的公共核心缓存，尤其是反射、可解析的类型和内省结果缓存。
	 * @since 4.2
	 * @see ReflectionUtils#clearCache()
	 * @see ResolvableType#clearCache()
	 * @see CachedIntrospectionResults#clearClassLoader(ClassLoader)
	 */
	protected void resetCommonCaches() {
		// 清理方法和字段引用对象列表的缓存
		ReflectionUtils.clearCache();
		// 清理可解析的类型的缓存
		ResolvableType.clearCache();
		// 清理给定类加载器的内省缓存
		CachedIntrospectionResults.clearClassLoader(getClassLoader());
	}

	// 应用上下文关闭

	/**
	 * Register a shutdown hook with the JVM runtime, closing this context
	 * on JVM shutdown unless it has already been closed at that time.
	 * 向JVM运行时注册关闭钩子，在JVM关闭时关闭这个应用上下文，除非上下文此时已关闭。
	 * <p>Delegates to {@code doClose()} for the actual closing procedure.
	 * 委托给doClose()方法进行实际的关闭程序。
	 * @see Runtime#addShutdownHook
	 * @see #close()
	 * @see #doClose()
	 */
	@Override
	public void registerShutdownHook() {
		if (this.shutdownHook == null) {
			// No shutdown hook registered yet.
			this.shutdownHook = new Thread() {
				@Override
				public void run() {
					synchronized (startupShutdownMonitor) {
						// 关闭这个应用上下文
						doClose();
					}
				}
			};
			// 添加JVM关闭钩子
			Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		}
	}

	/**
	 * DisposableBean callback for destruction of this instance.
	 * Only called when the ApplicationContext itself is running
	 * as a bean in another BeanFactory or ApplicationContext,
	 * which is rather unusual.
	 * 用于销毁这个实例的DisposableBean回调。
	 * 仅在应用上下文本身作为另一个bean工厂或应用上下文中的bean运行时调用，这是相当不寻常的。
	 * <p>The {@code close} method is the native way to
	 * shut down an ApplicationContext.
	 * close方法是关闭应用上下文的原生方法。
	 * @see #close()
	 * @see org.springframework.beans.factory.access.SingletonBeanFactoryLocator
	 */
	@Override
	public void destroy() {
		// 关闭这个应用上下文
		close();
	}

	/**
	 * Close this application context, destroying all beans in its bean factory.
	 * 关闭这个应用上下文，销毁其bean工厂中的所有beans。
	 * <p>Delegates to {@code doClose()} for the actual closing procedure.
	 * Also removes a JVM shutdown hook, if registered, as it's not needed anymore.
	 * 委托给doClose()方法进行实际的关闭程序。
	 * 如果已注册，还会删除JVM关闭钩子，因为它不再需要。
	 * @see #doClose()
	 * @see #registerShutdownHook()
	 */
	@Override
	public void close() {
		synchronized (this.startupShutdownMonitor) {
			// 关闭这个应用上下文
			doClose();
			// If we registered a JVM shutdown hook, we don't need it anymore now:
			// We've already explicitly closed the context.
			if (this.shutdownHook != null) {
				try {
					// 删除JVM关闭钩子
					Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
				}
				catch (IllegalStateException ex) {
					// ignore - VM is already shutting down
				}
			}
		}
	}

	/**
	 * Actually performs context closing: publishes a ContextClosedEvent and
	 * destroys the singletons in the bean factory of this application context.
	 * 执行应用上下文关闭：发布应用上下文已关闭事件，并销毁这个应用上下文的bean工厂中的所有单例beans。
	 * <p>Called by both {@code close()} and a JVM shutdown hook, if any.
	 * 由close()和JVM关闭钩子调用，如果有的话。
	 * @see org.springframework.context.event.ContextClosedEvent
	 * @see #destroyBeans()
	 * @see #close()
	 * @see #registerShutdownHook()
	 */
	protected void doClose() {
		if (this.active.get() && this.closed.compareAndSet(false, true)) {
			if (logger.isInfoEnabled()) {
				// "开始关闭这个应用上下文"的日志
				logger.info("Closing " + this);
			}

			// 1.注销这个应用上下文
			LiveBeansView.unregisterApplicationContext(this);

			try {
				// Publish shutdown event.
				// 2.发布应用上下文已关闭的事件
				publishEvent(new ContextClosedEvent(this));
			}
			catch (Throwable ex) {
				logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
			}

			// Stop all Lifecycle beans, to avoid delays during individual destruction.
			// 3.停止所有生命周期beans，以避免在个别销毁期间出现延迟
			try {
				getLifecycleProcessor().onClose();
			}
			catch (Throwable ex) {
				logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
			}

			// Destroy all cached singletons in the context's BeanFactory.
			// 4.销毁所有缓存在这个应用上下文的bean工厂中的单例beans
			destroyBeans();

			// Close the state of this context itself.
			// 5.关闭这个应用上下文本身的状态(释放其内部的bean工厂)
			closeBeanFactory();

			// Let subclasses do some final clean-up if they wish...
			// 6.如果希望子类做最后的清理...(子类扩展)
			onClose();

			// 7.设置应用上下文为"非活动"状态
			this.active.set(false);
		}
	}

	/**
	 * Template method for destroying all beans that this context manages.
	 * The default implementation destroy all cached singletons in this context,
	 * invoking {@code DisposableBean.destroy()} and/or the specified
	 * "destroy-method".
	 * 用于销毁这个应用上下文管理的所有beans的模板方法。
	 * 默认实现是销毁这个应用上下文中所有缓存的单例beans，调用{@code DisposableBean.destroy()}和/或指定的"destroy-method"。
	 * <p>Can be overridden to add context-specific bean destruction steps
	 * right before or right after standard singleton destruction,
	 * while the context's BeanFactory is still active.
	 * @see #getBeanFactory()
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
	 */
	protected void destroyBeans() {
		// 销毁这个工厂中的所有单例beans，包括已注册为一次性的内部beans
		getBeanFactory().destroySingletons();
	}

	/**
	 * Template method which can be overridden to add context-specific shutdown work.
	 * The default implementation is empty.
	 * 可以被重写的模板方法，以添加特定应用上下文的关闭工作。
	 * 默认实现是空操作。
	 * <p>Called at the end of {@link #doClose}'s shutdown procedure, after
	 * this context's BeanFactory has been closed. If custom shutdown logic
	 * needs to execute while the BeanFactory is still active, override
	 * the {@link #destroyBeans()} method instead.
	 */
	protected void onClose() {
		// For subclasses: do nothing by default.
		// 对于子类，默认情况下不执行任何操作
	}

	@Override
	public boolean isActive() {
		return this.active.get();
	}

	/**
	 * Assert that this context's BeanFactory is currently active,
	 * throwing an {@link IllegalStateException} if it isn't.
	 * 断言这个应用上下文的Bean工厂当前处于活动状态，如果不是则抛出非法状态异常。
	 * <p>Invoked by all {@link BeanFactory} delegation methods that depend
	 * on an active context, i.e. in particular all bean accessor methods.
	 * <p>The default implementation checks the {@link #isActive() 'active'} status
	 * of this context overall. May be overridden for more specific checks, or for a
	 * no-op if {@link #getBeanFactory()} itself throws an exception in such a case.
	 */
	protected void assertBeanFactoryActive() {
		if (!this.active.get()) {
			// 非活动状态
			if (this.closed.get()) {
				// 这个应用上下文已经关闭
				throw new IllegalStateException(getDisplayName() + " has been closed already");
			}
			else {
				// 这个应用上下文还没有刷新
				throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface (Bean工厂接口的实现)
	//---------------------------------------------------------------------
	// 必填参数：bean名称/类型

	@Override
	public Object getBean(String name) throws BeansException {
		// 断言这个应用上下文的Bean工厂当前处于活动状态
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		// 断言这个应用上下文的Bean工厂当前处于活动状态
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, requiredType);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType, args);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		// 别名列表
		return getBeanFactory().getAliases(name);
	}


	//---------------------------------------------------------------------
	// Implementation of ListableBeanFactory interface (列表的Bean工厂接口的实现)
	//---------------------------------------------------------------------
	// 必填参数：bean名称/类型

	// Bean定义

	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		// Bean定义的名称列表
		return getBeanFactory().getBeanDefinitionNames();
	}

	// Bean名称列表

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	// Bean实例映射表

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	// 注解的Bean

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForAnnotation(annotationType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansWithAnnotation(annotationType);
	}

	@Override
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
	}


	//---------------------------------------------------------------------
	// Implementation of HierarchicalBeanFactory interface (分层的Bean工厂接口的实现)
	//---------------------------------------------------------------------

	@Override
	public BeanFactory getParentBeanFactory() {
		// 父亲Bean工厂
		return getParent();
	}

	@Override
	public boolean containsLocalBean(String name) {
		return getBeanFactory().containsLocalBean(name);
	}

	/**
	 * Return the internal bean factory of the parent context if it implements
	 * ConfigurableApplicationContext; else, return the parent context itself.
	 * @see org.springframework.context.ConfigurableApplicationContext#getBeanFactory
	 */
	protected BeanFactory getInternalParentBeanFactory() {
		return (getParent() instanceof ConfigurableApplicationContext) ?
				((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent();
	}


	//---------------------------------------------------------------------
	// Implementation of MessageSource interface
	//---------------------------------------------------------------------

	@Override
	public String getMessage(String code, Object args[], String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, Object args[], Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(code, args, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(resolvable, locale);
	}

	/**
	 * Return the internal MessageSource used by the context.
	 * @return the internal MessageSource (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	private MessageSource getMessageSource() throws IllegalStateException {
		if (this.messageSource == null) {
			throw new IllegalStateException("MessageSource not initialized - " +
					"call 'refresh' before accessing messages via the context: " + this);
		}
		return this.messageSource;
	}

	/**
	 * Return the internal message source of the parent context if it is an
	 * AbstractApplicationContext too; else, return the parent context itself.
	 */
	protected MessageSource getInternalParentMessageSource() {
		return (getParent() instanceof AbstractApplicationContext) ?
			((AbstractApplicationContext) getParent()).messageSource : getParent();
	}


	//---------------------------------------------------------------------
	// Implementation of ResourcePatternResolver interface (资源模式解析器接口的实现)
	//---------------------------------------------------------------------

	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return this.resourcePatternResolver.getResources(locationPattern);
	}


	//---------------------------------------------------------------------
	// Implementation of Lifecycle interface (组件生命周期处理器接口的实现)
	//---------------------------------------------------------------------

	@Override
	public void start() {
		// 启动这个应用上下文
		getLifecycleProcessor().start();
		// 发布这个应用上下文启动事件
		publishEvent(new ContextStartedEvent(this));
	}

	@Override
	public void stop() {
		// 停止这个应用上下文
		getLifecycleProcessor().stop();
		// 发布这个应用上下文停止事件
		publishEvent(new ContextStoppedEvent(this));
	}

	@Override
	public boolean isRunning() {
		// 检查这个应用上下文当前是否正在运行
		return (this.lifecycleProcessor != null && this.lifecycleProcessor.isRunning());
	}


	//---------------------------------------------------------------------
	// Abstract methods that must be implemented by subclasses
	// 必须由子类实现的抽象方法
	//---------------------------------------------------------------------

	/**
	 * Subclasses must implement this method to perform the actual configuration load.
	 * The method is invoked by `{@link #refresh()} before any other initialization work.
	 * 应用上下文子类必须实现本方法，执行实际的配置加载。
	 * 在任何其它初始化工作之前，由{@link #refresh()}调用本方法。
	 * <p>A subclass will either create a new bean factory and hold a reference to it,
	 * or return a single BeanFactory instance that it holds. In the latter case, it will
	 * usually throw an IllegalStateException if refreshing the context more than once.
	 * @throws BeansException if initialization of the bean factory failed
	 * @throws IllegalStateException if already initialized and multiple refresh
	 * attempts are not supported
	 */
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

	/**
	 * Subclasses must implement this method to release their internal bean factory.
	 * This method gets invoked by {@link #close()} after all other shutdown work.
	 * 应用上下文子类必须实现本方法，释放其内部的bean工厂。
	 * 在所有其他关闭工作之后，由{@link #close()}调用本方法。
	 * <p>Should never throw an exception but rather log shutdown failures.
	 */
	protected abstract void closeBeanFactory();

	/**
	 * Subclasses must return their internal bean factory here. They should implement the
	 * lookup efficiently, so that it can be called repeatedly without a performance penalty.
	 * 返回应用上下文子类其内部的bean工厂。
	 * 它们应该有效地实现查找，以便可以被重复地调用而不会降低性能。
	 * <p>Note: Subclasses should check whether the context is still active before
	 * returning the internal bean factory. The internal factory should generally be
	 * considered unavailable once the context has been closed.
	 * 注意：应用上下文子类应在返回内部的bean工厂之前检查应用上下文是否仍处于活动状态。
	 * 一旦应用上下文已关闭，通常应将内部工厂视为不可用。
	 * @return this application context's internal bean factory (never {@code null}) 本应用上下文的内部bean工厂
	 * @throws IllegalStateException if the context does not hold an internal bean factory yet
	 * (usually if {@link #refresh()} has never been called) or if the context has been
	 * closed already
	 * @see #refreshBeanFactory()
	 * @see #closeBeanFactory()
	 */
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;


	/**
	 * Return information about this context.
	 * 返回这个应用上下文的相关信息。
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getDisplayName());
		// 这个应用上下文启动时的系统时间
		sb.append(": startup date [").append(new Date(getStartupDate()));
		sb.append("]; ");
		// 父亲应用上下文
		ApplicationContext parent = getParent();
		if (parent == null) {
			// 应用上下文层次结构的根
			sb.append("root of context hierarchy");
		}
		else {
			// 父亲应用上下文
			sb.append("parent: ").append(parent.getDisplayName());
		}
		return sb.toString();
	}

}
