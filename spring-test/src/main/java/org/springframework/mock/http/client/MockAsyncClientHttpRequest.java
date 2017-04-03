
package org.springframework.mock.http.client;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

/**
 * An extension of {@link MockClientHttpRequest} that also implements
 * {@link AsyncClientHttpRequest} by wrapping the response in a
 * {@link SettableListenableFuture}.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 */
// 异步客户端HTTP请求模拟
public class MockAsyncClientHttpRequest extends MockClientHttpRequest implements AsyncClientHttpRequest {

	public MockAsyncClientHttpRequest() {
	}

	public MockAsyncClientHttpRequest(HttpMethod httpMethod, URI uri) {
		super(httpMethod, uri);
	}


	// 异步地执行
	@Override
	public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
		// 可设置可监听的异步计算的结果
		SettableListenableFuture<ClientHttpResponse> future = new SettableListenableFuture<ClientHttpResponse>();
		future.set(execute()); // 设置执行结果
		return future;
	}

}
