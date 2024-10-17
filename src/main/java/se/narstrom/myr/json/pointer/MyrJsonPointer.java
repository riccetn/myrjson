package se.narstrom.myr.json.pointer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;

public class MyrJsonPointer implements JsonPointer {
	private static final Pattern PATTERN_INDEX = Pattern.compile("(0|[1-9][0-9]*)");

	private final List<String> path;

	public MyrJsonPointer(final String pointer) {
		this.path = parse(pointer);
	}

	@Override
	public <T extends JsonStructure> T add(final T target, final JsonValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JsonStructure> T remove(final T target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends JsonStructure> T replace(final T target, final JsonValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsValue(final JsonStructure target) {
		Objects.requireNonNull(target);
		return resolve(target) != null;
	}

	@Override
	public JsonValue getValue(final JsonStructure target) {
		Objects.requireNonNull(target);
		final JsonValue value = resolve(target);
		if (value == null)
			throw new JsonException("Path: cannot find " + this + " in " + target);
		return value;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		for (final String key : path) {
			sb.append('/');
			for (int i = 0; i < key.length(); ++i) {
				final char ch = key.charAt(i);
				switch (ch) {
					case '~' -> sb.append("~0");
					case '/' -> sb.append("~1");
					default -> sb.append(ch);
				}
			}
		}

		return sb.toString();
	}

	private JsonValue resolve(final JsonStructure target) {
		JsonValue val = target;
		for (final String key : path) {
			if (val.getValueType() == ValueType.ARRAY) {
				final JsonArray array = (JsonArray) val;
				if (!PATTERN_INDEX.matcher(key).matches())
					return null;
				final int index = Integer.parseInt(key);
				if (index < 0 || index >= array.size())
					return null;
				val = array.get(index);
			} else if (val.getValueType() == ValueType.OBJECT) {
				final JsonObject object = (JsonObject) val;
				val = object.get(key);
				if (val == null)
					return null;
			} else {
				return null;
			}
		}
		return val;
	}

	private static List<String> parse(final String pointer) {
		if (pointer.isEmpty())
			return List.of();

		if (pointer.charAt(0) != '/')
			throw new JsonException("pointer most begin with a '/'");

		final List<String> result = new ArrayList<>();
		final StringBuilder sb = new StringBuilder();

		for (int i = 1; i < pointer.length(); ++i) {
			final char ch = pointer.charAt(i);

			if (ch == '~') {
				++i;
				if (i >= pointer.length())
					throw new JsonException("~ at end");

				final char ch2 = pointer.charAt(i);
				switch (ch2) {
					case '0' -> sb.append('~');
					case '1' -> sb.append('/');
					default -> throw new JsonException("Unknown escape sequence '~" + ch2 + "'");
				}
			} else if (ch == '/') {
				result.add(sb.toString());
				sb.delete(0, sb.length());
			} else {
				sb.append(ch);
			}
		}
		result.add(sb.toString());

		return Collections.unmodifiableList(result);
	}
}
