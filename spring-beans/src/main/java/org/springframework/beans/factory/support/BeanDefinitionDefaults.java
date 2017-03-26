
package org.springframework.beans.factory.support;

import org.springframework.util.StringUtils;

/**
 * A simple holder for {@code BeanDefinition} property defaults.
 *
 * @author Mark Fisher
 * @since 2.5
 */
// 组件定义默认值列表
public class BeanDefinitionDefaults {

	/**
	 * 是否延迟初始化
	 */
	private boolean lazyInit;

    /**
     * 依赖检查
     */
	private int dependencyCheck = AbstractBeanDefinition.DEPENDENCY_CHECK_NONE;

    /**
     * 自动装配模式
     */
	private int autowireMode = AbstractBeanDefinition.AUTOWIRE_NO;

    /**
     * 初始化方法名称
     */
	private String initMethodName;

    /**
     * 销毁方法名称
     */
	private String destroyMethodName;


	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	public boolean isLazyInit() {
		return this.lazyInit;
	}

	public void setDependencyCheck(int dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	public int getDependencyCheck() {
		return this.dependencyCheck;
	}

	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	public int getAutowireMode() {
		return this.autowireMode;
	}

	public void setInitMethodName(String initMethodName) {
		this.initMethodName = (StringUtils.hasText(initMethodName)) ? initMethodName : null;
	}

	public String getInitMethodName() {
		return this.initMethodName;
	}

	public void setDestroyMethodName(String destroyMethodName) {
		this.destroyMethodName = (StringUtils.hasText(destroyMethodName)) ? destroyMethodName : null;
	}

	public String getDestroyMethodName() {
		return this.destroyMethodName;
	}

}
