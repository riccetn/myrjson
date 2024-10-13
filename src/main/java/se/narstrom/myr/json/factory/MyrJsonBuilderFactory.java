package se.narstrom.myr.json.factory;

import java.util.Map;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.spi.JsonProvider;
import se.narstrom.myr.json.builder.MyrJsonArrayBuilder;
import se.narstrom.myr.json.builder.MyrJsonObjectBuilder;

public final class MyrJsonBuilderFactory implements JsonBuilderFactory {

	private final JsonProvider provider;

	private final Map<String, Object> config;

	public MyrJsonBuilderFactory(final JsonProvider provider, final Map<String, ?> config) {
		this.provider = provider;
		this.config = Map.copyOf(config);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return new MyrJsonObjectBuilder(provider);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return new MyrJsonArrayBuilder(provider);
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return config;
	}

}
