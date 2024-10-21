package se.narstrom.myr.json.patch;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

final class MutableJsonObject implements JsonObject, MutableJsonStructure {

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonValue get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBoolean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(String name, boolean defaultValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInt(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(String name, int defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JsonArray getJsonArray(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonNumber getJsonNumber(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject getJsonObject(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonString getJsonString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String name, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNull(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonValue put(String key, JsonValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends JsonValue> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JsonValue remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<JsonValue> values() {
		// TODO Auto-generated method stub
		return null;
	}

}
