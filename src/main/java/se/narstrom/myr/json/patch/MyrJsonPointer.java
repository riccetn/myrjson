package se.narstrom.myr.json.patch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonPointer;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.JsonValue.ValueType;
import se.narstrom.myr.json.MyrJsonContext;

public final class MyrJsonPointer implements JsonPointer {
	private static final Pattern PATTERN_INDEX = Pattern.compile("(0|[1-9][0-9]*)");

	private final MyrJsonContext context;

	private final List<String> path;

	public MyrJsonPointer(final String pointer, final MyrJsonContext context) {
		this.path = parse(pointer);
		this.context = context;
	}

	@Override
	public <T extends JsonStructure> T add(final T target, final JsonValue value) {
		if (path.isEmpty())
			return (T) value;
		return (T) addToStructure(target, 0, value);
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
	public <T extends JsonStructure> T remove(final T target) {
		if(path.isEmpty())
			throw new JsonException("Cannot remove root");
		return (T) removeFromStructure(target, 0);
	}

	@Override
	public <T extends JsonStructure> T replace(final T target, final JsonValue value) {
		if(path.isEmpty())
			return (T) value;
		return (T) replaceInStructure(target, 0, value);
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

	private JsonArray addToArray(final JsonArray target, final int pathIndex, final JsonValue value) {
		final JsonArrayBuilder builder = context.defaultBuilderFactory().createArrayBuilder(target);
		final String key = path.get(pathIndex);
	
		if (pathIndex == path.size() - 1 && Objects.equals(path.get(pathIndex), "-")) {
			builder.add(value);
			return builder.build();
		}
	
		if (!PATTERN_INDEX.matcher(key).matches())
			throw new JsonException("Not an array index");
		final int index = Integer.parseUnsignedInt(key);
	
		Objects.checkIndex(index, target.size()+1);
	
		if (pathIndex == path.size() - 1) {
			builder.add(index, value);
		} else {
			final JsonValue next = target.get(index);
			builder.set(index, addToStructure((JsonStructure) next, pathIndex + 1, value));
		}
		return builder.build();
	}

	private JsonObject addToObject(final JsonObject target, final int pathIndex, final JsonValue value) {
		final JsonObjectBuilder builder = context.defaultBuilderFactory().createObjectBuilder(target);
		final String key = path.get(pathIndex);

		if (pathIndex == path.size() - 1) {
			builder.add(key, value);
		} else {
			final JsonValue next = target.get(key);
			builder.add(key, addToStructure((JsonStructure) next, pathIndex + 1, value));
		}
		return builder.build();
	}

	private JsonStructure addToStructure(final JsonStructure target, final int pathIndex, final JsonValue value) {
		if(target == null)
			throw new JsonException("No item");
		return switch (target.getValueType()) {
			case ARRAY -> addToArray((JsonArray) target, pathIndex, value);
			case OBJECT -> addToObject((JsonObject) target, pathIndex, value);
			default -> throw new JsonException("Path: cannot find " + this + " in " + target);
		};
	}

	private JsonArray removeFromArray(final JsonArray target, final int pathIndex) {
		final JsonArrayBuilder builder = context.defaultBuilderFactory().createArrayBuilder(target);
		final String key = path.get(pathIndex);
	
		if (!PATTERN_INDEX.matcher(key).matches())
			throw new JsonException("Not an array index");
		final int index = Integer.parseUnsignedInt(key);
	
		if(index < 0 || index >= target.size())
			throw new JsonException("Index out of bounds");
	
		if (pathIndex == path.size() - 1) {
			builder.remove(index);
		} else {
			final JsonValue next = target.get(index);
			builder.set(index, removeFromStructure((JsonStructure) next, pathIndex + 1));
		}
		return builder.build();
	}

	private JsonObject removeFromObject(final JsonObject target, final int pathIndex) {
		final JsonObjectBuilder builder = context.defaultBuilderFactory().createObjectBuilder(target);
		final String key = path.get(pathIndex);
	
		if (pathIndex == path.size() - 1) {
			if(!target.containsKey(key))
				throw new JsonException("Removing on-existing mapping from object");
			builder.remove(key);
		} else {
			final JsonValue next = target.get(key);
			builder.add(key, removeFromStructure((JsonStructure) next, pathIndex + 1));
		}
		return builder.build();
	}

	private JsonStructure removeFromStructure(final JsonStructure target, final int pathIndex) {
		return switch(target.getValueType()) {
			case ARRAY -> removeFromArray((JsonArray) target, pathIndex);
			case OBJECT -> removeFromObject((JsonObject) target, pathIndex);
			default -> throw new JsonException("Not a structure");
		};
	}

	private JsonArray replaceInArray(final JsonArray target, final int pathIndex, final JsonValue value) {
		final JsonArrayBuilder builder = context.defaultBuilderFactory().createArrayBuilder(target);
		final String key = path.get(pathIndex);
	
		if (!PATTERN_INDEX.matcher(key).matches())
			throw new JsonException("Not an array index");
		final int index = Integer.parseUnsignedInt(key);
	
		if(index < 0 || index >= target.size())
			throw new JsonException("Out of index");
	
		if (pathIndex == path.size() - 1) {
			builder.set(index, value);
		} else {
			final JsonValue next = target.get(index);
			builder.set(index, replaceInStructure((JsonStructure) next, pathIndex + 1, value));
		}
		return builder.build();
	}

	private JsonObject replaceInObject(final JsonObject target, final int pathIndex, final JsonValue value) {
		final String key = path.get(pathIndex);

		if(!target.containsKey(key))
			throw new JsonException("Target does not exist");

		final JsonObjectBuilder builder = context.defaultBuilderFactory().createObjectBuilder(target);

		if (pathIndex == path.size() - 1) {
			builder.add(key, value);
		} else {
			final JsonValue next = target.get(key);
			builder.add(key, replaceInStructure((JsonStructure) next, pathIndex + 1, value));
		}
		return builder.build();
	}

	private JsonStructure replaceInStructure(final JsonStructure target, final int pathIndex, final JsonValue value) {
		if(target == null)
			throw new JsonException("No item");
		return switch (target.getValueType()) {
			case ARRAY -> replaceInArray((JsonArray) target, pathIndex, value);
			case OBJECT -> replaceInObject((JsonObject) target, pathIndex, value);
			default -> throw new JsonException("Path: cannot find " + this + " in " + target);
		};
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
					default -> sb.append('~').append(ch2);
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
