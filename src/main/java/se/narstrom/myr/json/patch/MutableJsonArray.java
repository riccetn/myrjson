package se.narstrom.myr.json.patch;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

final class MutableJsonArray implements JsonArray, MutableJsonStructure {

	@Override
	public void add(int index, JsonValue element) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean add(JsonValue e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends JsonValue> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends JsonValue> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonValue get(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean getBoolean(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getBoolean(int index, boolean defaultValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(int index, int defaultValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public JsonArray getJsonArray(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonNumber getJsonNumber(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject getJsonObject(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonString getJsonString(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(int index, String defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JsonValue> List<T> getValuesAs(Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ValueType getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isNull(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator<JsonValue> iterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ListIterator<JsonValue> listIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListIterator<JsonValue> listIterator(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonValue remove(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonValue set(int index, JsonValue element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<JsonValue> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return null;
	}

}
