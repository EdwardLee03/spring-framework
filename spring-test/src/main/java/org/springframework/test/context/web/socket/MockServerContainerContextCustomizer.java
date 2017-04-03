
package org.springframework.test.context.web.socket;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.web.context.WebApplicationContext;

/**
 * {@link ContextCustomizer} that instantiates a new {@link MockServerContainer}
 * and stores it in the {@code ServletContext} under the attribute named
 * {@code "javax.websocket.server.ServerContainer"}.
 *
 * @author Sam Brannen
 * @since 4.3.1
 */
class MockServerContainerContextCustomizer implements ContextCustomizer {

	@Override
	public void customizeContext(ConfigurableApplicationContext context, MergedContextConfiguration mergedConfig) {
		if (context instanceof WebApplicationContext) { // Web应用上下文
			WebApplicationContext wac = (WebApplicationContext) context;
			wac.getServletContext().setAttribute("javax.websocket.server.ServerContainer", new MockServerContainer());
		}
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other != null && getClass() == other.getClass()));
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
