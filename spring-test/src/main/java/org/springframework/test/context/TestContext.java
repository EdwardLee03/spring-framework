
package org.springframework.test.context;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;
import org.springframework.core.AttributeAccessor;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;

/**
 * {@code TestContext} encapsulates the context in which a test is executed,
 * agnostic of the actual testing framework in use.
 *
 * @author Sam Brannen
 * @since 2.5
 */
// 核心接口 测试执行上下文
public interface TestContext extends AttributeAccessor, Serializable {

	/**
	 * Get the {@linkplain ApplicationContext application context} for this
	 * test context, possibly cached.
	 * <p>Implementations of this method are responsible for loading the
	 * application context if the corresponding context has not already been
	 * loaded, potentially caching the context as well.
	 * @return the application context
	 * @throws IllegalStateException if an error occurs while retrieving the
	 * application context
	 */
    // 核心方法 获取此测试上下文的应用上下文
	ApplicationContext getApplicationContext();

	/**
	 * Get the {@linkplain Class test class} for this test context.
	 * @return the test class (never {@code null})
	 */
    // 核心方法 获取此测试上下文的测试类
	Class<?> getTestClass();

	/**
	 * Get the current {@linkplain Object test instance} for this test context.
	 * <p>Note: this is a mutable property.
	 * @return the current test instance (may be {@code null})
	 * @see #updateState(Object, Method, Throwable)
	 */
    // 核心方法 获取当前的测试实例
	Object getTestInstance();

	/**
	 * Get the current {@linkplain Method test method} for this test context.
	 * <p>Note: this is a mutable property.
	 * @return the current test method (may be {@code null})
	 * @see #updateState(Object, Method, Throwable)
	 */
    // 核心方法 获取当前的测试法
	Method getTestMethod();

	/**
	 * Get the {@linkplain Throwable exception} that was thrown during execution
	 * of the {@linkplain #getTestMethod() test method}.
	 * <p>Note: this is a mutable property.
	 * @return the exception that was thrown, or {@code null} if no
	 * exception was thrown
	 * @see #updateState(Object, Method, Throwable)
	 */
    // 核心方法 获取测试方法执行期间抛出的异常
	Throwable getTestException();

	/**
	 * Call this method to signal that the {@linkplain ApplicationContext application
	 * context} associated with this test context is <em>dirty</em> and should be
	 * removed from the context cache.
	 * <p>Do this if a test has modified the context &mdash; for example, by
	 * modifying the state of a singleton bean, modifying the state of an embedded
	 * database, etc.
	 * @param hierarchyMode the context cache clearing mode to be applied if the
	 * context is part of a hierarchy (may be {@code null})
	 */
	void markApplicationContextDirty(HierarchyMode hierarchyMode);

	/**
	 * Update this test context to reflect the state of the currently executing
	 * test.
	 * <p>Caution: concurrent invocations of this method might not be thread-safe,
	 * depending on the underlying implementation.
	 * @param testInstance the current test instance (may be {@code null})
	 * @param testMethod the current test method (may be {@code null})
	 * @param testException the exception that was thrown in the test method, or
	 * {@code null} if no exception was thrown
	 */
    // 核心方法 更新当前执行测试的状态
	void updateState(Object testInstance, Method testMethod, Throwable testException);

}
