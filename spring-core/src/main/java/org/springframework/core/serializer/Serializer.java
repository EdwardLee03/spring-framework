
package org.springframework.core.serializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A strategy interface for streaming an object to an OutputStream.
 * 将对象写入到输出流的策略接口。
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @since 3.0.5
 */
// 序列化器
public interface Serializer<T> {

	/**
	 * Write an object of type T to the given OutputStream.
	 * <p>Note: Implementations should not close the given OutputStream
	 * (or any decorators of that OutputStream) but rather leave this up
	 * to the caller.
	 * @param object the object to serialize
	 * @param outputStream the output stream
	 * @throws IOException in case of errors writing to the stream
	 */
    // 将类型为T的对象写入到给定的输出流
	void serialize(T object, OutputStream outputStream) throws IOException;

}
