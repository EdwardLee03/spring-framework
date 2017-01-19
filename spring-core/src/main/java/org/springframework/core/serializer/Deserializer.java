
package org.springframework.core.serializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * A strategy interface for converting from data in an InputStream to an Object.
 * 将输入流中的数据转换为对象的策略接口。
 *
 * @author Gary Russell
 * @author Mark Fisher
 * @since 3.0.5
 */
// 反序列化器
public interface Deserializer<T> {

	/**
	 * Read (assemble) an object of type T from the given InputStream.
	 * <p>Note: Implementations should not close the given InputStream
	 * (or any decorators of that InputStream) but rather leave this up
	 * to the caller.
	 * @param inputStream the input stream
	 * @return the deserialized object
	 * @throws IOException in case of errors reading from the stream
	 */
    // 从给定的输入流读取(组装)类型T的对象
	T deserialize(InputStream inputStream) throws IOException;

}
