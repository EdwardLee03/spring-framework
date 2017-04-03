
package org.springframework.mock.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@link HttpInputMessage}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
// HTTP输入消息模拟
public class MockHttpInputMessage implements HttpInputMessage {

	private final HttpHeaders headers = new HttpHeaders();

    /**
     * 输出体
     */
	private final InputStream body;


	public MockHttpInputMessage(byte[] contents) {
		this.body = (contents != null ? new ByteArrayInputStream(contents) : null);
	}

	public MockHttpInputMessage(InputStream body) {
		Assert.notNull(body, "InputStream must not be null");
		this.body = body;
	}


	@Override
	public HttpHeaders getHeaders() {
		return this.headers;
	}

    // 返回输入内容
	@Override
	public InputStream getBody() throws IOException {
		return this.body;
	}

}
