
package org.springframework.test.web.servlet;

import java.io.IOException;
import java.util.concurrent.Callable;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;

/**
 * A sub-class of {@code DispatcherServlet} that saves the result in an
 * {@link MvcResult}. The {@code MvcResult} instance is expected to be available
 * as the request attribute {@link MockMvc#MVC_RESULT_ATTRIBUTE}.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
// 核心类 测试分发程序
@SuppressWarnings("serial")
final class TestDispatcherServlet extends DispatcherServlet {

	private static final String KEY = TestDispatcherServlet.class.getName() + ".interceptor";


	/**
	 * Create a new instance with the given web application context.
	 */
	public TestDispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
	}


	// 核心实现 执行请求
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        // 注册异步执行结果拦截器
		registerAsyncResultInterceptors(request);
		super.service(request, response);
	}

	// 注册异步结果拦截器列表
	private void registerAsyncResultInterceptors(final HttpServletRequest request) {
        // 异步请求管理者
		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        // 注册调用拦截器
		asyncManager.registerCallableInterceptor(KEY, new CallableProcessingInterceptorAdapter() {
			@Override
			public <T> void postProcess(NativeWebRequest r, Callable<T> task, Object value) throws Exception {
				getMvcResult(request).setAsyncResult(value);
			}
		});
        // 注册结果拦截器
		asyncManager.registerDeferredResultInterceptor(KEY, new DeferredResultProcessingInterceptorAdapter() {
			@Override
			public <T> void postProcess(NativeWebRequest r, DeferredResult<T> result, Object value) throws Exception {
				getMvcResult(request).setAsyncResult(value);
			}
		});
	}

	protected DefaultMvcResult getMvcResult(ServletRequest request) {
		return (DefaultMvcResult) request.getAttribute(MockMvc.MVC_RESULT_ATTRIBUTE);
	}

	// 核心实现 处理程序执行链
	@Override
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        // 处理执行链
		HandlerExecutionChain chain = super.getHandler(request);
		if (chain != null) {
			DefaultMvcResult mvcResult = getMvcResult(request);
			mvcResult.setHandler(chain.getHandler());
			mvcResult.setInterceptors(chain.getInterceptors());
		}
		return chain;
	}

	// 核心实现 提交响应
	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		DefaultMvcResult mvcResult = getMvcResult(request);
		mvcResult.setModelAndView(mv);
		super.render(mv, request, response);
	}

	// 核心实现 处理执行异常
	@Override
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) throws Exception {

		ModelAndView mav = super.processHandlerException(request, response, handler, ex);

		// We got this far, exception was processed..
		DefaultMvcResult mvcResult = getMvcResult(request);
		mvcResult.setResolvedException(ex);
		mvcResult.setModelAndView(mav);

		return mav;
	}

}
