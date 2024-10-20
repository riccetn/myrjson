package se.narstrom.myr.json.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonObjectBuilder implements JsonObjectBuilder {
	private final MyrJsonContext context;

	private final Map<String, JsonValue> map = new LinkedHashMap<>();

	public MyrJsonObjectBuilder(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigDecimal value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigInteger value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final boolean value) {
		return add(name, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonObjectBuilder add(final String name, final double value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final int value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonArrayBuilder builder) {
		return add(name, builder.build());
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonObjectBuilder builder) {
		return add(name, builder.build());
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonValue value) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(value);

		switch (context.getKeyStrategy()) {
			case FIRST -> map.putIfAbsent(name, value);
			case LAST -> map.put(name, value);
			case NONE -> {
				if (map.putIfAbsent(name, value) != null)
					throw new JsonException("Duplicate key '" + name + "'");
			}
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(final String name, final long value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final String value) {
		return add(name, context.createValue(value));
	}

	@Override
	public JsonObjectBuilder addAll(final JsonObjectBuilder builder) {
		for (final Map.Entry<String, JsonValue> entry : builder.build().entrySet()) {
			add(entry.getKey(), entry.getValue());
		}
		return this;
	}

	@Override
	public JsonObjectBuilder addNull(final String name) {
		return add(name, JsonValue.NULL);
	}

	@Override
	public JsonObject build() {
		final JsonObject obj = new MyrJsonObject(map);
		map.clear();
		return obj;
	}

	@Override
	public JsonObjectBuilder remove(final String name) {
		map.remove(Objects.requireNonNull(name));
		return this;
	}

}
