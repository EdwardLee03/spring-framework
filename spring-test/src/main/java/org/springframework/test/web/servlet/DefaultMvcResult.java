
package org.springframework.test.web.servlet;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * A simple implementation of {@link MvcResult} with setters.
 *
 * @author Rossen Stoyanchev
 * @author Rob Winch
 * @since 3.2
 */
class DefaultMvcResult implements MvcResult {

	private static final Object RESULT_NONE = new Object();


	private final MockHttpServletRequest mockRequest;

	private final MockHttpServletResponse mockResponse;

	private Object handler;

	private HandlerInterceptor[] interceptors;

	private ModelAndView modelAndView;

	private Exception resolvedException;

	/**
	 * 异步执行结果（原子引用）
	 */
	private final AtomicReference<Object> asyncResult = new AtomicReference<Object>(RESULT_NONE);


	/**
	 * Create a new instance with the given request and response.
	 */
	public DefaultMvcResult(MockHttpServletRequest request, MockHttpServletResponse response) {
		this.mockRequest = request;
		this.mockResponse = response;
	}


	@Override
	public MockHttpServletRequest getRequest() {
		return this.mockRequest;
	}

	@Override
	public MockHttpServletResponse getResponse() {
		return this.mockResponse;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
	}

	@Override
	public Object getHandler() {
		return this.handler;
	}

	public void setInterceptors(HandlerInterceptor... interceptors) {
		this.interceptors = interceptors;
	}

	@Override
	public HandlerInterceptor[] getInterceptors() {
		return this.interceptors;
	}

	public void setResolvedException(Exception resolvedException) {
		this.resolvedException = resolvedException;
	}

	@Override
	public Exception getResolvedException() {
		return this.resolvedException;
	}

	public void setModelAndView(ModelAndView mav) {
		this.modelAndView = mav;
	}

	@Override
	public ModelAndView getModelAndView() {
		return this.modelAndView;
	}

	@Override
	public FlashMap getFlashMap() {
		return RequestContextUtils.getOutputFlashMap(this.mockRequest);
	}

	public void setAsyncResult(Object asyncResult) {
		this.asyncResult.set(asyncResult);
	}

	@Override
	public Object getAsyncResult() {
		return getAsyncResult(-1);
	}

	@Override
	public Object getAsyncResult(long timeToWait) {
		if (this.mockRequest.getAsyncContext() != null) {
			timeToWait = (timeToWait == -1 ? this.mockRequest.getAsyncContext().getTimeout() : timeToWait);
		}

		if (timeToWait > 0) {
			long endTime = System.currentTimeMillis() + timeToWait;
			while (System.currentTimeMillis() < endTime && this.asyncResult.get() == RESULT_NONE) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException ex) {
					throw new IllegalStateException("Interrupted while waiting for " +
							"async result to be set for handler [" + this.handler + "]", ex);
				}
			}
		}

        // 获取异步执行结果
		Object result = this.asyncResult.get();
		if (result == RESULT_NONE) {
			throw new IllegalStateException("Async result for handler [" + this.handler + "] " +
					"was not set during the specified timeToWait=" + timeToWait);
		}
		return result;
	}

}
