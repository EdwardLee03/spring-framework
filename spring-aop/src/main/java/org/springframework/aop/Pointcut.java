
package org.springframework.aop;

/**
 * Core Spring pointcut abstraction.
 * 切入点。
 *
 * <p>A pointcut is composed of a {@link ClassFilter} and a {@link MethodMatcher}.
 * Both these basic terms and a Pointcut itself can be combined to build up combinations
 * (e.g. through {@link org.springframework.aop.support.ComposablePointcut}).
 * 切入点由类型匹配过滤者{@link ClassFilter}和方法匹配者{@link MethodMatcher}组成。
 * 这些基本术语和切入点本身可以结合来建立组合。
 *
 * @author Rod Johnson
 * @see ClassFilter
 * @see MethodMatcher
 * @see org.springframework.aop.support.Pointcuts
 * @see org.springframework.aop.support.ClassFilters
 * @see org.springframework.aop.support.MethodMatchers
 */
public interface Pointcut {

	/**
	 * Return the ClassFilter for this pointcut.
	 * 返回这个切入点的类型匹配过滤者。
	 * @return the ClassFilter (never {@code null})
	 */
	ClassFilter getClassFilter();

	/**
	 * Return the MethodMatcher for this pointcut.
	 * 返回这个切入点的方法匹配者。
	 * @return the MethodMatcher (never {@code null})
	 */
	MethodMatcher getMethodMatcher();


	/**
	 * Canonical Pointcut instance that always matches.
	 * 总是匹配的切入点实例
	 */
	Pointcut TRUE = TruePointcut.INSTANCE;

}
