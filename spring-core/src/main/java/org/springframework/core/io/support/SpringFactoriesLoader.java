
package org.springframework.core.io.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.UrlResource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * General purpose factory loading mechanism for internal use within the framework.
 * 框架内部使用的通用工厂加载机制。
 *
 * <p>{@code SpringFactoriesLoader} {@linkplain #loadFactories loads} and instantiates
 * factories of a given type from {@value #FACTORIES_RESOURCE_LOCATION} files which
 * may be present in multiple JAR files in the classpath. The {@code spring.factories}
 * file must be in {@link Properties} format, where the key is the fully qualified
 * name of the interface or abstract class, and the value is a comma-separated list of
 * implementation class names. For example:
 * SpringFactoriesLoader加载并实例化来自工厂资源位置文件("META-INF/spring.factories")中指定类型的工厂组件列表，
 * 它可能存在于类路径中的多个JAR文件中。
 * "spring.factories"文件必须是属性文件格式(Properties)，
 * 键是接口或抽象类的完全限定名称，值是以逗号分隔的实现类名称列表。
 *
 * <pre class="code">example.MyService=example.MyServiceImpl1,example.MyServiceImpl2</pre>
 *
 * where {@code example.MyService} is the name of the interface, and {@code MyServiceImpl1}
 * and {@code MyServiceImpl2} are two implementations.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.2
 */
public abstract class SpringFactoriesLoader {

	private static final Log logger = LogFactory.getLog(SpringFactoriesLoader.class);

	/**
	 * The location to look for factories.
	 * 寻找工厂组件列表的位置。
	 * <p>Can be present in multiple JAR files.
	 * 可以存在于多个JAR文件中。
	 */
	public static final String FACTORIES_RESOURCE_LOCATION = "META-INF/spring.factories";


	/**
	 * Load and instantiate the factory implementations of the given type from
	 * {@value #FACTORIES_RESOURCE_LOCATION}, using the given class loader.
	 * 使用给定的类加载器加载并实例化指定类型的工厂实现类列表。
	 * <p>The returned factories are sorted in accordance with the {@link AnnotationAwareOrderComparator}.
	 * <p>If a custom instantiation strategy is required, use {@link #loadFactoryNames}
	 * to obtain all registered factory names.
	 * @param factoryClass the interface or abstract class representing the factory (表示工厂的接口或抽象类)
	 * @param classLoader the ClassLoader to use for loading (can be {@code null} to use the default) (用于加载工厂类的类加载器)
	 * @see #loadFactoryNames
	 * @throws IllegalArgumentException if any factory implementation class cannot
	 * be loaded or if an error occurs while instantiating any factory
	 */
	public static <T> List<T> loadFactories(Class<T> factoryClass, ClassLoader classLoader) {
		Assert.notNull(factoryClass, "'factoryClass' must not be null");
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			// Spring框架工厂加载器使用的类加载器
			classLoaderToUse = SpringFactoriesLoader.class.getClassLoader();
		}
		// 获取所有已注册的工厂实现类名称列表
		List<String> factoryNames = loadFactoryNames(factoryClass, classLoaderToUse);
		if (logger.isTraceEnabled()) {
			// 已加载工厂类名称列表
			logger.trace("Loaded [" + factoryClass.getName() + "] names: " + factoryNames);
		}
		// 工厂组件列表
		List<T> result = new ArrayList<T>(factoryNames.size());
		for (String factoryName : factoryNames) {
			// 实例化工厂组件
			result.add(instantiateFactory(factoryName, factoryClass, classLoaderToUse));
		}
		// 排序
		AnnotationAwareOrderComparator.sort(result);
		return result;
	}

	/**
	 * Load the fully qualified class names of factory implementations of the
	 * given type from {@value #FACTORIES_RESOURCE_LOCATION}, using the given
	 * class loader.
	 * 使用给定的类加载器加载指定类型的工厂实现的完全限定类名，来自工厂资源位置文件("META-INF/spring.factories")。
	 * @param factoryClass the interface or abstract class representing the factory
	 * @param classLoader the ClassLoader to use for loading resources; can be
	 * {@code null} to use the default
	 * @see #loadFactories
	 * @throws IllegalArgumentException if an error occurs while loading factory names
	 */
	public static List<String> loadFactoryNames(Class<?> factoryClass, ClassLoader classLoader) {
		// 工厂类名称
		String factoryClassName = factoryClass.getName();
		try {
			// "系统+引导+应用"资源
			// 系统资源("系统+引导")
			Enumeration<URL> urls = (classLoader != null ? classLoader.getResources(FACTORIES_RESOURCE_LOCATION) :
					ClassLoader.getSystemResources(FACTORIES_RESOURCE_LOCATION));
			List<String> result = new ArrayList<String>();
			while (urls.hasMoreElements()) {
				URL url = urls.nextElement();
				// 加载"META-INF/spring.factories"属性文件
				Properties properties = PropertiesLoaderUtils.loadProperties(new UrlResource(url));
				// 配置的工厂实现类名称列表
				String factoryClassNames = properties.getProperty(factoryClassName);
				// 逗号分隔(",")
				result.addAll(Arrays.asList(StringUtils.commaDelimitedListToStringArray(factoryClassNames)));
			}
			return result;
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Unable to load [" + factoryClass.getName() +
					"] factories from location [" + FACTORIES_RESOURCE_LOCATION + "]", ex);
		}
	}

	/**
	 * 实例化工厂组件。
	 */
	@SuppressWarnings("unchecked")
	private static <T> T instantiateFactory(String instanceClassName, Class<T> factoryClass, ClassLoader classLoader) {
		try {
			// 工厂实例的类型对象
			Class<?> instanceClass = ClassUtils.forName(instanceClassName, classLoader);
			// 实现类判断
			if (!factoryClass.isAssignableFrom(instanceClass)) {
				throw new IllegalArgumentException(
						"Class [" + instanceClassName + "] is not assignable to [" + factoryClass.getName() + "]");
			}
			// 获取默认无参的构造函数
			Constructor<?> constructor = instanceClass.getDeclaredConstructor();
			// 标记构造函数可访问
			ReflectionUtils.makeAccessible(constructor);
			// 创建并初始化新的实例(Constructor.newInstance())
			return (T) constructor.newInstance();
		}
		catch (Throwable ex) {
			throw new IllegalArgumentException("Unable to instantiate factory class: " + factoryClass.getName(), ex);
		}
	}

}
