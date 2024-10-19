package se.narstrom.myr.json.value;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

public final class MyrJsonObject extends AbstractMap<String, JsonValue> implements JsonObject {
	private final Map<String, JsonValue> map;

	MyrJsonObject(final Map<String, JsonValue> map) {
		this.map = Collections.unmodifiableMap(new LinkedHashMap<>(map));
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		return map.entrySet();
	}

	@Override
	public boolean getBoolean(final String name) {
		Objects.requireNonNull(name);
		final JsonValue value = map.get(name);
		return switch (value) {
			case JsonValue val when val == JsonValue.TRUE -> true;
			case JsonValue val when val == JsonValue.FALSE -> false;
			case null -> throw new NullPointerException();
			default -> throw new ClassCastException();
		};
	}

	@Override
	public boolean getBoolean(final String name, final boolean defaultValue) {
		Objects.requireNonNull(name);
		final JsonValue value = map.get(name);
		return switch(value) {
			case JsonValue val when val == JsonValue.TRUE -> true;
			case JsonValue val when val == JsonValue.FALSE -> false;
			default -> defaultValue;
		};
	}

	@Override
	public int getInt(final String name) {
		Objects.requireNonNull(name);
		return getJsonNumber(name).intValue();
	}

	@Override
	public int getInt(final String name, final int defaultValue) {
		Objects.requireNonNull(name);
		final JsonValue value = map.get(name);
		if (value == null || value.getValueType() != ValueType.NUMBER)
			return defaultValue;
		return ((JsonNumber) value).intValue();
	}

	@Override
	public JsonArray getJsonArray(final String name) {
		Objects.requireNonNull(name);
		return (JsonArray) map.get(name);
	}

	@Override
	public JsonNumber getJsonNumber(final String name) {
		Objects.requireNonNull(name);
		return (JsonNumber) map.get(name);
	}

	@Override
	public JsonObject getJsonObject(final String name) {
		Objects.requireNonNull(name);
		return (JsonObject) map.get(name);
	}

	@Override
	public JsonString getJsonString(final String name) {
		Objects.requireNonNull(name);
		return (JsonString) map.get(name);
	}

	@Override
	public String getString(final String name) {
		Objects.requireNonNull(name);
		return getJsonString(name).getString();
	}

	@Override
	public String getString(final String name, final String defaultValue) {
		Objects.requireNonNull(name);
		final JsonValue value = map.get(name);
		if (value == null || value.getValueType() != ValueType.STRING)
			return defaultValue;
		return ((JsonString) value).getString();
	}

	@Override
	public ValueType getValueType() {
		return ValueType.OBJECT;
	}

	@Override
	public boolean isNull(final String name) {
		Objects.requireNonNull(name);
		final JsonValue value = map.get(name);
		if (value == null)
			throw new NullPointerException();
		return value == JsonValue.NULL;
	}
}
