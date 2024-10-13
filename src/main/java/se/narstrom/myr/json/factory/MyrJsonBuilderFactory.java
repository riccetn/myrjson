package se.narstrom.myr.json.factory;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
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
	public JsonArrayBuilder createArrayBuilder(final Collection<?> collection) {
		final JsonArrayBuilder builder = createArrayBuilder();
		for (final Object obj : collection) {
			final Object value;
			if (obj instanceof Optional<?> opt) {
				if (opt.isEmpty())
					continue;
				value = opt.get();
			} else {
				value = obj;
			}

			switch (value) {
				case JsonValue val -> builder.add(val);
				case String str -> builder.add(str);
				case Number num -> builder.add(provider.createValue(num));
				default -> throw new IllegalArgumentException();
			}
		}

		return builder;
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final JsonArray array) {
		final JsonArrayBuilder builder = createArrayBuilder();
		for (final JsonValue value : array) {
			builder.add(value);
		}
		return builder;
	}

	@Override
	public Map<String, ?> getConfigInUse() {
		return config;
	}

}
