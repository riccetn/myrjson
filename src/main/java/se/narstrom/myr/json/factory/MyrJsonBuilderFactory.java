package se.narstrom.myr.json.factory;

import java.util.Collection;
import java.util.Map;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonBuilderFactory;
import jakarta.json.JsonConfig;
import jakarta.json.JsonConfig.KeyStrategy;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import se.narstrom.myr.json.builder.MyrJsonArrayBuilder;
import se.narstrom.myr.json.builder.MyrJsonObjectBuilder;

public final class MyrJsonBuilderFactory implements JsonBuilderFactory {

	private final JsonProvider provider;

	private final KeyStrategy keyStrategy;

	public MyrJsonBuilderFactory(final JsonProvider provider, final Map<String, ?> config) {
		this.provider = provider;
		this.keyStrategy = ((Map<String, KeyStrategy>) config).get(JsonConfig.KEY_STRATEGY);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder() {
		return new MyrJsonObjectBuilder(provider, (keyStrategy != null) ? keyStrategy : KeyStrategy.LAST);
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final JsonObject object) {
		final JsonObjectBuilder builder = createObjectBuilder();
		for (final Map.Entry<String, JsonValue> entry : object.entrySet()) {
			builder.add(entry.getKey(), entry.getValue());
		}
		return builder;
	}

	@Override
	public JsonObjectBuilder createObjectBuilder(final Map<String, Object> map) {
		final JsonObjectBuilder builder = createObjectBuilder();
		for (final Map.Entry<String, Object> entry : map.entrySet()) {
			builder.add(entry.getKey(), createJsonValue(entry.getValue()));
		}
		return builder;
	}

	@Override
	public JsonArrayBuilder createArrayBuilder() {
		return new MyrJsonArrayBuilder(provider);
	}

	@Override
	public JsonArrayBuilder createArrayBuilder(final Collection<?> collection) {
		final JsonArrayBuilder builder = createArrayBuilder();
		for (final Object obj : collection) {
			builder.add(createJsonValue(obj));
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
		if (keyStrategy != null)
			return Map.of(JsonConfig.KEY_STRATEGY, keyStrategy);
		else
			return Map.of();
	}

	private JsonValue createJsonValue(final Object obj) {
		return switch (obj) {
			case JsonValue val -> val;
			case String str -> provider.createValue(str);
			case Number num -> provider.createValue(num);
			default -> throw new IllegalArgumentException();
		};
	}
}
