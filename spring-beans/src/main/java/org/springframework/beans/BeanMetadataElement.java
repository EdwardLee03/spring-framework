
package org.springframework.beans;

/**
 * Interface to be implemented by bean metadata elements
 * that carry a configuration source object.
 *
 * @author Juergen Hoeller
 * @since 2.0
 */
// 核心接口 组件元数据元素
public interface BeanMetadataElement {

	/**
	 * Return the configuration source {@code Object} for this metadata element
	 * (may be {@code null}).
	 */
    // 核心方法 返回元数据元素的配置源对象/null
	Object getSource();

}
