
package org.springframework.test.context;

import java.util.List;

/**
 * Factory for creating {@link ContextCustomizer ContextCustomizers}.
 *
 * <p>Factories are invoked after {@link ContextLoader ContextLoaders} have
 * processed context configuration attributes but before the
 * {@link MergedContextConfiguration} is created.
 * 其在上下文加载器({@link ContextLoader})处理上下文配置属性之后被调用，
 * 但在合并的上下文配置({@link MergedContextConfiguration})被创建之前调用。
 *
 * <p>By default, the Spring TestContext Framework will use the
 * {@link org.springframework.core.io.support.SpringFactoriesLoader SpringFactoriesLoader}
 * mechanism for loading factories configured in all {@code META-INF/spring.factories}
 * files on the classpath.
 * 默认情况下，测试上下文框架会使用Spring工厂加载器({@link org.springframework.core.io.support.SpringFactoriesLoader SpringFactoriesLoader})
 * 机制来加载在所有{@code META-INF/spring.factories}文件中配置的工厂组件列表。
 *
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 4.3
 */
// 核心接口 创建应用上下文定制者的工厂
public interface ContextCustomizerFactory {

	/**
	 * Create a {@link ContextCustomizer} that should be used to customize a
	 * {@link org.springframework.context.ConfigurableApplicationContext ConfigurableApplicationContext}
	 * before it is refreshed.
	 * @param testClass the test class (测试类实例)
	 * @param configAttributes the list of context configuration attributes for
	 * the test class, ordered <em>bottom-up</em> (i.e., as if we were traversing
	 * up the class hierarchy); never {@code null} or empty
	 * @return a {@link ContextCustomizer} or {@code null} if no customizer should
	 * be used
	 */
	ContextCustomizer createContextCustomizer(Class<?> testClass, List<ContextConfigurationAttributes> configAttributes);

}
