package se.narstrom.myr.json.builder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

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
		list.add(value);
		return this;
	}

	@Override
	public JsonArrayBuilder add(final String value) {
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final BigDecimal value) {
		return add(provider.createValue(value));
	}

	@Override
	public JsonArrayBuilder add(final BigInteger value) {
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
	public JsonArray build() {
		return new MyrJsonArray(list);
	}

}
