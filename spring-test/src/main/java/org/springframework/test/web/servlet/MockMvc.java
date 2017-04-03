
package org.springframework.test.web.servlet;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;

import org.springframework.beans.Mergeable;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * <strong>Main entry point for server-side Spring MVC test support.</strong>
 *
 * <h3>Example</h3>
 *
 * <pre class="code">
 * import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
 * import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 * import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
 *
 * // ...
 *
 * WebApplicationContext wac = ...;
 *
 * MockMvc mockMvc = webAppContextSetup(wac).build();
 *
 * mockMvc.perform(get("/form"))
 *     .andExpect(status().isOk())
 *     .andExpect(content().mimeType("text/html"))
 *     .andExpect(forwardedUrl("/WEB-INF/layouts/main.jsp"));
 * </pre>
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @author Sam Brannen
 * @since 3.2
 */
// 核心类 MVC模拟
public final class MockMvc {

	static String MVC_RESULT_ATTRIBUTE = MockMvc.class.getName().concat(".MVC_RESULT_ATTRIBUTE");

    /**
     * 分发程序
     */
	private final TestDispatcherServlet servlet;

    /**
     * 过滤器列表
     */
	private final Filter[] filters;

    /**
     * 服务端程序执行上下文
     */
	private final ServletContext servletContext;

    /**
     * 请求构建者
     */
	private RequestBuilder defaultRequestBuilder;

    /**
     * 结果匹配程序列表
     */
	private List<ResultMatcher> defaultResultMatchers = new ArrayList<ResultMatcher>();

    /**
     * 结果处理程序列表
     */
	private List<ResultHandler> defaultResultHandlers = new ArrayList<ResultHandler>();


	/**
	 * Private constructor, not for direct instantiation.
	 * @see org.springframework.test.web.servlet.setup.MockMvcBuilders
	 */
	MockMvc(TestDispatcherServlet servlet, Filter[] filters, ServletContext servletContext) {
		Assert.notNull(servlet, "DispatcherServlet is required");
		Assert.notNull(filters, "filters cannot be null");
		Assert.noNullElements(filters, "filters cannot contain null values");
		Assert.notNull(servletContext, "A ServletContext is required");

		this.servlet = servlet;
		this.filters = filters;
		this.servletContext = servletContext;
	}

	/**
	 * A default request builder merged into every performed request.
	 * @see org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder#defaultRequest(RequestBuilder)
	 */
	void setDefaultRequest(RequestBuilder requestBuilder) {
		this.defaultRequestBuilder = requestBuilder;
	}

	/**
	 * Expectations to assert after every performed request.
	 * @see org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder#alwaysExpect(ResultMatcher)
	 */
	void setGlobalResultMatchers(List<ResultMatcher> resultMatchers) {
		Assert.notNull(resultMatchers, "resultMatchers is required");
		this.defaultResultMatchers = resultMatchers;
	}

	/**
	 * General actions to apply after every performed request.
	 * @see org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder#alwaysDo(ResultHandler)
	 */
	void setGlobalResultHandlers(List<ResultHandler> resultHandlers) {
		Assert.notNull(resultHandlers, "resultHandlers is required");
		this.defaultResultHandlers = resultHandlers;
	}

	/**
	 * Perform a request and return a type that allows chaining further
	 * actions, such as asserting expectations, on the result.
	 *
	 * @param requestBuilder used to prepare the request to execute;
	 * see static factory methods in
	 * {@link org.springframework.test.web.servlet.request.MockMvcRequestBuilders}
	 *
	 * @return an instance of {@link ResultActions}; never {@code null}
	 *
	 * @see org.springframework.test.web.servlet.request.MockMvcRequestBuilders
	 * @see org.springframework.test.web.servlet.result.MockMvcResultMatchers
	 */
    // 核心实现 执行请求
	public ResultActions perform(RequestBuilder requestBuilder) throws Exception {
		if (this.defaultRequestBuilder != null) {
			if (requestBuilder instanceof Mergeable) {
				requestBuilder = (RequestBuilder) ((Mergeable) requestBuilder).merge(this.defaultRequestBuilder);
			}
		}

		// HTTP请求、响应
		MockHttpServletRequest request = requestBuilder.buildRequest(this.servletContext);
		MockHttpServletResponse response = new MockHttpServletResponse();

		if (requestBuilder instanceof SmartRequestBuilder) {
			request = ((SmartRequestBuilder) requestBuilder).postProcessRequest(request);
		}

        // 执行结果
		final MvcResult mvcResult = new DefaultMvcResult(request, response);
		request.setAttribute(MVC_RESULT_ATTRIBUTE, mvcResult);

		RequestAttributes previousAttributes = RequestContextHolder.getRequestAttributes();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));

        // 过滤器链
		MockFilterChain filterChain = new MockFilterChain(this.servlet, this.filters);
		filterChain.doFilter(request, response);

        // 异步请求
		if (DispatcherType.ASYNC.equals(request.getDispatcherType()) &&
				request.getAsyncContext() != null & !request.isAsyncStarted()) {

			request.getAsyncContext().complete();
		}

        // 应用结果行为
		applyDefaultResultActions(mvcResult);

		RequestContextHolder.setRequestAttributes(previousAttributes);

		return new ResultActions() { // 请求执行结果行为

			@Override
			public ResultActions andExpect(ResultMatcher matcher) throws Exception {
                // 匹配执行结果
				matcher.match(mvcResult);
				return this;
			}

			@Override
			public ResultActions andDo(ResultHandler handler) throws Exception {
                // 处理执行结果
				handler.handle(mvcResult);
				return this;
			}

			@Override
			public MvcResult andReturn() {
				return mvcResult;
			}

		};
	}

    // 应用结果行为
	private void applyDefaultResultActions(MvcResult mvcResult) throws Exception {
        // 结果匹配程序
		for (ResultMatcher matcher : this.defaultResultMatchers) {
			matcher.match(mvcResult);
		}

        // 结果处理程序
		for (ResultHandler handler : this.defaultResultHandlers) {
			handler.handle(mvcResult);
		}
	}

}
