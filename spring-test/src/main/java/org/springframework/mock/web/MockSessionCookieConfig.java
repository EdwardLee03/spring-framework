
package org.springframework.mock.web;

import javax.servlet.SessionCookieConfig;

/**
 * Mock implementation of the {@link javax.servlet.SessionCookieConfig} interface.
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see javax.servlet.ServletContext#getSessionCookieConfig()
 */
// 会话Cookie配置模拟
public class MockSessionCookieConfig implements SessionCookieConfig {

	private String name;

	private String domain;

	private String path;

	private String comment;

    /**
     * 是否只读
     */
	private boolean httpOnly;

    /**
     * 是否安全
     */
	private boolean secure;

    /**
     * 失效时间
     */
	private int maxAge = -1;


	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@Override
	public String getDomain() {
		return this.domain;
	}

	@Override
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public String getComment() {
		return this.comment;
	}

	@Override
	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	@Override
	public boolean isHttpOnly() {
		return this.httpOnly;
	}

	@Override
	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public boolean isSecure() {
		return this.secure;
	}

	@Override
	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	@Override
	public int getMaxAge() {
		return this.maxAge;
	}

}
