
package org.springframework.context;

/**
 * An extension of the {@link Lifecycle} interface for those objects that require to
 * be started upon ApplicationContext refresh and/or shutdown in a particular order.
 * The {@link #isAutoStartup()} return value indicates whether this object should
 * be started at the time of a context refresh. The callback-accepting
 * {@link #stop(Runnable)} method is useful for objects that have an asynchronous
 * shutdown process. Any implementation of this interface <i>must</i> invoke the
 * callback's run() method upon shutdown completion to avoid unnecessary delays
 * in the overall ApplicationContext shutdown.
 * {@link Lifecycle}接口的扩展，组件生命周期智能管理接口，需要基于应用上下文的刷新或关闭被启动。
 * 是否自动启动方法的返回值表明组件对象在应用上下文刷新时应该被启动。
 * 接收回调的停止任务线程方法对于具有异步关闭过程的组件对象是非常有用的。
 * 这个接口的任何实现类都必须在关闭完成后调用回调的run方法，避免不必要的延迟。
 *
 * <p>This interface extends {@link Phased}, and the {@link #getPhase()} method's
 * return value indicates the phase within which this Lifecycle component should
 * be started and stopped. The startup process begins with the <i>lowest</i>
 * phase value and ends with the <i>highest</i> phase value (Integer.MIN_VALUE
 * is the lowest possible, and Integer.MAX_VALUE is the highest possible). The
 * shutdown process will apply the reverse order. Any components with the
 * same value will be arbitrarily ordered within the same phase.
 * 启动过程始于最低的阶段值，终于最高的阶段值。
 * 关闭过程将应用相反的顺序。
 * 具有相同值的任何组件将在同一阶段内任意顺序。
 *
 * <p>Example: if component B depends on component A having already started, then
 * component A should have a lower phase value than component B. During the
 * shutdown process, component B would be stopped before component A.
 * 示例：如果组件B依赖已经启动的组件A，那么组件A的阶段值应低于组件B的。
 * 在关闭过程期间，组件B应该在组件A之前停止。
 *
 * <p>Any explicit "depends-on" relationship will take precedence over
 * the phase order such that the dependent bean always starts after its
 * dependency and always stops before its dependency.
 * 任何显式的"依赖"关系都将优先于阶段顺序，以便从属bean始终在其依赖之后启动，并始终在其依赖之前停止。
 *
 * <p>Any Lifecycle components within the context that do not also implement
 * SmartLifecycle will be treated as if they have a phase value of 0. That
 * way a SmartLifecycle implementation may start before those Lifecycle
 * components if it has a negative phase value, or it may start after
 * those components if it has a positive phase value.
 * 任何Lifecycle组件，未实现SmartLifecycle，将被视为相位值为0。
 *
 * <p>Note that, due to the auto-startup support in SmartLifecycle,
 * a SmartLifecycle bean instance will get initialized on startup of the
 * application context in any case. As a consequence, the bean definition
 * lazy-init flag has very limited actual effect on SmartLifecycle beans.
 * 注意：由于SmartLifecycle支持自动启动，SmartLifecycle组件实例会在应用上下文启动阶段就实例化完成。
 * 因此，bean定义的延迟初始化标识对SmartLifecycle组件的实际影响非常有限。
 *
 * @author Mark Fisher
 * @since 3.0
 * @see LifecycleProcessor
 * @see ConfigurableApplicationContext
 */
public interface SmartLifecycle extends Lifecycle, Phased {

	/**
	 * Returns {@code true} if this {@code Lifecycle} component should get
	 * started automatically by the container at the time that the containing
	 * {@link ApplicationContext} gets refreshed.
	 * 组件是否在应用上下文刷新时由容器自动启动。
	 * <p>A value of {@code false} indicates that the component is intended to
	 * be started through an explicit {@link #start()} call instead, analogous
	 * to a plain {@link Lifecycle} implementation.
	 * @see #start()
	 * @see #getPhase()
	 * @see LifecycleProcessor#onRefresh()
	 * @see ConfigurableApplicationContext#refresh()
	 */
	boolean isAutoStartup();

	/**
	 * Indicates that a Lifecycle component must stop if it is currently running.
	 * 表明生命周期组件必须停止，即使它当前正在运行。
	 * <p>The provided callback is used by the {@link LifecycleProcessor} to support
	 * an ordered, and potentially concurrent, shutdown of all components having a
	 * common shutdown order value. The callback <b>must</b> be executed after
	 * the {@code SmartLifecycle} component does indeed stop.
	 * 为组件生命周期处理器提供的回调函数。
	 * <p>The {@link LifecycleProcessor} will call <i>only</i> this variant of the
	 * {@code stop} method; i.e. {@link Lifecycle#stop()} will not be called for
	 * {@code SmartLifecycle} implementations unless explicitly delegated to within
	 * the implementation of this method.
	 * @see #stop()
	 * @see #getPhase()
	 */
	void stop(Runnable callback);

}
