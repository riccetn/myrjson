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

	private final Map<String, Object> map = new LinkedHashMap<>();

	public MyrJsonObjectBuilder(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigDecimal value) {
		return add(name, new MyrJsonNumber(value));
	}

	@Override
	public JsonObjectBuilder add(final String name, final BigInteger value) {
		return add(name, new MyrJsonNumber(new BigDecimal(value)));
	}

	@Override
	public JsonObjectBuilder add(final String name, final boolean value) {
		return add(name, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonObjectBuilder add(final String name, final double value) {
		return add(name, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonObjectBuilder add(final String name, final int value) {
		return add(name, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonArrayBuilder builder) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(builder);
		map.put(name, context.defaultBuilderFactory().createArrayBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonObjectBuilder builder) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(builder);
		map.put(name, context.defaultBuilderFactory().createObjectBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonObjectBuilder add(final String name, final JsonValue value) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(value);

		final Object objToAdd = switch (value.getValueType()) {
			case ARRAY -> context.defaultBuilderFactory().createArrayBuilder(value.asJsonArray());
			case OBJECT -> context.defaultBuilderFactory().createObjectBuilder(value.asJsonObject());
			default -> value;
		};

		switch (context.getKeyStrategy()) {
			case FIRST -> map.putIfAbsent(name, objToAdd);
			case LAST -> map.put(name, objToAdd);
			case NONE -> {
				if (map.putIfAbsent(name, objToAdd) != null)
					throw new JsonException("Duplicate key '" + name + "'");
			}
		}
		return this;
	}

	@Override
	public JsonObjectBuilder add(final String name, final long value) {
		return add(name, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonObjectBuilder add(final String name, final String value) {
		return add(name, new MyrJsonString(value));
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
		final Map<String, JsonValue> result = LinkedHashMap.newLinkedHashMap(map.size());
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			final JsonValue value = switch (entry.getValue()) {
				case JsonArrayBuilder builder -> builder.build();
				case JsonObjectBuilder builder -> builder.build();
				case JsonValue val -> val;
				default -> throw new AssertionError();
			};
			result.put(entry.getKey(), value);
		}
		final JsonObject obj = new MyrJsonObject(result);
		map.clear();
		return obj;
	}

	@Override
	public JsonObjectBuilder remove(final String name) {
		map.remove(Objects.requireNonNull(name));
		return this;
	}

}
