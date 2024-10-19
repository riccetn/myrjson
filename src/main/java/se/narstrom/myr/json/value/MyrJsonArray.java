package se.narstrom.myr.json.value;

import java.util.AbstractList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

public final class MyrJsonArray extends AbstractList<JsonValue> implements JsonArray {
	private List<JsonValue> list;

	MyrJsonArray(final List<JsonValue> list) {
		this.list = List.copyOf(list);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.ARRAY;
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public JsonObject getJsonObject(final int index) {
		return (JsonObject) list.get(index);
	}

	@Override
	public JsonArray getJsonArray(final int index) {
		return (JsonArray) list.get(index);
	}

	@Override
	public JsonNumber getJsonNumber(final int index) {
		return (JsonNumber) list.get(index);
	}

	@Override
	public JsonString getJsonString(final int index) {
		return (JsonString) list.get(index);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends JsonValue> List<T> getValuesAs(final Class<T> clazz) {
		return (List<T>) list;
	}

	@Override
	public String getString(final int index) {
		return getJsonString(index).getString();
	}

	@Override
	public String getString(final int index, final String defaultValue) {
		final JsonValue value = list.get(index);
		if (value == null || value.getValueType() != ValueType.STRING)
			return defaultValue;
		return ((JsonString) value).getString();
	}

	@Override
	public int getInt(final int index) {
		return getJsonNumber(index).intValue();
	}

	@Override
	public int getInt(final int index, final int defaultValue) {
		final JsonValue value = list.get(index);
		if (value == null || value.getValueType() != ValueType.NUMBER)
			return defaultValue;
		return ((JsonNumber) value).intValue();
	}

	@Override
	public boolean getBoolean(final int index) {
		final JsonValue value = list.get(index);
		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			throw new ClassCastException();
	}

	@Override
	public boolean getBoolean(final int index, final boolean defaultValue) {
		final JsonValue value = list.get(index);
		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			return defaultValue;
	}

	@Override
	public boolean isNull(final int index) {
		return list.get(index) == JsonValue.NULL;
	}

	@Override
	public JsonValue get(final int index) {
		return list.get(index);
	}
}
