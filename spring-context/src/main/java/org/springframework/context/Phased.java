
package org.springframework.context;

/**
 * Interface for objects that may participate in a phased
 * process such as lifecycle management.
 * 参与分阶段过程的对象。
 *
 * @author Mark Fisher
 * @since 3.0
 * @see SmartLifecycle
 */
public interface Phased {

	/**
	 * Return the phase value of this object.
	 * 返回这个对象的相位值。
	 */
	int getPhase();

}
