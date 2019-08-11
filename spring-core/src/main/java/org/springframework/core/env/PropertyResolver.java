
package org.springframework.core.env;

/**
 * Interface for resolving properties against any underlying source.
 * 属性解析器，解析任何基础源的属性集合。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see Environment
 * @see PropertySourcesPropertyResolver
 */
public interface PropertyResolver {

	/**
	 * Return whether the given property key is available for resolution,
	 * i.e. if the value for the given key is not {@code null}.
	 * 返回给定的属性键是否可用于解析。
	 */
	boolean containsProperty(String key);

	// 属性值

	/**
	 * Return the property value associated with the given key,
	 * or {@code null} if the key cannot be resolved.
	 * 返回与给定键关联的属性值。
	 * @param key the property name to resolve
	 * @see #getProperty(String, String)
	 * @see #getProperty(String, Class)
	 * @see #getRequiredProperty(String)
	 */
	String getProperty(String key);

	/**
	 * Return the property value associated with the given key, or
	 * {@code defaultValue} if the key cannot be resolved.
	 * 返回与给定键关联的属性值。
	 * 如果键无法被解析，则返回默认值。
	 * @param key the property name to resolve
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String)
	 * @see #getProperty(String, Class)
	 */
	String getProperty(String key, String defaultValue);

	/**
	 * Return the property value associated with the given key,
	 * or {@code null} if the key cannot be resolved.
	 * 返回与给定键关联的属性值。
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType);

	/**
	 * Return the property value associated with the given key,
	 * or {@code defaultValue} if the key cannot be resolved.
	 * 返回与给定键关联的属性值。
	 * 如果键无法被解析，则返回默认值。
	 * @param key the property name to resolve
	 * @param targetType the expected type of the property value
	 * @param defaultValue the default value to return if no value is found
	 * @see #getRequiredProperty(String, Class)
	 */
	<T> T getProperty(String key, Class<T> targetType, T defaultValue);

	/**
	 * Convert the property value associated with the given key to a {@code Class}
	 * of type {@code T} or {@code null} if the key cannot be resolved.
	 * @throws org.springframework.core.convert.ConversionException if class specified
	 * by property value cannot be found or loaded or if targetType is not assignable
	 * from class specified by property value
	 * @see #getProperty(String, Class)
	 * @deprecated as of 4.3, in favor of {@link #getProperty} with manual conversion
	 * to {@code Class} via the application's {@code ClassLoader}
	 */
	@Deprecated
	<T> Class<T> getPropertyAsClass(String key, Class<T> targetType);

	// 必填属性

	/**
	 * Return the property value associated with the given key (never {@code null}).
	 * 返回与给定键关联的属性值。
	 * @throws IllegalStateException if the key cannot be resolved
	 * @see #getRequiredProperty(String, Class)
	 */
	String getRequiredProperty(String key) throws IllegalStateException;

	/**
	 * Return the property value associated with the given key, converted to the given
	 * targetType (never {@code null}).
	 * 返回与给定键关联的属性值。
	 * @throws IllegalStateException if the given key cannot be resolved
	 */
	<T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException;

	// 解析${...}占位符
  /// SystemPropertyUtils

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value are ignored and passed through unchanged.
	 * 解析给定文本中的${...}占位符，将其替换为getProperty()解析的相应属性值。
	 * 没有默认值的无法解析的占位符将被忽略并传递不变。
	 * @param text the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String)
	 */
	String resolvePlaceholders(String text);

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with corresponding
	 * property values as resolved by {@link #getProperty}. Unresolvable placeholders with
	 * no default value will cause an IllegalArgumentException to be thrown.
	 * 解析给定文本中的${...}占位符，将其替换为getProperty()解析的相应属性值。
	 * 没有默认值的无法解析的占位符将导致抛出非法参数异常IllegalArgumentException。
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * or if any placeholders are unresolvable
	 * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String, boolean)
	 */
	String resolveRequiredPlaceholders(String text) throws IllegalArgumentException;

}
