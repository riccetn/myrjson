package se.narstrom.myr.json.value;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonArrayBuilder implements JsonArrayBuilder {
	private final MyrJsonContext context;

	private final List<Object> list = new ArrayList<>();

	public MyrJsonArrayBuilder(final MyrJsonContext context) {
		this.context = context;
	}

	@Override
	public JsonArrayBuilder add(final BigDecimal value) {
		Objects.requireNonNull(value);
		return add(new MyrJsonNumber(value));
	}

	@Override
	public JsonArrayBuilder add(final BigInteger value) {
		Objects.requireNonNull(value);
		return add(new MyrJsonNumber(new BigDecimal(value)));
	}

	@Override
	public JsonArrayBuilder add(final boolean value) {
		return add(value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder add(final double value) {
		return add(new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(final int value) {
		return add(new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(final int index, final BigDecimal value) {
		Objects.requireNonNull(value);
		return add(index, new MyrJsonNumber(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final BigInteger value) {
		Objects.requireNonNull(value);
		return add(index, new MyrJsonNumber(new BigDecimal(value)));
	}

	@Override
	public JsonArrayBuilder add(final int index, final boolean value) {
		return add(index, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder add(final int index, final double value) {
		return add(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(final int index, final int value) {
		return add(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(int index, final JsonArrayBuilder builder) {
		list.add(index, context.defaultBuilderFactory().createArrayBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder add(int index, final JsonObjectBuilder builder) {
		list.add(index, context.defaultBuilderFactory().createObjectBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder add(final int index, final JsonValue value) {
		Objects.checkIndex(index, list.size() + 1);
		Objects.requireNonNull(value);
		switch (value.getValueType()) {
			case ARRAY -> add(index, context.defaultBuilderFactory().createArrayBuilder(value.asJsonArray()));
			case OBJECT -> add(index, context.defaultBuilderFactory().createObjectBuilder(value.asJsonObject()));
			default -> list.add(index, value);
		}
		return this;
	}

	@Override
	public JsonArrayBuilder add(final int index, final long value) {
		return add(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(final int index, final String value) {
		Objects.requireNonNull(value);
		return add(index, new MyrJsonString(value));
	}

	@Override
	public JsonArrayBuilder add(final JsonArrayBuilder builder) {
		list.add(context.defaultBuilderFactory().createArrayBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder add(final JsonObjectBuilder builder) {
		list.add(context.defaultBuilderFactory().createObjectBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder add(final JsonValue value) {
		Objects.requireNonNull(value);
		list.add(value);
		return this;
	}

	@Override
	public JsonArrayBuilder add(final long value) {
		return add(new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder add(final String value) {
		Objects.requireNonNull(value);
		return add(new MyrJsonString(value));
	}

	@Override
	public JsonArrayBuilder addAll(final JsonArrayBuilder builder) {
		list.addAll(builder.build());
		return this;
	}

	@Override
	public JsonArrayBuilder addNull() {
		return add(JsonValue.NULL);
	}

	@Override
	public JsonArrayBuilder addNull(final int index) {
		return add(index, JsonValue.NULL);
	}

	@Override
	public JsonArray build() {
		final List<JsonValue> result = new ArrayList<>(list.size());
		for (final Object value : list) {
			switch (value) {
				case JsonArrayBuilder builder -> result.add(builder.build());
				case JsonObjectBuilder builder -> result.add(builder.build());
				case JsonValue val -> result.add(val);
				default -> throw new AssertionError();
			}
		}
		return new MyrJsonArray(result);
	}

	@Override
	public JsonArrayBuilder remove(final int index) {
		Objects.checkIndex(index, list.size());
		list.remove(index);
		return this;
	}

	@Override
	public JsonArrayBuilder set(final int index, final BigDecimal value) {
		Objects.requireNonNull(value);
		return set(index, new MyrJsonNumber(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final BigInteger value) {
		Objects.requireNonNull(value);
		return set(index, new MyrJsonNumber(new BigDecimal(value)));
	}

	@Override
	public JsonArrayBuilder set(final int index, final boolean value) {
		return set(index, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder set(final int index, final double value) {
		return set(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder set(final int index, final int value) {
		return set(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder set(int index, final JsonArrayBuilder builder) {
		list.set(index, context.defaultBuilderFactory().createArrayBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder set(int index, final JsonObjectBuilder builder) {
		list.set(index, context.defaultBuilderFactory().createObjectBuilder(builder.build()));
		return this;
	}

	@Override
	public JsonArrayBuilder set(final int index, final JsonValue value) {
		Objects.requireNonNull(value);
		Objects.checkIndex(index, list.size());
		list.set(index, value);
		return this;
	}

	@Override
	public JsonArrayBuilder set(final int index, final long value) {
		return set(index, new MyrJsonNumber(BigDecimal.valueOf(value)));
	}

	@Override
	public JsonArrayBuilder set(final int index, final String value) {
		Objects.requireNonNull(value);
		return set(index, new MyrJsonString(value));
	}

	@Override
	public JsonArrayBuilder setNull(final int index) {
		return set(index, JsonValue.NULL);
	}

}
