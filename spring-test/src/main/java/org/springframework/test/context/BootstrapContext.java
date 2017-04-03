
package org.springframework.test.context;

/**
 * {@code BootstrapContext} encapsulates the context in which the <em>Spring
 * TestContext Framework</em> is bootstrapped.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see BootstrapWith
 * @see TestContextBootstrapper
 */
// 核心接口 引导上下文
public interface BootstrapContext {

	/**
	 * Get the {@linkplain Class test class} for this bootstrap context.
	 * @return the test class (never {@code null})
	 */
    // 核心方法 获取测试类实例
	Class<?> getTestClass();

	/**
	 * Get the {@link CacheAwareContextLoaderDelegate} to use for transparent
	 * interaction with the {@code ContextCache}.
	 * @return the context loader delegate (never {@code null})
	 */
    // 核心方法 用于与上下文缓存的透明交互
	CacheAwareContextLoaderDelegate getCacheAwareContextLoaderDelegate();

}
