
package org.springframework.test.context.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext.HierarchyMode;
import org.springframework.test.context.CacheAwareContextLoaderDelegate;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.SmartContextLoader;
import org.springframework.util.Assert;

/**
 * Default implementation of the {@link CacheAwareContextLoaderDelegate} interface.
 *
 * <p>To use a static {@code DefaultContextCache}, invoke the
 * {@link #DefaultCacheAwareContextLoaderDelegate()} constructor; otherwise,
 * invoke the {@link #DefaultCacheAwareContextLoaderDelegate(ContextCache)}
 * and provide a custom {@link ContextCache} implementation.
 *
 * @author Sam Brannen
 * @since 4.1
 */
public class DefaultCacheAwareContextLoaderDelegate implements CacheAwareContextLoaderDelegate {

	private static final Log logger = LogFactory.getLog(DefaultCacheAwareContextLoaderDelegate.class);

	/**
	 * Default static cache of Spring application contexts.
	 */
	static final ContextCache defaultContextCache = new DefaultContextCache();

    /**
     * 上下文缓存
     */
	private final ContextCache contextCache;


	/**
	 * Construct a new {@code DefaultCacheAwareContextLoaderDelegate} using
	 * a static {@link DefaultContextCache}.
	 * <p>This default cache is static so that each context can be cached
	 * and reused for all subsequent tests that declare the same unique
	 * context configuration within the same JVM process.
	 * @see #DefaultCacheAwareContextLoaderDelegate(ContextCache)
	 */
	public DefaultCacheAwareContextLoaderDelegate() {
		this(defaultContextCache);
	}

	/**
	 * Construct a new {@code DefaultCacheAwareContextLoaderDelegate} using
	 * the supplied {@link ContextCache}.
	 * @see #DefaultCacheAwareContextLoaderDelegate()
	 */
	public DefaultCacheAwareContextLoaderDelegate(ContextCache contextCache) {
		Assert.notNull(contextCache, "ContextCache must not be null");
		this.contextCache = contextCache;
	}

	/**
	 * Get the {@link ContextCache} used by this context loader delegate.
	 */
	protected ContextCache getContextCache() {
		return this.contextCache;
	}

	/**
	 * Load the {@code ApplicationContext} for the supplied merged context configuration.
	 * <p>Supports both the {@link SmartContextLoader} and {@link ContextLoader} SPIs.
	 * @throws Exception if an error occurs while loading the application context
	 */
	protected ApplicationContext loadContextInternal(MergedContextConfiguration mergedContextConfiguration)
			throws Exception {
		// 上下文加载器
		ContextLoader contextLoader = mergedContextConfiguration.getContextLoader();
		Assert.notNull(contextLoader, "Cannot load an ApplicationContext with a NULL 'contextLoader'. " +
				"Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");

		ApplicationContext applicationContext;
        // 加载上下文
		if (contextLoader instanceof SmartContextLoader) {
			SmartContextLoader smartContextLoader = (SmartContextLoader) contextLoader;
			applicationContext = smartContextLoader.loadContext(mergedContextConfiguration);
		}
		else {
			String[] locations = mergedContextConfiguration.getLocations();
			Assert.notNull(locations, "Cannot load an ApplicationContext with a NULL 'locations' array. " +
					"Consider annotating your test class with @ContextConfiguration or @ContextHierarchy.");
			applicationContext = contextLoader.loadContext(locations);
		}

		return applicationContext;
	}

	@Override
	public ApplicationContext loadContext(MergedContextConfiguration mergedContextConfiguration) {
		synchronized (this.contextCache) {
            // 先从应用上下文缓存获取
			ApplicationContext context = this.contextCache.get(mergedContextConfiguration);
			if (context == null) {
				try {
                    // 加载应用上下文
					context = loadContextInternal(mergedContextConfiguration);
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("Storing ApplicationContext in cache under key [%s]",
								mergedContextConfiguration));
					}
					this.contextCache.put(mergedContextConfiguration, context);
				}
				catch (Exception ex) {
					throw new IllegalStateException("Failed to load ApplicationContext", ex);
				}
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug(String.format("Retrieved ApplicationContext from cache with key [%s]",
							mergedContextConfiguration));
				}
			}

			this.contextCache.logStatistics();

			return context;
		}
	}

	@Override
	public void closeContext(MergedContextConfiguration mergedContextConfiguration, HierarchyMode hierarchyMode) {
		synchronized (this.contextCache) {
            // 从应用上下文缓存移除
			this.contextCache.remove(mergedContextConfiguration, hierarchyMode);
		}
	}

}
