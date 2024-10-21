package se.narstrom.myr.json.value;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

public final class MyrJsonArray extends AbstractList<JsonValue> implements JsonArray {
	private JsonValue[] array;

	MyrJsonArray(final List<JsonValue> list) {
		this.array = list.toArray(JsonValue[]::new);
	}

	@Override
	public JsonValue get(final int index) {
		return array[index];
	}

	@Override
	public boolean getBoolean(final int index) {
		Objects.checkIndex(index, array.length);
		final JsonValue value = array[index];

		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			throw new ClassCastException();
	}

	@Override
	public boolean getBoolean(final int index, final boolean defaultValue) {
		if (index < 0 || index >= array.length)
			return defaultValue;

		final JsonValue value = array[index];

		if (value == JsonValue.TRUE)
			return true;
		else if (value == JsonValue.FALSE)
			return false;
		else
			return defaultValue;
	}

	@Override
	public int getInt(final int index) {
		return getJsonNumber(index).intValue();
	}

	@Override
	public int getInt(final int index, final int defaultValue) {
		if (index < 0 || index >= array.length)
			return defaultValue;

		final JsonValue value = array[index];

		if (value.getValueType() != ValueType.NUMBER)
			return defaultValue;

		return ((JsonNumber) value).intValue();
	}

	@Override
	public JsonArray getJsonArray(final int index) {
		Objects.checkIndex(index, array.length);
		return (JsonArray) array[index];
	}

	@Override
	public JsonNumber getJsonNumber(final int index) {
		Objects.checkIndex(index, array.length);
		return (JsonNumber) array[index];
	}

	@Override
	public JsonObject getJsonObject(final int index) {
		Objects.checkIndex(index, array.length);
		return (JsonObject) array[index];
	}

	@Override
	public JsonString getJsonString(final int index) {
		Objects.checkIndex(index, array.length);
		return (JsonString) array[index];
	}

	@Override
	public String getString(final int index) {
		return getJsonString(index).getString();
	}

	@Override
	public String getString(final int index, final String defaultValue) {
		if (index < 0 || index >= array.length)
			return defaultValue;

		final JsonValue value = array[index];

		if (value == null || value.getValueType() != ValueType.STRING)
			return defaultValue;
		return ((JsonString) value).getString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends JsonValue> List<T> getValuesAs(final Class<T> clazz) {
		return (List<T>) List.of(array);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.ARRAY;
	}

	@Override
	public boolean isNull(final int index) {
		Objects.checkIndex(index, array.length);
		return array[index] == JsonValue.NULL;
	}

	@Override
	public int size() {
		return array.length;
	}
}
