package se.narstrom.myr.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import se.narstrom.myr.json.value.MyrJsonArray;

public final class MyrJsonArrayBuilder implements JsonArrayBuilder {
	private final JsonProvider provider;

	private final List<JsonValue> list = new ArrayList<>();

	public MyrJsonArrayBuilder(final JsonProvider provider) {
		this.provider = provider;
	}

	@Override
	public JsonArrayBuilder add(final JsonValue value) {
		Objects.requireNonNull(value);
		list.add(value);
		return this;
	}

	@Override
	public JsonArrayBuilder add(final String value) {
		Objects.requireNonNull(value);
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final BigDecimal value) {
		Objects.requireNonNull(value);
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final BigInteger value) {
		Objects.requireNonNull(value);
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int value) {
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final long value) {
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final double value) {
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final boolean value) {
		return add(value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder addNull() {
		return add(JsonValue.NULL);
	}

	@Override
	public JsonArrayBuilder add(final JsonObjectBuilder builder) {
		return add(builder.build());
	}

	@Override
	public JsonArrayBuilder add(final JsonArrayBuilder builder) {
		return add(builder.build());
	}

	@Override
	public JsonArrayBuilder add(final int index, final JsonValue value) {
		Objects.checkIndex(index, list.size() + 1);
		Objects.requireNonNull(value);
		list.add(index, value);
		return this;
	}

	@Override
	public JsonArrayBuilder add(final int index, final String value) {
		Objects.requireNonNull(value);
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final BigDecimal value) {
		Objects.requireNonNull(value);
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final BigInteger value) {
		Objects.requireNonNull(value);
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final double value) {
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final long value) {
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final int value) {
		return add(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final int index, final boolean value) {
		return add(index, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder addNull(final int index) {
		return add(index, JsonValue.NULL);
	}

	@Override
	public JsonArrayBuilder add(int index, final JsonArrayBuilder builder) {
		return add(index, builder.build());
	}

	@Override
	public JsonArrayBuilder add(int index, final JsonObjectBuilder builder) {
		return add(index, builder.build());
	}

	@Override
	public JsonArrayBuilder addAll(final JsonArrayBuilder builder) {
		list.addAll(builder.build());
		return this;
	}

	@Override
	public JsonArrayBuilder remove(final int index) {
		Objects.checkIndex(index, list.size());
		list.remove(index);
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
	public JsonArrayBuilder set(final int index, final String value) {
		Objects.requireNonNull(value);
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final BigDecimal value) {
		Objects.requireNonNull(value);
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final BigInteger value) {
		Objects.requireNonNull(value);
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final double value) {
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final long value) {
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final int value) {
		return set(index, provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder set(final int index, final boolean value) {
		return set(index, value ? JsonValue.TRUE : JsonValue.FALSE);
	}

	@Override
	public JsonArrayBuilder setNull(final int index) {
		return set(index, JsonValue.NULL);
	}

	@Override
	public JsonArrayBuilder set(int index, final JsonArrayBuilder builder) {
		return set(index, builder.build());
	}

	@Override
	public JsonArrayBuilder set(int index, final JsonObjectBuilder builder) {
		return set(index, builder.build());
	}

	@Override
	public JsonArray build() {
		return new MyrJsonArray(list);
	}

}
