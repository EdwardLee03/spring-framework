
package org.springframework.mock.web;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.util.Assert;

/**
 * Implementation of the {@link javax.servlet.FilterConfig} interface which
 * simply passes the call through to a given Filter/FilterChain combination
 * (indicating the next Filter in the chain along with the FilterChain that it is
 * supposed to work on) or to a given Servlet (indicating the end of the chain).
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @see javax.servlet.Filter
 * @see javax.servlet.Servlet
 * @see MockFilterChain
 */
// 通过的过滤器链式实现
public class PassThroughFilterChain implements FilterChain {

    /**
     * 当前过滤器
     */
	private Filter filter;

    /**
     * 下一个过滤器链
     */
	private FilterChain nextFilterChain;

    /**
     * 服务端程序
     */
	private Servlet servlet;


	/**
	 * Create a new PassThroughFilterChain that delegates to the given Filter,
	 * calling it with the given FilterChain.
	 * @param filter the Filter to delegate to
	 * @param nextFilterChain the FilterChain to use for that next Filter
	 */
	public PassThroughFilterChain(Filter filter, FilterChain nextFilterChain) {
		Assert.notNull(filter, "Filter must not be null");
		Assert.notNull(nextFilterChain, "'FilterChain must not be null");
		this.filter = filter;
		this.nextFilterChain = nextFilterChain;
	}

	/**
	 * Create a new PassThroughFilterChain that delegates to the given Servlet.
	 * @param servlet the Servlet to delegate to
	 */
	public PassThroughFilterChain(Servlet servlet) {
		Assert.notNull(servlet, "Servlet must not be null");
		this.servlet = servlet;
	}


	/**
	 * Pass the call on to the Filter/Servlet.
	 */
    // 通过过滤器或服务端程序的调用
	@Override
	public void doFilter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		if (this.filter != null) {
            // 过滤器链式过滤执行
			this.filter.doFilter(request, response, this.nextFilterChain);
		}
		else {
            // 处理请求程序
			this.servlet.service(request, response);
		}
	}

}
