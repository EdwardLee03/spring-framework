
package org.springframework.test.context.transaction;

import org.springframework.core.NamedInheritableThreadLocal;

/**
 * {@link InheritableThreadLocal}-based holder for the current {@link TransactionContext}.
 * 当前事务上下文({@link TransactionContext})
 * 基于可继承的线程本地变量({@link InheritableThreadLocal})持有者。
 *
 * @author Sam Brannen
 * @since 4.1
 */
class TransactionContextHolder {

    /**
     * 可继承的当前事务上下文
     */
	private static final ThreadLocal<TransactionContext> currentTransactionContext = new NamedInheritableThreadLocal<TransactionContext>(
		"Test Transaction Context");


	static TransactionContext getCurrentTransactionContext() {
		return currentTransactionContext.get();
	}

	static void setCurrentTransactionContext(TransactionContext transactionContext) {
		currentTransactionContext.set(transactionContext);
	}

	static TransactionContext removeCurrentTransactionContext() {
		synchronized (currentTransactionContext) { // 同步对象
			TransactionContext transactionContext = currentTransactionContext.get();
			currentTransactionContext.remove();
			return transactionContext;
		}
	}

}
