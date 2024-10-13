package se.narstrom.myr.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import se.narstrom.myr.json.value.MyrJsonObject;

public final class MyrJsonObjectBuilder implements JsonObjectBuilder {
	private final JsonProvider provider;

	private final Map<String, JsonValue> map = new LinkedHashMap<>();

	public MyrJsonObjectBuilder(final JsonProvider provider) {
		this.provider = provider;
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonValue value) {
		map.put(name, value);
		return this;
	}

	@Override
	public JsonObjectBuilder add(final String name, final String value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigInteger value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigDecimal value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final int value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final long value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final double value) {
		return add(name, provider.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final boolean value) {
		return add(name, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonObjectBuilder addNull(final String name) {
		return add(name, JsonValue.NULL);
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonObjectBuilder builder) {
		return add(name, builder.build());
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonArrayBuilder builder) {
		return add(name, builder.build());
	}

	@Override
	public JsonObject build() {
		return new MyrJsonObject(Collections.unmodifiableMap(map));
	}

}
