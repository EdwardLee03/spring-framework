
package org.springframework.context;

/**
 * Strategy interface for processing Lifecycle beans within the ApplicationContext.
 * 组件生命周期处理器，用于在应用上下文中处理生命周期组件列表的策略接口。
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface LifecycleProcessor extends Lifecycle {

	/**
	 * Notification of context refresh, e.g. for auto-starting components.
	 * 应用上下文刷新阶段的通知，如自动启动的组件列表。
	 */
	void onRefresh();

	/**
	 * Notification of context close phase, e.g. for auto-stopping components.
	 * 应用上下文关闭阶段的通知，如自动停止的组件列表。
	 */
	void onClose();

}
