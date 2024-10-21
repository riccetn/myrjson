package se.narstrom.myr.json.patch;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

final class MutableJsonArray extends AbstractList<JsonValue> implements JsonArray, MutableJsonStructure {
	private final List<JsonValue> list = new ArrayList<>();

	@Override
	public boolean add(final JsonValue value) {
		return list.add(value);
	}

	@Override
	public void add(final int index, final JsonValue value) {
		list.add(index, value);
	}

	@Override
	public JsonValue get(final int index) {
		return list.get(index);
	}

	@Override
	public boolean getBoolean(final int index) {
		return switch (list.get(index).getValueType()) {
			case TRUE -> true;
			case FALSE -> false;
			default -> throw new ClassCastException();
		};
	}

	@Override
	public boolean getBoolean(final int index, final boolean defaultValue) {
		if (index < 0 || index >= list.size())
			return defaultValue;

		return switch (list.get(index).getValueType()) {
			case TRUE -> true;
			case FALSE -> false;
			default -> defaultValue;
		};
	}

	@Override
	public int getInt(final int index) {
		return ((JsonNumber) list.get(index)).intValue();
	}

	@Override
	public int getInt(final int index, final int defaultValue) {
		if (index < 0 || index >= list.size())
			return defaultValue;
		return ((JsonNumber) list.get(index)).intValue();
	}

	@Override
	public JsonArray getJsonArray(final int index) {
		return list.get(index).asJsonArray();
	}

	@Override
	public JsonNumber getJsonNumber(final int index) {
		return (JsonNumber) list.get(index);
	}

	@Override
	public JsonObject getJsonObject(final int index) {
		return list.get(index).asJsonObject();
	}

	@Override
	public JsonString getJsonString(final int index) {
		return (JsonString) list.get(index);
	}

	@Override
	public String getString(final int index) {
		return ((JsonString) list.get(index)).getString();
	}

	@Override
	public String getString(final int index, final String defaultValue) {
		if (index < 0 || index >= list.size())
			return defaultValue;
		return ((JsonString) list.get(index)).getString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends JsonValue> List<T> getValuesAs(final Class<T> clazz) {
		return (List<T>) List.copyOf(list);
	}

	@Override
	public ValueType getValueType() {
		return ValueType.ARRAY;
	}

	@Override
	public boolean isNull(final int index) {
		return list.get(index) == JsonValue.NULL;
	}

	@Override
	public JsonValue set(final int index, final JsonValue value) {
		return list.set(index, value);
	}

	@Override
	public int size() {
		return list.size();
	}
}
