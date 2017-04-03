
package org.springframework.mock.http.client;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.util.Assert;

/**
 * Mock implementation of {@link ClientHttpResponse}.
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 */
// 客户端HTTP响应模拟
public class MockClientHttpResponse extends MockHttpInputMessage implements ClientHttpResponse {

    /**
     * HTTP状态
     */
	private final HttpStatus status;


	/**
	 * Constructor with response body as a byte array.
	 */
	public MockClientHttpResponse(byte[] body, HttpStatus statusCode) {
		super(body);
		Assert.notNull(statusCode, "HttpStatus is required");
		this.status = statusCode;
	}

	/**
	 * Constructor with response body as InputStream.
	 */
	public MockClientHttpResponse(InputStream body, HttpStatus statusCode) {
		super(body);
		Assert.notNull(statusCode, "HttpStatus is required");
		this.status = statusCode;
	}


	@Override
	public HttpStatus getStatusCode() throws IOException {
		return this.status;
	}

	@Override
	public int getRawStatusCode() throws IOException {
		return this.status.value();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.status.getReasonPhrase();
	}

	@Override
	public void close() {
		try {
			InputStream body = getBody();
			if (body != null) {
				body.close();
			}
		}
		catch (IOException ex) {
			// ignore
		}
	}

}
