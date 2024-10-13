package se.narstrom.myr.json.value;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

public final class MyrJsonObject extends AbstractMap<String, JsonValue> implements JsonObject {
	private final Map<String, JsonValue> map;

	public MyrJsonObject(final Map<String, JsonValue> map) {
		this.map = map;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.OBJECT;
	}

	@Override
	public JsonArray getJsonArray(final String name) {
		return (JsonArray) map.get(name);
	}

	@Override
	public JsonObject getJsonObject(final String name) {
		return (JsonObject) map.get(name);
	}

	@Override
	public JsonNumber getJsonNumber(final String name) {
		return (JsonNumber) map.get(name);
	}

	@Override
	public JsonString getJsonString(final String name) {
		return (JsonString) map.get(name);
	}

	@Override
	public String getString(final String name) {
		return getJsonString(name).getString();
	}

	@Override
	public String getString(final String name, final String defaultValue) {
		final JsonValue value = map.get(name);
		if (value == null || value.getValueType() != ValueType.STRING)
			return defaultValue;
		return ((JsonString) value).getString();
	}

	@Override
	public int getInt(final String name) {
		return getJsonNumber(name).intValue();
	}

	@Override
	public int getInt(final String name, final int defaultValue) {
		final JsonValue value = map.get(name);
		if (value == null || value.getValueType() != ValueType.NUMBER)
			return defaultValue;
		return ((JsonNumber) value).intValue();
	}

	@Override
	public boolean getBoolean(final String name) {
		final JsonValue value = map.get(name);
		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			throw new ClassCastException();
	}

	@Override
	public boolean getBoolean(final String name, final boolean defaultValue) {
		final JsonValue value = map.get(name);
		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			return defaultValue;
	}

	@Override
	public boolean isNull(final String name) {
		return map.get(name) == JsonValue.NULL;
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		return map.entrySet();
	}
}
