
package org.springframework.test.web.servlet;

/**
 * Builds a {@link MockMvc} instance.
 *
 * <p>See static factory methods in
 * {@link org.springframework.test.web.servlet.setup.MockMvcBuilders MockMvcBuilders}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
// 核心接口 MVC模拟实例构建者
public interface MockMvcBuilder {

	/**
	 * Build a {@link MockMvc} instance.
	 */
	MockMvc build();

}
