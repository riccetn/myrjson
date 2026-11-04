package se.narstrom.myr.json.benchmark;

import java.util.function.Supplier;

import jakarta.json.spi.JsonProvider;

/**
 * The JSON-P implementations under comparison. Providers are instantiated
 * directly so that both can live on the same classpath without relying on
 * {@link java.util.ServiceLoader} ordering.
 */
public enum Implementation {
	MYRJSON(se.narstrom.myr.json.MyrJsonProvider::new),
	PARSSON(org.eclipse.parsson.JsonProviderImpl::new);

	private final Supplier<JsonProvider> factory;

	Implementation(final Supplier<JsonProvider> factory) {
		this.factory = factory;
	}

	public JsonProvider newProvider() {
		return factory.get();
	}
}
